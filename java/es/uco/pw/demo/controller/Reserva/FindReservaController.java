package es.uco.pw.demo.controller.Reserva;

import es.uco.pw.demo.model.domain.Reserva;
import es.uco.pw.demo.model.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FindReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/findReserva")
    public ModelAndView findReserva(@RequestParam(value = "id", required = false) Integer id) {
        ModelAndView mv = new ModelAndView("reserva/findReservaViewSuccess"); 

        if (id == null) {
            mv.addObject("reserva", null);
            mv.addObject("busquedaRealizada", false);
            return mv;
        }

        Reserva reserva = reservaRepository.findReservaById(id);

        mv.addObject("reserva", reserva);
        mv.addObject("busquedaRealizada", true);
        return mv;
    }
}