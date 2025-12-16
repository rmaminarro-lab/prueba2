package es.uco.pw.demo.controller.AlquilerSocio;

import es.uco.pw.demo.model.domain.AlquilerSocio;
import es.uco.pw.demo.model.repository.AlquilerSocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ShowAlquilerSocioController {

    @Autowired
    private AlquilerSocioRepository alquilerSocioRepository;

    @GetMapping("/showAlquilerSocio")
    public ModelAndView showAlquilerSocio() {

        List<AlquilerSocio> alquilerSocios = alquilerSocioRepository.findAllAlquilerSocios();

        ModelAndView mv = new ModelAndView("alquilerSocios/showAlquilerSocioView");
        mv.addObject("alquilerSocios", alquilerSocios);
        return mv;
    }
}