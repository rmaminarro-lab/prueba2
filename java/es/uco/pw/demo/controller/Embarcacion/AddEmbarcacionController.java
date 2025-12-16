package es.uco.pw.demo.controller.Embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.TipoEmbarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Cambiado a POST
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AddEmbarcacionController {

    private final EmbarcacionRepository embarcacionRepository;

    @Autowired
    public AddEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/addEmbarcacionForm")
    public ModelAndView showForm() {
        ModelAndView mv = new ModelAndView("embarcacion/addEmbarcacionForm");
        mv.addObject("tiposEmbarcacion", TipoEmbarcacion.values());
        return mv;
    }

    @PostMapping("/addEmbarcacion") // Cambiado a POST
    public ModelAndView addEmbarcacion(
            @RequestParam String matricula,
            @RequestParam String nombre,
            @RequestParam String tipo, // Recibimos String
            @RequestParam Integer numPlazas,
            @RequestParam String dimensiones,
            @RequestParam(required = false) String dniPatron
    ) {
        ModelAndView mv = new ModelAndView();
        try {
            if (embarcacionRepository.findEmbarcacionByMatricula(matricula) != null) {
                throw new Exception("La matrícula '" + matricula + "' ya está registrada.");
            }
            if (embarcacionRepository.findEmbarcacionByNombre(nombre) != null) {
                throw new Exception("El nombre '" + nombre + "' ya está registrado.");
            }
            // Si el DNI del patrón está vacío, se guarda como null
            String patronDni = (dniPatron != null && dniPatron.isEmpty()) ? null : dniPatron;

            Embarcacion embarcacion = new Embarcacion(
                    matricula,
                    nombre,
                    TipoEmbarcacion.valueOf(tipo.toUpperCase()), // Convertimos String a ENUM
                    numPlazas,
                    dimensiones,
                    patronDni
            );

            //Guardar
            boolean success = embarcacionRepository.addEmbarcacion(embarcacion);
            if (success) {
                mv.setViewName("embarcacion/addEmbarcacionViewSuccess");
                mv.addObject("embarcacion", embarcacion);
            } else {
                throw new Exception("Error inesperado al guardar la embarcación.");
            }

        } catch (Exception e) {
            // 3. Manejar errores
            mv.setViewName("embarcacion/addEmbarcacionViewFail");
            mv.addObject("error", e.getMessage());
            mv.addObject("tiposEmbarcacion", TipoEmbarcacion.values());
        }
        
        return mv;
    }
}