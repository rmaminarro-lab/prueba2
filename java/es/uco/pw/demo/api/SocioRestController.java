package es.uco.pw.demo.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;

@RestController
@RequestMapping(path="/api/socios", produces="application/json")
public class SocioRestController {

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private SocioInscripcionRepository socioInscripcionRepository;

    // --- GET & POST (Semana 1) ---

    @GetMapping
    public ResponseEntity<List<Socio>> getAllSocios() {
        List<Socio> socios = socioRepository.findAllSocios();
        if (socios == null || socios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(socios, HttpStatus.OK);
    }

    @GetMapping("/{dni}")
    public ResponseEntity<Socio> getSocioByDni(@PathVariable String dni) {
        Socio socio = socioRepository.findSocioByDni(dni);
        return (socio != null) ? new ResponseEntity<>(socio, HttpStatus.OK)
                               : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes="application/json")
    public ResponseEntity<Socio> createSocio(@RequestBody Socio socio) {
        if (socioRepository.findSocioByDni(socio.getDni()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
       
        boolean created = socioRepository.addSocio(socio);
        return created ? new ResponseEntity<>(socio, HttpStatus.CREATED)
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- PATCH & DELETE (Semana 2) ---

    @PatchMapping("/{dni}")
    public ResponseEntity<Socio> updateSocio(@PathVariable String dni, @RequestBody Socio updates) {
        //Recuperar original
        Socio socioActual = socioRepository.findSocioByDni(dni);
        if (socioActual == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //Aplicar cambios si no son nulos (DNI no se toca)
        if (updates.getNombre() != null) socioActual.setNombre(updates.getNombre());
        if (updates.getApellidos() != null) socioActual.setApellidos(updates.getApellidos());
        if (updates.getFechaNacimiento() != null) socioActual.setFechaNacimiento(updates.getFechaNacimiento());
        if (updates.getDireccion() != null) socioActual.setDireccion(updates.getDireccion());
        if (updates.getFechaInscripcion() != null) socioActual.setFechaInscripcion(updates.getFechaInscripcion());
       
        // Aquí asumimos que si llegan en el JSON se actualizan.
        socioActual.setEsTitular(updates.isEsTitular());
        socioActual.setTieneTituloPatron(updates.isTieneTituloPatron());

        //Guardar
        boolean success = socioRepository.updateSocio(socioActual);
        return success ? new ResponseEntity<>(socioActual, HttpStatus.OK)
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> deleteSocio(@PathVariable String dni) {
        Socio socio = socioRepository.findSocioByDni(dni);
        if (socio == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        int vinculaciones = socioInscripcionRepository.countInscripcionesBySocio(dni);
        if (vinculaciones > 0) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        boolean deleted = socioRepository.deleteSocio(dni);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                       : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}