package es.uco.pw.demo.controller.Socio;

import es.uco.pw.demo.model.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeleteSocioController {

    @Autowired
    private SocioRepository socioRepository;

    @GetMapping("/deleteSocioForm")
    public String showDeleteForm() {
        return "socio/deleteSocioForm";
    }

    @GetMapping("/deleteSocio")
    public ModelAndView deleteSocio(@RequestParam("dni") String dni) {

        boolean deleted = socioRepository.deleteSocio(dni);

        ModelAndView mv;
        if (deleted) {
            mv = new ModelAndView("socio/deleteSocioViewSuccess");
            mv.addObject("dni", dni);
        } else {
            mv = new ModelAndView("socio/deleteSocioViewFail");
        }
        return mv;
    }
}