package com.example.openlodge.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.openlodge.model.Propiedad;
import com.example.openlodge.model.Servicio;
import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.PropiedadRepository;
import com.example.openlodge.repository.ReservaRepository;
import com.example.openlodge.repository.ServicioRepository;
import com.example.openlodge.repository.UsuarioRepository;

@Service
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final ReservaRepository reservaRepository;

    @Autowired
    public PropiedadService(PropiedadRepository propiedadRepository, UsuarioRepository usuarioRepository,
            ServicioRepository servicioRepository, ReservaRepository reservaRepository) {
        this.propiedadRepository = propiedadRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.reservaRepository = reservaRepository;
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

    @Transactional
    public Propiedad crearPropiedad(Propiedad propiedad, String emailUsuarioLogueado) {
        Optional<Usuario> anfitrionOptional = usuarioRepository.findByEmail(emailUsuarioLogueado);

        // Verificamos si el anfitrión existe
        if (anfitrionOptional.isEmpty()) {
            throw new EntityNotFoundException("Anfitrión no encontrado con email: " + emailUsuarioLogueado);
        }

        // Si existe, lo seteamos en el objeto propiedad
        propiedad.setAnfitrion(anfitrionOptional.get());

        long count = propiedadRepository.count();
        long nextImageIndex = count + 1;
        String newImageUrl = String.format("/img/propiedades/%d/IMG1.jpg", nextImageIndex);
        propiedad.setImagenPrincipalUrl(newImageUrl);

        // Guardamos la propiedad (ya vinculada) en la BD
        return propiedadRepository.save(propiedad);
    }

    @Transactional
    public Propiedad agregarServicioAPropiedad(Long propiedadId, Long servicioId, String emailUsuarioLogueado) {

        Propiedad propiedad = propiedadRepository.findById(propiedadId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con ID: " + propiedadId));

        validarPropietario(propiedad, emailUsuarioLogueado);

        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + servicioId));

        propiedad.getServicios().add(servicio);
        return propiedadRepository.save(propiedad);
    }

    private void validarPropietario(Propiedad propiedad, String emailUsuarioLogueado) {
        String emailDelPropietario = propiedad.getAnfitrion().getEmail().trim().toLowerCase();
        String emailLogueado = emailUsuarioLogueado.trim().toLowerCase();

        if (!emailDelPropietario.equals(emailLogueado)) {
            throw new AccessDeniedException("No tienes permiso para modificar esta propiedad.");
        }
    }

    public List<Propiedad> obtenerPropiedadesPorEmailAnfitrion(String emailUsuarioLogueado) {

        Usuario anfitrion = usuarioRepository.findByEmail(emailUsuarioLogueado)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario anfitrión no encontrado con email: " + emailUsuarioLogueado));

        return propiedadRepository.findByAnfitrionId(anfitrion.getId());
    }

    /**
     * Borra una propiedad, validando que el usuario sea el dueño
     */
    @Transactional
    public void borrarPropiedad(Long propiedadId, String emailUsuarioLogueado) {

        // 1. Buscamos la propiedad
        Propiedad propiedad = propiedadRepository.findById(propiedadId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con ID: " + propiedadId));

        // 2. Validamos que el usuario logueado es el dueño
        validarPropietario(propiedad, emailUsuarioLogueado);

        //  3.  Comprobamos si hay reservas asociadas
        if (!reservaRepository.findByPropiedadId(propiedadId).isEmpty()) {
            // Si la lista NO está vacía, lanzamos un error
            throw new IllegalStateException("No se puede borrar la propiedad porque tiene reservas asociadas.");
        }

        // 4. Borramos la propiedad
        propiedadRepository.delete(propiedad);
    }

    /**
     * Actualiza una propiedad existente.
     */
    @Transactional
    public Propiedad actualizarPropiedad(Long propiedadId, Propiedad datosNuevos, String emailUsuarioLogueado) {

        // 1. Encontrar la propiedad existente en la BD
        Propiedad propiedadExistente = propiedadRepository.findById(propiedadId)
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con ID: " + propiedadId));

        // 2. Validar que el usuario logueado es el dueño
        validarPropietario(propiedadExistente, emailUsuarioLogueado);

        // 3. Actualizar los campos simples
        propiedadExistente.setTitulo(datosNuevos.getTitulo());
        propiedadExistente.setDescripcion(datosNuevos.getDescripcion());
        propiedadExistente.setDireccion(datosNuevos.getDireccion());
        propiedadExistente.setPrecioPorNoche(datosNuevos.getPrecioPorNoche());
        propiedadExistente.setNumeroHuespedes(datosNuevos.getNumeroHuespedes());
        propiedadExistente.setImagenPrincipalUrl(datosNuevos.getImagenPrincipalUrl());

        // 4. Actualizar los servicios (esta es la parte compleja)
        if (datosNuevos.getServicios() != null) {
            // Obtenemos los IDs de los servicios que envió el frontend
            Set<Long> idsServiciosNuevos = datosNuevos.getServicios().stream()
                    .map(Servicio::getId)
                    .collect(Collectors.toSet());

            // Buscamos las entidades 'Servicio' completas en la BD
            Set<Servicio> serviciosCompletos = new HashSet<>(servicioRepository.findAllById(idsServiciosNuevos));

            // Reemplazamos la lista de servicios antigua por la nueva
            propiedadExistente.setServicios(serviciosCompletos);
        }

        // 5. Guardamos la entidad actualizada
        return propiedadRepository.save(propiedadExistente);
    }
}