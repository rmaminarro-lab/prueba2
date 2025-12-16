package es.uco.pw.demo.controller.Embarcacion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.TipoEmbarcacion;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;

@Controller
public class ShowTiposEmbarcacionController {
    
    private EmbarcacionRepository embarcacionRepository;
    private ModelAndView modelAndView = new ModelAndView();
    
    @Autowired
    public ShowTiposEmbarcacionController(EmbarcacionRepository embarcacionRepository) {
        this.embarcacionRepository = embarcacionRepository;
    }

    @GetMapping("/showTiposEmbarcacion")
    public ModelAndView showTiposEmbarcacion() {
        this.modelAndView.setViewName("embarcacion/showTiposEmbarcacionView");

        TipoEmbarcacion[] tipos = TipoEmbarcacion.values();
        List<TipoResumen> resumenTipos = new ArrayList<>();

        for (TipoEmbarcacion tipo : tipos) {
            List<Embarcacion> embarcaciones = this.embarcacionRepository.findEmbarcacionByTipo(tipo);
            int cantidad = (embarcaciones != null) ? embarcaciones.size() : 0;
            resumenTipos.add(new TipoResumen(tipo.name(), cantidad));
        }

        this.modelAndView.addObject("resumenTipos", resumenTipos);
        return modelAndView;
    }

    public static class TipoResumen {
        private String tipo;
        private int cantidad;

        public TipoResumen(String tipo, int cantidad) {
            this.tipo = tipo;
            this.cantidad = cantidad;
        }

        public String getTipo() { return tipo; }
        public int getCantidad() { return cantidad; }
    }
}