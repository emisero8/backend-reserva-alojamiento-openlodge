package com.example.openlodge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.openlodge.model.Propiedad;
import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.PropiedadRepository;
import com.example.openlodge.repository.UsuarioRepository;

@Service
public class PropiedadService {
    // 1. Inyectamos ambos repositorios
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PropiedadService(PropiedadRepository propiedadRepository, UsuarioRepository usuarioRepository) {
        this.propiedadRepository = propiedadRepository;
        this.usuarioRepository = usuarioRepository;
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

    // (Aquí irían métodos como actualizarPropiedad, borrarPropiedad, etc.)
}
