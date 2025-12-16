package es.uco.pw.demo.controller.AlquilerSocio;

import es.uco.pw.demo.model.repository.AlquilerSocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeleteAlquilerSocioController {

    @Autowired
    private AlquilerSocioRepository alquilerSocioRepository;

    @GetMapping("/deleteAlquilerSocioForm")
    public ModelAndView showDeleteForm() {
        return new ModelAndView("alquilerSocios/deleteAlquilerSocioForm");
    }

    @GetMapping("/deleteAlquilerSocio")
    public ModelAndView deleteAlquilerSocio(@RequestParam("idAlquiler") int idAlquiler,
                                            @RequestParam("dni") String dni) {
        boolean deleted = alquilerSocioRepository.deleteAlquilerSocio(idAlquiler, dni);
        ModelAndView mv = deleted
                ? new ModelAndView("alquilerSocios/deleteAlquilerSocioViewSuccess")
                : new ModelAndView("alquilerSocios/deleteAlquilerSocioViewFail");
        mv.addObject("idAlquiler", idAlquiler);
        mv.addObject("dni", dni);
        return mv;
    }
}