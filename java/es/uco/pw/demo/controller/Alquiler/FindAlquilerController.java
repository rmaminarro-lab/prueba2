package es.uco.pw.demo.controller.Alquiler;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindAlquilerController {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @GetMapping("/findAlquiler")
    public ModelAndView findAlquiler(
            @RequestParam(value = "id", required = false) Integer id
    ) {
        ModelAndView modelAndView = new ModelAndView("alquiler/findAlquiler");

        if (id != null) {
            Alquiler alquiler = alquilerRepository.findAlquilerById(id);
            modelAndView.addObject("alquiler", alquiler);
            modelAndView.addObject("buscado", true);
        } else {
            modelAndView.addObject("buscado", false);
        }

        return modelAndView;
    }
}