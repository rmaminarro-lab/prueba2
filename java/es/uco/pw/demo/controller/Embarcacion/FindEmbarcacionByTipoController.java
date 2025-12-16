package es.uco.pw.demo.controller.Embarcacion;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.TipoEmbarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class FindEmbarcacionByTipoController {

    private final EmbarcacionRepository embarcacionRepository;

    @Autowired
    public FindEmbarcacionByTipoController(EmbarcacionRepository repo) {
        this.embarcacionRepository = repo;
    }

    @GetMapping("/findEmbarcacionByTipo")
    public ModelAndView findEmbarcacionByTipo(
            @RequestParam(value = "tipo", required = false) String tipo
    ) {
        ModelAndView model = new ModelAndView("embarcacion/findEmbarcacionByTipo");

        if (tipo != null && !tipo.isEmpty()) {
            try {
                TipoEmbarcacion tipoEnum = TipoEmbarcacion.valueOf(tipo.toUpperCase());
                List<Embarcacion> lista = embarcacionRepository.findEmbarcacionByTipo(tipoEnum);
                model.addObject("embarcaciones", lista);
            } catch (IllegalArgumentException e) {
                model.addObject("embarcaciones", null);
                System.err.println("Tipo de embarcación no válido: " + tipo);
            }
            model.addObject("buscado", true);
        } else {
            model.addObject("buscado", false);
        }

        return model;
    }
}