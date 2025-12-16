package es.uco.pw.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;

@RestController
@RequestMapping(path="/api/patrones", produces="application/json")
public class PatronRestController {

    @Autowired
    private PatronRepository patronRepository;

    // Obtener lista

    @GetMapping
    public ResponseEntity<List<Patron>> getAllPatrones() {
        List<Patron> patrones = patronRepository.findAllPatrones();
        return (patrones != null && !patrones.isEmpty()) 
                ? new ResponseEntity<>(patrones, HttpStatus.OK) 
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Crear nuevo patrón
    
    @PostMapping(consumes="application/json")
    public ResponseEntity<Patron> createPatron(@RequestBody Patron patron) {
        if (patronRepository.findPatronByDni(patron.getDni()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); 
        }

        boolean created = patronRepository.addPatron(patron);
        return created ? new ResponseEntity<>(patron, HttpStatus.CREATED) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Actualizar campos del patrón excepto el DNI (PATCH).

    @PatchMapping("/{dni}")
    public ResponseEntity<Patron> updatePatron(@PathVariable String dni, @RequestBody Patron updates) {
        Patron patronActual = patronRepository.findPatronByDni(dni);
        if (patronActual == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (updates.getNombre() != null) patronActual.setNombre(updates.getNombre());
        if (updates.getApellidos() != null) patronActual.setApellidos(updates.getApellidos());
        if (updates.getFechaNacimiento() != null) patronActual.setFechaNacimiento(updates.getFechaNacimiento());
        if (updates.getFechaExpedicionTitulo() != null) patronActual.setFechaExpedicionTitulo(updates.getFechaExpedicionTitulo());

        boolean success = patronRepository.updatePatron(patronActual);
        return success ? new ResponseEntity<>(patronActual, HttpStatus.OK) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Eliminar patrón si no está vinculado con ninguna embarcación (DELETE).

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> deletePatron(@PathVariable String dni) {
        if (patronRepository.findPatronByDni(dni) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        int barcosAsignados = patronRepository.countEmbarcacionesVinculadas(dni);
        if (barcosAsignados > 0) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); 
        }

        boolean deleted = patronRepository.deletePatron(dni);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) 
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
