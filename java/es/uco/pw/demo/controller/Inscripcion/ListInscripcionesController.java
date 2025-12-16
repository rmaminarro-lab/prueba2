package es.uco.pw.demo.controller.Inscripcion;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ListInscripcionesController {
@Autowired
private InscripcionRepository inscripcionRepository;
@Autowired
private SocioInscripcionRepository socioInscripcionRepository;

    @GetMapping("/listInscripciones")
    public ModelAndView listInscripciones() {
        ModelAndView mv = new ModelAndView("inscripcion/listInscripcionesView");

        // 1. Obtenemos todas las inscripciones
        List<Inscripcion> inscripciones = inscripcionRepository.findAllInscripciones();

        // 2. Creamos un mapa para guardar el recuento de miembros de cada inscripción
        Map<Integer, Integer> familiaresCount = new HashMap<>();
        
        if (inscripciones != null) {
            for (Inscripcion insc : inscripciones) {
                int totalMiembros = socioInscripcionRepository.countByInscripcionId(insc.getId());
                int numFamiliares = totalMiembros - 1; 
                
                familiaresCount.put(insc.getId(), numFamiliares);
            }
        }

        mv.addObject("inscripciones", inscripciones);
        mv.addObject("familiaresCount", familiaresCount);
        return mv;
    }
}