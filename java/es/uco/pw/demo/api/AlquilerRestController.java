package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.domain.AlquilerSocio;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import es.uco.pw.demo.model.repository.AlquilerSocioRepository;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.repository.SocioRepository;

@RestController
@RequestMapping(path="/api/alquileres", produces="application/json")
public class AlquilerRestController {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private AlquilerSocioRepository alquilerSocioRepository;
    @Autowired
    private EmbarcacionRepository embarcacionRepository;
    @Autowired
    private SocioRepository socioRepository;

    private static final double PRECIO_POR_PERSONA_Y_DIA = 20.0;

    // GET & POST

    @GetMapping
    public ResponseEntity<List<Alquiler>> getAllAlquileres() {
        List<Alquiler> alquileres = alquilerRepository.findAllAlquileres();
        return (alquileres != null && !alquileres.isEmpty()) 
                ? new ResponseEntity<>(alquileres, HttpStatus.OK) 
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Listar futuros
    @GetMapping(params = "fecha")
    public ResponseEntity<List<Alquiler>> getAlquileresFuturos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Alquiler> lista = alquilerRepository.findAlquileresFuturos(fecha);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alquiler> getAlquilerById(@PathVariable int id) {
        Alquiler alquiler = alquilerRepository.findAlquilerById(id);
        return (alquiler != null) ? new ResponseEntity<>(alquiler, HttpStatus.OK) 
                                  : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Alquiler> createAlquiler(@RequestBody Alquiler alquiler) {
        // Validaciones básicas antes de insertar (opcionalmente aquí o en servicio)
        boolean created = alquilerRepository.addAlquiler(alquiler);
        return created ? new ResponseEntity<>(alquiler, HttpStatus.CREATED) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- PATCH & DELETE ---

    @PatchMapping("/{id}/socios")
    public ResponseEntity<Alquiler> addPasajero(@PathVariable int id, @RequestBody AlquilerSocio request) {
        // 1. Obtener Alquiler y validar fecha futura
        Alquiler alquiler = alquilerRepository.findAlquilerById(id);
        if (alquiler == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // No se puede editar un alquiler pasado o en curso
        }

        // 2. Verificar Socio
        Socio socio = socioRepository.findSocioByDni(request.getDniSocio());
        if (socio == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // 3. Verificar Capacidad Embarcación
        Embarcacion barco = embarcacionRepository.findEmbarcacionByMatricula(alquiler.getMatricula());
        if (alquiler.getPlazasReservadas() >= barco.getNumPlazas()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Barco lleno
        }

        // 4. Añadir pasajero
        boolean linked = alquilerSocioRepository.addAlquilerSocio(new AlquilerSocio(id, request.getDniSocio()));
        if (!linked) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        // 5. Actualizar Alquiler (Plazas y Precio)
        int nuevasPlazas = alquiler.getPlazasReservadas() + 1;
        long dias = ChronoUnit.DAYS.between(alquiler.getFechaInicio(), alquiler.getFechaFin()) + 1;
        double nuevoPrecio = nuevasPlazas * dias * PRECIO_POR_PERSONA_Y_DIA;

        alquiler.setPlazasReservadas(nuevasPlazas);
        alquiler.setPrecioTotal(nuevoPrecio);
        
        alquilerRepository.updateAlquilerPlazasPrecio(id, nuevasPlazas, nuevoPrecio);

        return new ResponseEntity<>(alquiler, HttpStatus.OK);
    }

    
    @DeleteMapping("/{id}/socios/{dni}")
    public ResponseEntity<Alquiler> removePasajero(@PathVariable int id, @PathVariable String dni) {
        // 1. Obtener Alquiler
        Alquiler alquiler = alquilerRepository.findAlquilerById(id);
        if (alquiler == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // 2. Validar fecha futura
        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // 3. Eliminar pasajero
        boolean deleted = alquilerSocioRepository.deleteAlquilerSocio(id, dni);
        if (!deleted) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // El socio no estaba en el alquiler

        // 4. Actualizar Alquiler
        int nuevasPlazas = alquiler.getPlazasReservadas() - 1;
        if (nuevasPlazas < 1) nuevasPlazas = 1; // Mínimo siempre queda el patrón/titular teóricamente
        
        long dias = ChronoUnit.DAYS.between(alquiler.getFechaInicio(), alquiler.getFechaFin()) + 1;
        double nuevoPrecio = nuevasPlazas * dias * PRECIO_POR_PERSONA_Y_DIA;

        alquiler.setPlazasReservadas(nuevasPlazas);
        alquiler.setPrecioTotal(nuevoPrecio);

        alquilerRepository.updateAlquilerPlazasPrecio(id, nuevasPlazas, nuevoPrecio);

        return new ResponseEntity<>(alquiler, HttpStatus.OK);
    }

    /**
     * Cancelar alquiler no realizado (DELETE).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlquiler(@PathVariable int id) {
        Alquiler alquiler = alquilerRepository.findAlquilerById(id);
        if (alquiler == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Validar que no haya pasado
        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Ya empezó o terminó
        }

        boolean deleted = alquilerRepository.deleteAlquiler(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}