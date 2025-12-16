package es.uco.pw.demo.controller.Reserva;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ShowReservasController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/showReservas")
    public ModelAndView showReservas() {

        List<Reserva> reservas = reservaRepository.findAllReservas();

        ModelAndView mv = new ModelAndView("reserva/showReservaView");
        mv.addObject("reservas", reservas);
        return mv;
    }
}