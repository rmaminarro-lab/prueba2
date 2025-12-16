package es.uco.pw.demo.controller.Socio;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UpdateSocioPatronController {

    @Autowired
    private SocioRepository socioRepository;

    @GetMapping("/updatePatronStatusForm")
    public String showForm() {
        return "socio/updatePatronStatusForm";
    }

    @PostMapping("/updatePatronStatus")
    public ModelAndView updatePatronStatus(
            @RequestParam String dni,
            @RequestParam(defaultValue = "false") boolean tieneTituloPatron
    ) {
        ModelAndView mv = new ModelAndView();

        // 1. Comprobar que el socio existe
        Socio socio = socioRepository.findSocioByDni(dni);
        if (socio == null) {
            mv.setViewName("socio/updatePatronStatusViewFail");
            mv.addObject("error", "No se encontró ningún socio con el DNI: " + dni);
            return mv;
        }

        // 2. Realizar la actualización
        boolean success = socioRepository.setTituloPatron(dni, tieneTituloPatron);

        if (success) {
            socio.setTieneTituloPatron(tieneTituloPatron); // Actualizamos el objeto
            mv.setViewName("socio/updatePatronStatusViewSuccess");
            mv.addObject("socio", socio);
        } else {
            mv.setViewName("socio/updatePatronStatusViewFail");
            mv.addObject("error", "Error inesperado al actualizar la base de datos.");
        }
        return mv;
    }
}