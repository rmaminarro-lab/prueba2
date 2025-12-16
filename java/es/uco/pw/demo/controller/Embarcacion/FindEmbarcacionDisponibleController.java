package es.uco.pw.demo.controller.Embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FindEmbarcacionDisponibleController {

    @Autowired
    private EmbarcacionRepository embarcacionRepository;

    /**
     * Muestra el formulario de búsqueda o los resultados.
     */
    @GetMapping("/findEmbarcacionDisponible")
    public ModelAndView findEmbarcacionDisponible(
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        ModelAndView mv = new ModelAndView("embarcacion/findEmbarcacionDisponibleForm");

        if (fechaInicio != null && fechaFin != null) {
            if (fechaInicio.isAfter(fechaFin)) {
                mv.addObject("error", "La fecha de inicio no puede ser posterior a la fecha de fin.");
                mv.addObject("buscado", true);
                return mv;
            }

            List<Embarcacion> embarcaciones = embarcacionRepository.findEmbarcacionesDisponibles(fechaInicio, fechaFin);
            
            mv.addObject("embarcaciones", embarcaciones);
            mv.addObject("buscado", true);
            mv.addObject("fechaInicio", fechaInicio);
            mv.addObject("fechaFin", fechaFin);
        } else {
            mv.addObject("buscado", false);
        }
        return mv;
    }
}