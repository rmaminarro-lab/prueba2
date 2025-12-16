package es.uco.pw.demo.controller.SocioInscripcion;

import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class FindSocioInscripcionController {

    @Autowired
    private SocioInscripcionRepository socioInscripcionRepository;

    @GetMapping("/findSocioInscripcionById")
    public ModelAndView findSocioInscripcionById(@RequestParam(value = "id", required = false) Integer id) {
        ModelAndView mv = new ModelAndView("socioInscripcion/findSocioInscripcionById");

        if (id == null) {
            mv.addObject("socioInscripciones", null);
            mv.addObject("busquedaRealizada", false);
            return mv;
        }

        // Se llama al método corregido: findByIdInscripcion()
        List<SocioInscripcion> socioInscripciones = socioInscripcionRepository.findByIdInscripcion(id);

        mv.addObject("socioInscripciones", socioInscripciones);
        mv.addObject("busquedaRealizada", true);

        return mv;
    }


    @GetMapping("/findSocioInscripcionByDni")
    public ModelAndView findSocioInscripcionByDni(@RequestParam(value = "dniSocio", required = false) String dniSocio) {
        ModelAndView mv = new ModelAndView("socioInscripcion/findSocioInscripcionesByDni");

        if (dniSocio == null || dniSocio.isEmpty()) {
            mv.addObject("socioInscripciones", null);
            mv.addObject("busquedaRealizada", false);
            return mv;
        }
        
        List<SocioInscripcion> socioInscripciones = socioInscripcionRepository.findByDniSocio(dniSocio);

        mv.addObject("socioInscripciones", socioInscripciones);
        mv.addObject("busquedaRealizada", true);

        return mv;
    }
}