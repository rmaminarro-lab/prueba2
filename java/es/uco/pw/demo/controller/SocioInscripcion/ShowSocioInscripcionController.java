package es.uco.pw.demo.controller.SocioInscripcion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;

@Controller
public class ShowSocioInscripcionController {
    
    private SocioInscripcionRepository socioInscripcionRepository;
    
    @Autowired
    public ShowSocioInscripcionController(SocioInscripcionRepository socioInscripcionRepository) {
        this.socioInscripcionRepository = socioInscripcionRepository;
    }
    
    @GetMapping("/showSocioInscripcion")
    public ModelAndView showSocioInscripcion() {
        ModelAndView modelAndView = new ModelAndView("socioInscripcion/showSocioInscripcionView");
        
        List<SocioInscripcion> allRelaciones = this.socioInscripcionRepository.findAllSocioInscripciones();
        modelAndView.addObject("relaciones", allRelaciones);
        
        return modelAndView;
    }
}