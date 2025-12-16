package es.uco.pw.demo.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.repository.ReservaRepository;

@RestController
@RequestMapping(path="/api/reservas", produces="application/json")
public class ReservaRestController {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    private static final double PRECIO_POR_PERSONA_EVENTO = 40.0;

    // --- GET & POST  ---

    @GetMapping
    public ResponseEntity<List<Reserva>> getAllReservas() {
        return new ResponseEntity<>(reservaRepository.findAllReservas(), HttpStatus.OK);
    }

    @GetMapping(params = "fecha")
    public ResponseEntity<List<Reserva>> getReservasFuturas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return new ResponseEntity<>(reservaRepository.findReservasFuturas(fecha), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable int id) {
        Reserva reserva = reservaRepository.findReservaById(id);
        return (reserva != null) ? new ResponseEntity<>(reserva, HttpStatus.OK) 
                                 : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Reserva> createReserva(@RequestBody Reserva reserva) {
        // Validar disponibilidad simple
        if (reservaRepository.isReservada(reserva.getMatricula(), reserva.getFecha())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        
        boolean created = reservaRepository.addReserva(reserva);
        return created ? new ResponseEntity<>(reserva, HttpStatus.CREATED) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- PATCH & DELETE ---

    
    @PatchMapping("/{id}/fecha")
    public ResponseEntity<Reserva> updateFecha(@PathVariable int id, @RequestBody Reserva request) {
        Reserva reserva = reservaRepository.findReservaById(id);
        if (reserva == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        LocalDate nuevaFecha = request.getFecha();
        if (nuevaFecha == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Validaciones: Fecha futura y posterior a la original
        if (!nuevaFecha.isAfter(LocalDate.now())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // El enunciado dice "otra posterior", asumimos posterior a la actual de la reserva
        if (!nuevaFecha.isAfter(reserva.getFecha())) {
        }

        // Disponibilidad
        if (reservaRepository.isReservada(reserva.getMatricula(), nuevaFecha) || 
            reservaRepository.isAlquilada(reserva.getMatricula(), nuevaFecha)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Ocupada
        }

        reserva.setFecha(nuevaFecha);
        reservaRepository.updateReserva(reserva);
        return new ResponseEntity<>(reserva, HttpStatus.OK);
    }

    /**
     * Modificar detalles (Proposito, Plazas) (PATCH).
     * Recalcula precio si cambian las plazas.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Reserva> updateDetalles(@PathVariable int id, @RequestBody Reserva updates) {
        Reserva reserva = reservaRepository.findReservaById(id);
        if (reserva == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // 1. Actualizar Propósito
        if (updates.getProposito() != null) {
            reserva.setProposito(updates.getProposito());
        }

        // 2. Actualizar Plazas
        if (updates.getPlazasReservadas() > 0) {
            Embarcacion barco = embarcacionRepository.findEmbarcacionByMatricula(reserva.getMatricula());
            // Validar capacidad (Plazas + 1 patrón <= Capacidad)
            if (updates.getPlazasReservadas() + 1 > barco.getNumPlazas()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Excede capacidad
            }
            
            reserva.setPlazasReservadas(updates.getPlazasReservadas());
            // Actualizar precio
            reserva.setPrecioTotal(updates.getPlazasReservadas() * PRECIO_POR_PERSONA_EVENTO);
        }

        reservaRepository.updateReserva(reserva);
        return new ResponseEntity<>(reserva, HttpStatus.OK);
    }

    /**
     * Cancelar reserva futura (DELETE).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable int id) {
        Reserva reserva = reservaRepository.findReservaById(id);
        if (reserva == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Validar que no haya pasado
        if (!reserva.getFecha().isAfter(LocalDate.now())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        boolean deleted = reservaRepository.deleteReserva(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}