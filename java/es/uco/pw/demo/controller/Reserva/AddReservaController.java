package es.uco.pw.demo.controller.Reserva;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.repository.EmbarcacionRepository;
import es.uco.pw.demo.model.repository.ReservaRepository;
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
import java.time.Period;

@Controller
public class AddReservaController {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private EmbarcacionRepository embarcacionRepository;
    @Autowired
    private SocioRepository socioRepository;

    private static final double PRECIO_POR_PERSONA_EVENTO = 40.0;

    @GetMapping("/addReservaForm")
    public String showAddReservaForm() {
        return "reserva/addReservaForm";
    }

    @PostMapping("/addReserva")
    @Transactional(rollbackFor = Exception.class)
    public ModelAndView addReserva(
            @RequestParam String dniSocio,
            @RequestParam String matricula,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam int plazasReservadas,
            @RequestParam String proposito
    ) {
        ModelAndView mv = new ModelAndView();

        try {
            // 1. Validar Socio
            Socio socio = socioRepository.findSocioByDni(dniSocio);
            if (socio == null) {
                throw new Exception("El socio con DNI " + dniSocio + " no existe.");
            }
            if (Period.between(socio.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
                throw new Exception("El socio debe ser mayor de edad para reservar (Requisito D).");
            }

            // 2. Validar Embarcación
            Embarcacion embarcacion = embarcacionRepository.findEmbarcacionByMatricula(matricula);
            if (embarcacion == null) {
                throw new Exception("La embarcación con matrícula " + matricula + " no existe.");
            }

            // 3. Validar Patrón
            if (embarcacion.getDniPatron() == null || embarcacion.getDniPatron().isEmpty()) {
                throw new Exception("La embarcación no tiene un patrón (empleado) asociado. No se puede reservar para eventos.");
            }

            // 4. Validar Plazas
            int plazasTotalesNecesarias = plazasReservadas + 1; // +1 por el patrón
            if (plazasTotalesNecesarias > embarcacion.getNumPlazas()) {
                throw new Exception("Capacidad insuficiente. Plazas solicitadas (" + plazasReservadas + ") + 1 (patrón) = " + plazasTotalesNecesarias + ". La embarcación solo tiene " + embarcacion.getNumPlazas() + " plazas.");
            }
            
            // 5. Validar Disponibilidad
            if (fecha.isBefore(LocalDate.now())) {
                 throw new Exception("La fecha de reserva no puede ser en el pasado.");
            }
            if (reservaRepository.isReservada(matricula, fecha)) {
                throw new Exception("La embarcación ya tiene una reserva para el " + fecha + ".");
            }
            if (reservaRepository.isAlquilada(matricula, fecha)) {
                 throw new Exception("La embarcación está en un periodo de alquiler en la fecha " + fecha + ".");
            }

            // 6. Calcular Precio (Requisito D)
            double precioTotal = PRECIO_POR_PERSONA_EVENTO * plazasReservadas;

            // 7. Crear y Guardar Reserva
            Reserva reserva = new Reserva(
                    dniSocio,
                    matricula,
                    fecha,
                    plazasReservadas,
                    proposito,
                    precioTotal
            );

            boolean success = reservaRepository.addReserva(reserva);

            if (success) {
                mv.setViewName("reserva/addReservaViewSuccess");
                mv.addObject("reserva", reserva);
            } else {
                throw new Exception("Error inesperado al guardar la reserva.");
            }
        } catch (Exception e) {
            mv.setViewName("reserva/addReservaViewFail");
            mv.addObject("errorMessage", e.getMessage());
        }

        return mv;
    }
}