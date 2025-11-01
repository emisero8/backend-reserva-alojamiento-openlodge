package com.example.openlodge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.openlodge.model.Propiedad;
import com.example.openlodge.model.Servicio;
import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.PropiedadRepository;
import com.example.openlodge.repository.ServicioRepository;
import com.example.openlodge.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class PropiedadService {
    // 1. Inyectamos ambos repositorios
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;

    @Autowired
    public PropiedadService(PropiedadRepository propiedadRepository, UsuarioRepository usuarioRepository, ServicioRepository servicioRepository) {
        this.propiedadRepository = propiedadRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
    }

    // --- Métodos de Lógica de Negocio ---

    /**
     * Obtiene todas las propiedades.
     */
    public List<Propiedad> obtenerTodasLasPropiedades() {
        return propiedadRepository.findAll();
    }

    /**
     * Obtiene una propiedad específica por su ID.
     */
    public Optional<Propiedad> obtenerPropiedadPorId(Long id) {
        return propiedadRepository.findById(id);
    }

    /**
     * Obtiene todas las propiedades de un anfitrión específico.
     * (Este método usa el que creamos en el PropiedadRepository)
     */
    public List<Propiedad> obtenerPropiedadesPorAnfitrion(Long anfitrionId) {
        return propiedadRepository.findByAnfitrionId(anfitrionId);
    }

    /**
     * Crea una nueva propiedad y la asigna a un anfitrión.
     * Esta es la lógica de negocio clave.
     */
    public Propiedad crearPropiedad(Propiedad propiedad, Long anfitrionId) {

        // 1. Buscamos al usuario (anfitrión) que va a ser el dueño
        Optional<Usuario> anfitrionOptional = usuarioRepository.findById(anfitrionId);

        // 2. Verificamos si el anfitrión existe
        if (anfitrionOptional.isEmpty()) {
            // Si no existe, lanzamos un error.
            // (La API capturará este error y devolverá un "404 Not Found")
            throw new RuntimeException("Anfitrión no encontrado con ID: " + anfitrionId);
        }

        // 3. Si existe, lo "seteamos" en el objeto propiedad
        propiedad.setAnfitrion(anfitrionOptional.get());

        // 4. Guardamos la propiedad (ya vinculada) en la BD
        return propiedadRepository.save(propiedad);
    }

    /**
     * Agrega un servicio existente a una propiedad existente.
     */
    @Transactional // ⬅️ Asegura que la operación se complete (o falle) toda junta
    public Propiedad agregarServicioAPropiedad(Long propiedadId, Long servicioId, String emailUsuarioLogueado) {
        
        // 1. Buscamos la propiedad
        Propiedad propiedad = propiedadRepository.findById(propiedadId)
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada con ID: " + propiedadId));
        //1.1 Validar que el usuario sea el propietario de la propiedad
        validarPropietario(propiedad, emailUsuarioLogueado);

        // 2. Buscamos el servicio
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + servicioId));

        // 3. Añadimos el servicio al Set de la propiedad
        //    (Como es un Set, si ya existe, no hace nada)
        propiedad.getServicios().add(servicio);

        // 4. Guardamos la propiedad actualizada
        return propiedadRepository.save(propiedad);
    }

    /**
     * Comprueba si el email del anfitrión de la propiedad
     * coincide con el email del usuario logueado.
     */
    private void validarPropietario(Propiedad propiedad, String emailUsuarioLogueado) {
        if (!propiedad.getAnfitrion().getEmail().equals(emailUsuarioLogueado)) {
            // Si no coinciden, lanzamos un error de Acceso Denegado.
            // (Spring lo convertirá en un 403 Forbidden)
            throw new AccessDeniedException("No tienes permiso para modificar esta propiedad.");
        }
    }
    // NOTA: Deberíamos añadir esta llamada a validarPropietario()
    // en futuros métodos como:
    // - public Propiedad actualizarPropiedad(Long propiedadId, Propiedad datos, String email)
    // - public void borrarPropiedad(Long propiedadId, String email)
}
