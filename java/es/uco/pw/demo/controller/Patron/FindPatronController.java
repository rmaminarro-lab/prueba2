package es.uco.pw.demo.controller.Patron;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindPatronController {

    @Autowired
    private PatronRepository patronRepository;

    // Mostrar el formulario
    @GetMapping("/findPatronForm")
    public ModelAndView showFindPatronForm() {
        return new ModelAndView("patron/findPatronForm");
    }

    // Procesar la búsqueda
    @GetMapping("/findPatron")
    public ModelAndView findPatron(@RequestParam("dni") String dni) {
        Patron patron = patronRepository.findPatronByDni(dni);
        
        ModelAndView mv;
        if (patron != null) {
            mv = new ModelAndView("patron/findPatronViewSuccess");
            mv.addObject("patron", patron);
        } else {
            mv = new ModelAndView("patron/findPatronViewFail");
        }
        return mv;
    }
}