package es.uco.pw.demo;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.domain.AlquilerSocio;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.domain.TipoInscripcion;

public class DemoAPI2 {

    public static void main(String[] args) {
        // Configuración para soportar PATCH
        RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        String baseURL = "http://localhost:8080/api";
        
        Integer idInscripcionGenerado = null;
        Integer idAlquilerGenerado = null;
        Integer idReservaGenerada = null;

        // DATOS
        String DNI_SOCIO = "12345678Z";   // Creado en Demo 1
        String DNI_PATRON = "87654321X";  // Creado en Demo 1
        String MATRICULA = "MAT050";      // Creado en Demo 1
        
        // DATOS EXTRA PARA PRUEBAS (Pasajero adicional)
        String DNI_PASAJERO = "11223344A"; 

        System.out.println("\n=============================================");
        System.out.println("   DEMO API 2: OPERACIONES SEMANA 2");
        System.out.println("=============================================\n");

        // --- CREACIÓN DE INSCRIPCIÓN Y ALQUILER ---
        
        // 1.1 Inscripción
        System.out.println("--> 1. Creando Inscripción...");
        try {
            Inscripcion insc = new Inscripcion(LocalDate.now(), TipoInscripcion.INDIVIDUAL, 350.0, DNI_SOCIO);
            ResponseEntity<Inscripcion> response = rest.postForEntity(baseURL + "/inscripciones", insc, Inscripcion.class);
            if (response.getBody() != null) {
                idInscripcionGenerado = response.getBody().getId();
                System.out.println("   [OK] Inscripción creada ID: " + idInscripcionGenerado);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("   [INFO] Error inscripción: " + e.getStatusCode());
        }

        // 1.2 Alquiler
        if (idInscripcionGenerado != null) {
            System.out.println("\n--> 2. Creando Alquiler...");
            try {
                Alquiler alquiler = new Alquiler(
                    idInscripcionGenerado, 
                    MATRICULA, 
                    LocalDate.now().plusDays(5), // Fecha futura
                    LocalDate.now().plusDays(7), 
                    4, 
                    400.0
                );
                ResponseEntity<Alquiler> response = rest.postForEntity(baseURL + "/alquileres", alquiler, Alquiler.class);
                if (response.getBody() != null) {
                    idAlquilerGenerado = response.getBody().getId();
                    System.out.println("   [OK] Alquiler creado ID: " + idAlquilerGenerado);
                }
            } catch (HttpClientErrorException e) { System.out.println("   [ERROR] Alquiler: " + e.getStatusCode()); }
        }

        // 1.3 Reserva
        System.out.println("\n--> 3. Creando Reserva...");
        try {
            Reserva reserva = new Reserva(
                DNI_SOCIO, 
                MATRICULA, 
                LocalDate.now().plusDays(20), 
                5, 
                "Prácticas de navegación avanzada",
                150.0
            );
            ResponseEntity<Reserva> response = rest.postForEntity(baseURL + "/reservas", reserva, Reserva.class);
            if (response.getBody() != null) {
                idReservaGenerada = response.getBody().getId();
                System.out.println("   [OK] Reserva creada ID: " + idReservaGenerada);
            }
        } catch (HttpClientErrorException e) { 
            System.out.println("   [INFO] Fallo reserva (Posiblemente falta patrón): " + e.getStatusCode()); 
        }

        // --- PASO 2: MODIFICACIONES ---
        
        System.out.println("\n--- FASE DE ACTUALIZACIONES (PATCH) ---");

        // 2.1 Vincular Patrón a Barco
        try {
            System.out.println("--> Vinculando Patrón (" + DNI_PATRON + ") a Barco (" + MATRICULA + ")...");
            Embarcacion updateBarco = new Embarcacion();
            updateBarco.setDniPatron(DNI_PATRON); // Usamos el objeto para enviar solo este campo
            
            rest.patchForObject(baseURL + "/embarcaciones/" + MATRICULA, updateBarco, Embarcacion.class);
            System.out.println("   [OK] Patrón asignado correctamente.");
        } catch (Exception e) { System.out.println("   [WARN] " + e.getMessage()); }

        // 2.2 Actualizar Inscripción a Familiar
        if (idInscripcionGenerado != null) {
            try {
                System.out.println("--> Actualizando Inscripción a FAMILIAR...");
                Inscripcion updateInsc = new Inscripcion();
                updateInsc.setTipo(TipoInscripcion.FAMILIAR);
                updateInsc.setCuotaActual(500.0);
                
                rest.put(baseURL + "/inscripciones/" + idInscripcionGenerado, updateInsc);
                System.out.println("   [OK] Inscripción actualizada.");
            } catch (Exception e) { System.out.println("   [WARN] " + e.getMessage()); }
        }

        // 2.3 Añadir Socio Pasajero
        if (idAlquilerGenerado != null) {
            try {
                System.out.println("--> Añadiendo pasajero extra al alquiler...");
                // Creamos el pasajero primero
                Socio pasajero = new Socio(DNI_PASAJERO, "Luis", "Ruiz", LocalDate.of(1990,1,1), "C/Sol", LocalDate.now(), false, false);
                try { rest.postForEntity(baseURL + "/socios", pasajero, Socio.class); } catch (Exception e) {}

                AlquilerSocio nuevoPasajero = new AlquilerSocio(0, DNI_PASAJERO);
                rest.patchForObject(baseURL + "/alquileres/" + idAlquilerGenerado + "/socios", nuevoPasajero, Alquiler.class);
                System.out.println("   [OK] Pasajero añadido y precio recalculado.");
            } catch (Exception e) { System.out.println("   [WARN] " + e.getMessage()); }
        }

        // --- PASO 3: BORRADOS (DELETE) ---
        
        System.out.println("\n--- FASE DE BORRADOS ---");

        // 3.1 Restricción de borrado de Patrón
        try {
            System.out.println("--> Intentando borrar Patrón con barco asignado...");
            rest.delete(baseURL + "/patrones/" + DNI_PATRON);
            System.out.println("   [FAIL] Se borró el patrón (ERROR).");
        } catch (HttpClientErrorException e) {
            System.out.println("   [OK] Bloqueado correctamente (" + e.getStatusCode() + ")");
        }

        // 3.2 Cancelar Reserva
        if (idReservaGenerada != null) {
            try {
                System.out.println("--> Cancelando Reserva futura...");
                rest.delete(baseURL + "/reservas/" + idReservaGenerada);
                System.out.println("   [OK] Reserva cancelada.");
            } catch (Exception e) { System.out.println("   [WARN] " + e.getMessage()); }
        }

        System.out.println("\n=== FIN DEMO 2 ===");
    }
}