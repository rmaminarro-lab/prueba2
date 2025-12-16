package es.uco.pw.demo.controller.Patron;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ShowPatronController {

    @Autowired
    private PatronRepository patronRepository;

    @GetMapping("/showPatrones")
    public ModelAndView showAllPatrones() {

        List<Patron> patrons = patronRepository.findAllPatrones();

        ModelAndView mv = new ModelAndView("patron/showPatronView");
        mv.addObject("patrons", patrons);
        return mv;
    }
}