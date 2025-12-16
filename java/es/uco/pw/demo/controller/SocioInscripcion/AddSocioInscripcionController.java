package es.uco.pw.demo.controller.SocioInscripcion;

import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AddSocioInscripcionController {

    @Autowired
    private SocioInscripcionRepository socioInscripcionRepository;

    @GetMapping("/addSocioInscripcion")
    public ModelAndView addSocioInscripcion(
            @RequestParam(required = false) Integer idInscripcion,
            @RequestParam(required = false) String dniSocio
    ) {
        if (idInscripcion == null || dniSocio == null) {
            return new ModelAndView("socioInscripcion/addSocioInscripcionForm");
        }

        SocioInscripcion socioInscripcion = new SocioInscripcion(idInscripcion, dniSocio);

        boolean success = socioInscripcionRepository.addSocioInscripcion(socioInscripcion);

        ModelAndView mv;
        if (success) {
            mv = new ModelAndView("socioInscripcion/addSocioInscripcionViewSuccess");
            mv.addObject("socioInscripcion", socioInscripcion);
        } else {
            mv = new ModelAndView("socioInscripcion/addSocioInscripcionViewFail");
        }
        return mv;
    }
}