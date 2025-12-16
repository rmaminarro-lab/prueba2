package es.uco.pw.demo.controller.Alquiler;

import es.uco.pw.demo.model.repository.AlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeleteAlquilerController {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @GetMapping("/deleteAlquiler")
    public ModelAndView showDeleteForm() {
        return new ModelAndView("alquiler/deleteAlquiler");
    }

    @GetMapping("/deleteAlquiler/process")
    public ModelAndView processDelete(@RequestParam("id") int id) {
        ModelAndView mv;
        boolean deleted = alquilerRepository.deleteAlquilerById(id);
        if (deleted) {
            mv = new ModelAndView("alquiler/deleteAlquilerViewSuccess");
            mv.addObject("id", id);
        } else {
            mv = new ModelAndView("alquiler/deleteAlquilerViewFail");
            mv.addObject("id", id);
        }
        return mv;
    }
}
