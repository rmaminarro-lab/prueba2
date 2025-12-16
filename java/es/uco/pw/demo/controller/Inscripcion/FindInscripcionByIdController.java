package es.uco.pw.demo.controller.Inscripcion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;

@Controller
public class FindInscripcionByIdController {

    private InscripcionRepository inscripcionRepository;

    @Autowired
    public FindInscripcionByIdController(InscripcionRepository inscripcionRepository){
        this.inscripcionRepository = inscripcionRepository;
    }

    // Mostrar el formulario vacío
    @GetMapping("/findInscripcionByIdForm")
    public String showFindInscripcionForm() {
        return "inscripcion/findInscripcionById";
    }

    // Procesar la búsqueda (con parámetro id)
    @GetMapping("/findInscripcionById")
    public ModelAndView findInscripcionById(@RequestParam("id") int id){
        Inscripcion inscripcion = inscripcionRepository.findInscripcionById(id);

        ModelAndView model;
        if (inscripcion != null) {
            model = new ModelAndView("inscripcion/findInscripcionById");
            model.addObject("inscripcion", inscripcion);
        } else {
            model = new ModelAndView("inscripcion/findInscripcionById");
            model.addObject("inscripcion", null);
            model.addObject("error", "No se encontró ninguna inscripción con ese ID.");
        }
        return model;
    }
}