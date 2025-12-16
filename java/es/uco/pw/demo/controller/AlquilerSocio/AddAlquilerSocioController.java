package es.uco.pw.demo.controller.AlquilerSocio;

import es.uco.pw.demo.model.domain.AlquilerSocio;
import es.uco.pw.demo.model.repository.AlquilerSocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("alquilerSocio")
public class AddAlquilerSocioController {

    @Autowired
    private AlquilerSocioRepository alquilerSocioRepository;

    // Mostrar el formulario para añadir Alquiler-Socio
    @GetMapping("/addAlquilerSocio")
    public ModelAndView showAddAlquilerSocioForm() {
        ModelAndView modelAndView = new ModelAndView("alquilerSocios/addAlquilerSocioForm");
        // Se crea el objeto sin id, porque lo genera la BD automáticamente
        modelAndView.addObject("alquilerSocio", new AlquilerSocio(0, ""));
        return modelAndView;
    }

    // Procesar el formulario de añadir Alquiler-Socio
    @PostMapping("/addAlquilerSocio")
    public ModelAndView processAddAlquilerSocio(@ModelAttribute("alquilerSocio") AlquilerSocio alquilerSocio, SessionStatus status) {
        ModelAndView modelAndView;
        // Usamos el constructor sin id, la BD generará automáticamente el id
        AlquilerSocio nuevoAlquilerSocio = new AlquilerSocio(
                alquilerSocio.getIdAlquiler(),
                alquilerSocio.getDniSocio()
        );

        boolean success = alquilerSocioRepository.addAlquilerSocio(nuevoAlquilerSocio);

        if (success) {
            modelAndView = new ModelAndView("alquilerSocios/addAlquilerSocioViewSuccess");
            modelAndView.addObject("alquilerSocio", nuevoAlquilerSocio);
        } else {
            modelAndView = new ModelAndView("alquilerSocios/addAlquilerSocioViewFail");
        }

        status.setComplete(); // Limpiar el objeto de la sesión
        return modelAndView;
    }
}
