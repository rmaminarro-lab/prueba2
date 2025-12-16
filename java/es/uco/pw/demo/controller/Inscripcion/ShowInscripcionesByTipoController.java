package es.uco.pw.demo.controller.Inscripcion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.TipoInscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;

@Controller
public class ShowInscripcionesByTipoController {

    private final InscripcionRepository inscripcionRepository;

    @Autowired
    public ShowInscripcionesByTipoController(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    @GetMapping("/showInscripcionesByTipo")
    public ModelAndView showInscripcionesByTipo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("inscripcion/showInscripcionesByTipoView.html");

        List<ResumenTipo> resumenTipos = new ArrayList<>();

        for (TipoInscripcion tipo : TipoInscripcion.values()) {
            List<Inscripcion> inscripciones = this.inscripcionRepository.findInscripcionesByTipo(tipo);
            int cantidad = (inscripciones != null) ? inscripciones.size() : 0;
            resumenTipos.add(new ResumenTipo(tipo.name(), cantidad));
        }

        modelAndView.addObject("resumenTipos", resumenTipos);
        return modelAndView;
    }

    public static class ResumenTipo {
        private final String tipo;
        private final int cantidad;

        public ResumenTipo(String tipo, int cantidad) {
            this.tipo = tipo;
            this.cantidad = cantidad;
        }

        public String getTipo() { return tipo; }
        public int getCantidad() { return cantidad; }
    }
}