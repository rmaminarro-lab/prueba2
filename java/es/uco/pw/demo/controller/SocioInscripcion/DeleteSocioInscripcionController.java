package es.uco.pw.demo.controller.SocioInscripcion;

import es.uco.pw.demo.model.repository.SocioInscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeleteSocioInscripcionController {

    @Autowired
    private SocioInscripcionRepository socioInscripcionRepository;

    @GetMapping("/deleteSocioInscripcionForm")
    public String showForm() {
        return "socioInscripcion/deleteSocioInscripcionForm";
    }

    @GetMapping("/deleteSocioInscripcion")
    public ModelAndView deleteSocioInscripcion(
            @RequestParam("idInscripcion") int idInscripcion,
            @RequestParam("dniSocio") String dniSocio) {

        boolean deleted = socioInscripcionRepository.deleteSocioInscripcion(idInscripcion, dniSocio);

        ModelAndView mv;
        if (deleted) {
            mv = new ModelAndView("socioInscripcion/deleteSocioInscripcionViewSuccess");
            mv.addObject("idInscripcion", idInscripcion);
            mv.addObject("dniSocio", dniSocio);
        } else {
            mv = new ModelAndView("socioInscripcion/deleteSocioInscripcionViewFail");
        }
        return mv;
    }
}