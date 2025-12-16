package es.uco.pw.demo.controller.Socio;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.domain.TipoInscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;
import es.uco.pw.demo.model.repository.SocioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // Importante
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.Period;

@Controller
public class AddSocioController {
@Autowired
private SocioRepository socioRepository;    
@Autowired
private InscripcionRepository inscripcionRepository;  
@Autowired
private SocioInscripcionRepository socioInscripcionRepository;
private static final double CUOTA_INDIVIDUAL = 300.0;

@GetMapping("/addSocioForm")
public String mostrarFormulario() {
    return "socio/addSocioForm";
}

@PostMapping("/addSocio")
@Transactional(rollbackFor = Exception.class) // Si algo falla, deshace todo
public ModelAndView addSocio(
        @RequestParam String dni,
        @RequestParam String nombre,
        @RequestParam String apellidos,
        @RequestParam String fechaNacimiento,
        @RequestParam String direccion,
        @RequestParam(required = false, defaultValue = "false") boolean tieneTituloPatron) 
        {
        ModelAndView mv = new ModelAndView();
        LocalDate nacimientoDate = LocalDate.parse(fechaNacimiento);

        try {
            if (socioRepository.findSocioByDni(dni) != null) {
                throw new Exception("El DNI " + dni + " ya está registrado.");
            }

            if (Period.between(nacimientoDate, LocalDate.now()).getYears() < 18) {
                throw new Exception("El socio debe ser mayor de edad para inscribirse.");
            }
 
            Socio socio = new Socio(
                    dni,
                    nombre,
                    apellidos,
                    nacimientoDate,
                    direccion,
                    LocalDate.now(), // fechaInscripcion
                    true, // esTitular (siempre true en la inscripción inicial)
                    tieneTituloPatron
            );
            
            boolean socioGuardado = socioRepository.addSocio(socio);
            if (!socioGuardado) {
                throw new Exception("Error inesperado al guardar el socio.");
            }

            Inscripcion inscripcion = new Inscripcion(
                    LocalDate.now(),
                    TipoInscripcion.INDIVIDUAL,
                    CUOTA_INDIVIDUAL, // Cuota fija
                    socio.getDni()
            );

            int idInscripcion = inscripcionRepository.addInscripcion(inscripcion);
            if (idInscripcion == -1) {
                throw new Exception("Error inesperado al guardar la inscripción.");
            }

            SocioInscripcion vinculo = new SocioInscripcion(idInscripcion, socio.getDni());
            boolean vinculoGuardado = socioInscripcionRepository.addSocioInscripcion(vinculo);

            if (!vinculoGuardado) {
                throw new Exception("Error inesperado al vincular el socio con la inscripción.");
            }

            mv.setViewName("socio/addSocioViewSuccess");
            mv.addObject("socio", socio);

        } catch (Exception e) {
            mv.setViewName("socio/addSocioViewFail");
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }
}