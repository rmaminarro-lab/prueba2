package es.uco.pw.demo.controller.Embarcacion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

@Controller
public class FindEmbarcacionByMatriculaController {

    private EmbarcacionRepository embarcacionRepository;

    @Autowired
    public FindEmbarcacionByMatriculaController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/findEmbarcacionByMatricula")
    public ModelAndView findEmbarcacionByMatricula(
            @RequestParam(value = "matricula", required = false) String matricula
    ) {
        ModelAndView model = new ModelAndView("embarcacion/findEmbarcacionByMatricula.html");

        if (matricula != null && !matricula.isEmpty()) {
            Embarcacion embarcacion = embarcacionRepository.findEmbarcacionByMatricula(matricula);
            model.addObject("embarcacion", embarcacion);
            model.addObject("buscado", true);
        } else {
            model.addObject("buscado", false);
        }

        return model;
    }
}