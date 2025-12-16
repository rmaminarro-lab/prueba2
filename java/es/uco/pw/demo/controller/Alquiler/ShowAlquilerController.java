package es.uco.pw.demo.controller.Alquiler;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ShowAlquilerController {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @GetMapping("/showAlquileres")
    public ModelAndView showAlquileres() {
        // REVERTIDO: Volvemos a llamar a findAllAlquileres
        List<Alquiler> alquileres = alquilerRepository.findAllAlquileres();

        ModelAndView mv = new ModelAndView("alquiler/showAlquileresView");
        mv.addObject("alquileres", alquileres);
        return mv;
    }
}