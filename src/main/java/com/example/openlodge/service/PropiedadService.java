package com.example.openlodge.service;

import java.util.List;
import java.util.Optional;

// ¡NUEVO IMPORT!
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
// ¡IMPORT CORREGIDO! (El de Spring es mejor aquí)
import org.springframework.transaction.annotation.Transactional;

import com.example.openlodge.model.Propiedad;
import com.example.openlodge.model.Servicio;
import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.PropiedadRepository;
import com.example.openlodge.repository.ServicioRepository;
import com.example.openlodge.repository.UsuarioRepository;

@Service
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    @Autowired
    public PropiedadService(PropiedadRepository propiedadRepository, UsuarioRepository usuarioRepository,
            ServicioRepository servicioRepository) {
        this.propiedadRepository = propiedadRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
    }

    // ... (obtenerTodasLasPropiedades y obtenerPropiedadPorId quedan igual) ...
    public List<Propiedad> obtenerTodasLasPropiedades() {
        return propiedadRepository.findAll();
    }

    public Optional<Propiedad> obtenerPropiedadPorId(Long id) {
        return propiedadRepository.findById(id);
    }

    public List<Propiedad> obtenerPropiedadesPorAnfitrion(Long anfitrionId) {
        return propiedadRepository.findByAnfitrionId(anfitrionId);
    }

    public Propiedad crearPropiedad(Propiedad propiedad, Long anfitrionId) {
        Optional<Usuario> anfitrionOptional = usuarioRepository.findById(anfitrionId);

        if (anfitrionOptional.isEmpty()) {
            // ¡CAMBIO AQUÍ!
            throw new EntityNotFoundException("Anfitrión no encontrado con ID: " + anfitrionId);
        }
        propiedad.setAnfitrion(anfitrionOptional.get());
        return propiedadRepository.save(propiedad);
    }

    @Transactional
    public Propiedad agregarServicioAPropiedad(Long propiedadId, Long servicioId, String emailUsuarioLogueado) {

        Propiedad propiedad = propiedadRepository.findById(propiedadId)
                // ¡CAMBIO AQUÍ!
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con ID: " + propiedadId));

        validarPropietario(propiedad, emailUsuarioLogueado);

        Servicio servicio = servicioRepository.findById(servicioId)
                // ¡CAMBIO AQUÍ!
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + servicioId));

        propiedad.getServicios().add(servicio);
        return propiedadRepository.save(propiedad);
    }

    private void validarPropietario(Propiedad propiedad, String emailUsuarioLogueado) {
        if (!propiedad.getAnfitrion().getEmail().equals(emailUsuarioLogueado)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta propiedad.");
        }
    }

    public List<Propiedad> obtenerPropiedadesPorEmailAnfitrion(String emailUsuarioLogueado) {

        Usuario anfitrion = usuarioRepository.findByEmail(emailUsuarioLogueado)
                // ¡CAMBIO AQUÍ!
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario anfitrión no encontrado con email: " + emailUsuarioLogueado));

        return propiedadRepository.findByAnfitrionId(anfitrion.getId());
    }
}