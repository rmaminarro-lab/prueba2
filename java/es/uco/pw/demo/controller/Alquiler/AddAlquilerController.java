package es.uco.pw.demo.controller.Alquiler;

import es.uco.pw.demo.model.domain.Alquiler;
import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.AlquilerRepository;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class AddAlquilerController {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private EmbarcacionRepository embarcacionRepository;
    @Autowired
    private SocioRepository socioRepository;
    @Autowired
    private InscripcionRepository inscripcionRepository;

    private static final double PRECIO_POR_PERSONA_Y_DIA = 20.0;

    /**
     * Mostrar formulario para añadir un nuevo alquiler
     */
    @GetMapping("/addAlquilerForm")
    public ModelAndView showAddAlquilerForm() {
        return new ModelAndView("alquiler/addAlquilerForm");
    }

    /**
     * Procesar el formulario y añadir el alquiler (Requisito C.2)
     */
    @PostMapping("/addAlquiler")
    @Transactional(rollbackFor = Exception.class)
    public ModelAndView processAddAlquiler(
            @RequestParam String dniTitular,
            @RequestParam String matricula,
            @RequestParam int plazasReservadas,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        ModelAndView mv = new ModelAndView();
        try {
            // 1. Validar Socio
            Socio titular = socioRepository.findSocioByDni(dniTitular);
            if (titular == null) {
                throw new Exception("El socio con DNI " + dniTitular + " no existe.");
            }
            if (!titular.isEsTitular()) {
                throw new Exception("El socio con DNI " + dniTitular + " no es un titular de inscripción.");
            }
            if (!titular.isTieneTituloPatron()) {
                throw new Exception("El socio titular debe tener el título de patrón para alquilar (Requisito C).");
            }

            // 2. Validar Embarcación
            Embarcacion embarcacion = embarcacionRepository.findEmbarcacionByMatricula(matricula);
            if (embarcacion == null) {
                throw new Exception("La embarcación con matrícula " + matricula + " no existe.");
            }

            // 3. Validar Plazas
            // El patrón (socio) cuenta como plaza
            if (plazasReservadas > embarcacion.getNumPlazas()) {
                throw new Exception("La embarcación solo tiene " + embarcacion.getNumPlazas() + " plazas. Se solicitaron " + plazasReservadas + ".");
            }

            // 4. Validar Disponibilidad (usando la lógica de C.1)
            List<Embarcacion> disponibles = embarcacionRepository.findEmbarcacionesDisponibles(fechaInicio, fechaFin);
            boolean estaDisponible = disponibles.stream().anyMatch(e -> e.getMatricula().equals(matricula));
            if (!estaDisponible) {
                throw new Exception("La embarcación no está disponible para las fechas seleccionadas.");
            }

            // 5. Validar Reglas de Temporada (Requisito C.2)
            long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1; // +1 para incluir ambos días
            Month mesInicio = fechaInicio.getMonth();
            
            // Temporada ALTA: Mayo a Septiembre (1 o 2 semanas)
            if (mesInicio.getValue() >= 5 && mesInicio.getValue() <= 9) {
                if (dias != 7 && dias != 14) {
                    throw new Exception("Error de temporada: En Mayo-Septiembre solo se alquila por 1 o 2 semanas (7 o 14 días). Solicitados: " + dias + " días.");
                }
            } 
            // Temporada BAJA: Octubre a Abril (3 días)
            else {
                if (dias != 3) {
                    throw new Exception("Error de temporada: En Octubre-Abril solo se alquila por 3 días. Solicitados: " + dias + " días.");
                }
            }

            // 6. Calcular Precio (Req C. Nota)
            double precioTotal = PRECIO_POR_PERSONA_Y_DIA * plazasReservadas * dias;

            // 7. Obtener ID de Inscripción
            Inscripcion inscripcion = inscripcionRepository.findInscripcionByDniTitular(dniTitular);
            if (inscripcion == null) {
                throw new Exception("Error interno: No se encontró la inscripción asociada al titular.");
            }

            // 8. Crear y Guardar Alquiler
            Alquiler alquiler = new Alquiler(
                inscripcion.getId(),
                matricula,
                fechaInicio,
                fechaFin,
                plazasReservadas,
                precioTotal
            );

            boolean success = alquilerRepository.addAlquiler(alquiler); // El ID se asigna dentro

            if (success) {
                mv.setViewName("alquiler/addAlquilerViewSuccess");
                mv.addObject("alquiler", alquiler);
            } else {
                throw new Exception("Error inesperado al guardar el alquiler en la base de datos.");
            }
        
        } catch (Exception e) {
            mv.setViewName("alquiler/addAlquilerViewFail");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
}