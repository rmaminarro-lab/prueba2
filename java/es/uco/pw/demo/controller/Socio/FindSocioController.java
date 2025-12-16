package es.uco.pw.demo.controller.Socio;

import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindSocioController {

    @Autowired
    private SocioRepository socioRepository;

    @GetMapping("/findSocioForm")
    public String mostrarFormulario() {
        return "socio/findSocioForm";
    }

    @GetMapping("/findSocio")
    public ModelAndView findSocio(@RequestParam("dni") String dni) {
        Socio socio = socioRepository.findSocioByDni(dni);

        if (socio != null) {
            ModelAndView mv = new ModelAndView("socio/findSocioViewSuccess");
            mv.addObject("socio", socio);
            return mv;
        } else {
            return new ModelAndView("socio/findSocioViewFail");
        }
    }
}