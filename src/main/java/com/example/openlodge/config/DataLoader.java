package com.example.openlodge.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.openlodge.model.Servicio;
import com.example.openlodge.repository.ServicioRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final ServicioRepository servicioRepository;

    @Autowired
    public DataLoader(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    /**
     * Este método se ejecuta automáticamente
     * una vez que la aplicación ha arrancado
     */
    @Override
    public void run(String... args) throws Exception {
        // 1. Verificamos si la tabla de servicios ya tiene datos
        if (servicioRepository.count() == 0) {
            System.out.println("Cargando servicios iniciales en la BD...");

            // 2. Creamos los objetos de Servicio
            Servicio wifi = new Servicio(null, "WIFI", 1000.0, null);
            Servicio pileta = new Servicio(null, "Pileta", 10000.0, null);
            Servicio parrilla = new Servicio(null, "Parrilla", 5000.0, null);
            Servicio cochera = new Servicio(null, "Cochera", 3000.0, null);
            Servicio ac = new Servicio(null, "Aire Acondicionado", 7000.0, null);

            // 3. Los guardamos todos en la base de datos
            servicioRepository.saveAll(List.of(wifi, pileta, parrilla, cochera, ac));

            System.out.println("Servicios cargados exitosamente.");
        } else {
            System.out.println("La tabla de servicios ya tiene datos, no se cargó nada.");
        }
    }
}
