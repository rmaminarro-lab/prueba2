package es.uco.pw.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.TipoEmbarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.repository.PatronRepository;

@RestController
@RequestMapping(path="/api/embarcaciones", produces="application/json")
public class EmbarcacionRestController {

    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    @Autowired
    private PatronRepository patronRepository;

    // --- GET & POST (Semana 1) ---

    // Obtener lista completa
    @GetMapping
    public ResponseEntity<List<Embarcacion>> getAllEmbarcaciones() {
        List<Embarcacion> flota = embarcacionRepository.findAllEmbarcaciones();
        if (flota != null && !flota.isEmpty()) {
            return new ResponseEntity<>(flota, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    // Obtener lista por tipo
    @GetMapping(params="tipo")
    public ResponseEntity<List<Embarcacion>> getEmbarcacionesByTipo(@RequestParam String tipo) {
        try {
            TipoEmbarcacion tipoEnum = TipoEmbarcacion.valueOf(tipo.toUpperCase());
            List<Embarcacion> flota = embarcacionRepository.findEmbarcacionByTipo(tipoEnum);
            return new ResponseEntity<>(flota, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Crear nueva embarcación
    @PostMapping(consumes="application/json")
    public ResponseEntity<Embarcacion> createEmbarcacion(@RequestBody Embarcacion embarcacion) {
        if (embarcacionRepository.findEmbarcacionByMatricula(embarcacion.getMatricula()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        boolean created = embarcacionRepository.addEmbarcacion(embarcacion);
        if (created) {
            return new ResponseEntity<>(embarcacion, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- PATCH & DELETE  ---

    /**
     * Actualizar información, Vincular o Desvincular Patrón (PATCH).
     */
    @PatchMapping("/{matricula}")
    public ResponseEntity<Embarcacion> updateEmbarcacion(@PathVariable String matricula, @RequestBody Embarcacion updates) {
        // 1. Recuperar
        Embarcacion barco = embarcacionRepository.findEmbarcacionByMatricula(matricula);
        if (barco == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 2. Actualizar campos (Nombre, Tipo, Plazas, Dimensiones)
        if (updates.getNombre() != null) barco.setNombre(updates.getNombre());
        if (updates.getTipo() != null) barco.setTipo(updates.getTipo());
        if (updates.getNumPlazas() > 0) barco.setNumPlazas(updates.getNumPlazas());
        if (updates.getDimensiones() != null) barco.setDimensiones(updates.getDimensiones());

        // 3. Gestión del Patrón (Vinculación/Desvinculación)
        if (updates.getDniPatron() != null) {
            if (updates.getDniPatron().isEmpty()) {
                // Desvinculación explícita (cadena vacía)
                barco.setDniPatron(null);
            } else {
                // Vinculación: Verificar que el patrón existe
                if (patronRepository.findPatronByDni(updates.getDniPatron()) != null) {
                    barco.setDniPatron(updates.getDniPatron());
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Patrón no encontrado
                }
            }
        }

        // 4. Guardar
        boolean success = embarcacionRepository.updateEmbarcacion(barco);
        if (success) {
            return new ResponseEntity<>(barco, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Eliminar embarcación si no tiene alquileres ni reservas (DELETE).
     */
    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> deleteEmbarcacion(@PathVariable String matricula) {
        if (embarcacionRepository.findEmbarcacionByMatricula(matricula) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Validaciones estrictas
        int alquileres = embarcacionRepository.countAlquileres(matricula);
        int reservas = embarcacionRepository.countReservas(matricula);

        if (alquileres > 0 || reservas > 0) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409: Está en uso
        }

        boolean deleted = embarcacionRepository.deleteEmbarcacion(matricula);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}