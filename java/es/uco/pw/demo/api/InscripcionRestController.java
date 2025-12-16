package es.uco.pw.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.domain.TipoInscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.SocioRepository;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;

@RestController
@RequestMapping(path="/api/inscripciones", produces="application/json")
public class InscripcionRestController {

    @Autowired
    private InscripcionRepository inscripcionRepository;
   
    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private SocioInscripcionRepository socioInscripcionRepository;

    // --- GET & POST (Semana 1) ---

    @GetMapping
    public ResponseEntity<List<Inscripcion>> getAllInscripciones() {
        List<Inscripcion> lista = inscripcionRepository.findAllInscripciones();
        return (lista != null && !lista.isEmpty())
                ? new ResponseEntity<>(lista, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/individual")
    public ResponseEntity<List<Inscripcion>> getIndividuales() {
        return new ResponseEntity<>(inscripcionRepository.findInscripcionesByTipo(TipoInscripcion.INDIVIDUAL), HttpStatus.OK);
    }

    @GetMapping("/familiar")
    public ResponseEntity<List<Inscripcion>> getFamiliares() {
        return new ResponseEntity<>(inscripcionRepository.findInscripcionesByTipo(TipoInscripcion.FAMILIAR), HttpStatus.OK);
    }

    @GetMapping("/titular/{dni}")
    public ResponseEntity<Inscripcion> getByTitular(@PathVariable String dni) {
        Inscripcion i = inscripcionRepository.findInscripcionByDniTitular(dni);
        return (i != null) ? new ResponseEntity<>(i, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Inscripcion> createInscripcion(@RequestBody Inscripcion inscripcion) {
        // Validación: El titular debe existir
        if (socioRepository.findSocioByDni(inscripcion.getDniTitular()) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
       
        int id = inscripcionRepository.addInscripcion(inscripcion);
        if (id > 0) {
            inscripcion.setId(id);
            // Vincular automáticamente al titular
            socioInscripcionRepository.addSocioInscripcion(new SocioInscripcion(id, inscripcion.getDniTitular()));
            return new ResponseEntity<>(inscripcion, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- PUT, PATCH & DELETE (Semana 2) ---

    /**
     *Convertir inscripción individual a familiar (PUT).
     * Se espera que el cuerpo lleve el nuevo tipo (FAMILIAR) y quizás la nueva cuota.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Inscripcion> updateTipoInscripcion(@PathVariable int id, @RequestBody Inscripcion request) {
        Inscripcion inscripcionActual = inscripcionRepository.findInscripcionById(id);
        if (inscripcionActual == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Actualizamos datos
        inscripcionActual.setTipo(request.getTipo()); // Debería ser FAMILIAR
        inscripcionActual.setCuotaActual(request.getCuotaActual());
       
        boolean updated = inscripcionRepository.updateInscripcion(inscripcionActual);
        return updated ? new ResponseEntity<>(inscripcionActual, HttpStatus.OK)
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     *Vincular nuevo miembro a familiar (PATCH).
     * Recibe un JSON con el DNI del socio a añadir: { "dniSocio": "..." }
     */
    @PatchMapping(path = "/{id}/miembros", consumes = "application/json")
    public ResponseEntity<Void> addMiembro(@PathVariable int id, @RequestBody SocioInscripcion request) {
        Inscripcion inscripcion = inscripcionRepository.findInscripcionById(id);
        if (inscripcion == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Validación: Solo familiares aceptan miembros extra (además del titular)
        if (inscripcion.getTipo() != TipoInscripcion.FAMILIAR) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Socio socio = socioRepository.findSocioByDni(request.getDniSocio());
        if (socio == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND); // El socio debe existir antes

        SocioInscripcion vinculo = new SocioInscripcion(id, request.getDniSocio());
        boolean linked = socioInscripcionRepository.addSocioInscripcion(vinculo);
       
        return linked ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     *Desvincular miembro (PATCH/DELETE).
     * Endpoint: /api/inscripciones/{id}/miembros/{dni}
     */
    @DeleteMapping("/{id}/miembros/{dni}")
    public ResponseEntity<Void> removeMiembro(@PathVariable int id, @PathVariable String dni) {
        boolean removed = socioInscripcionRepository.removeSocioFromInscripcion(id, dni);
        return removed ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *Cancelar inscripción completa dado el DNI del titular (DELETE).
     */
    @DeleteMapping("/titular/{dni}")
    public ResponseEntity<Void> deleteInscripcionByTitular(@PathVariable String dni) {
        Inscripcion inscripcion = inscripcionRepository.findInscripcionByDniTitular(dni);
        if (inscripcion == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Primero borramos las vinculaciones (tabla intermedia) por integridad referencial,
        // a menos que la BD tenga ON DELETE CASCADE configurado.
        // Asumiendo que debemos hacerlo manual:
        // (Esto requeriría un método 'deleteAllByInscripcionId' en el repo, si no hay Cascade)
        // Por ahora intentamos borrar la inscripción directamente:
       
        boolean deleted = inscripcionRepository.deleteInscripcion(inscripcion.getId());
       
        // Si falla por FK, el repo captura la excepción y devuelve false -> 409 Conflict
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}