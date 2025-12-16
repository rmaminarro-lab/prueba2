package es.uco.pw.demo.controller.Patron;

import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
public class AddPatronController {

    @Autowired
    private PatronRepository patronRepository;

    /**
     * Muestra el formulario
     */
    @GetMapping("/addPatronForm")
    public String showAddPatronForm() {
        return "patron/addPatronForm";
    }

    /**
     * Procesa el formulario
     */
    @PostMapping("/addPatron")
    public ModelAndView addPatron(
            @RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaExpedicionTitulo
    ) {
        ModelAndView mv = new ModelAndView();
        try {
            // 1. Validar DNI duplicado
            if (patronRepository.findPatronByDni(dni) != null) {
                throw new Exception("El DNI " + dni + " ya está registrado como patrón.");
            }
            // 2. Crear y guardar el patrón
            Patron patron = new Patron(
                    dni,
                    nombre,
                    apellidos,
                    fechaNacimiento,
                    fechaExpedicionTitulo
            );

            boolean success = patronRepository.addPatron(patron);

            if (success) {
                mv.setViewName("patron/addPatronViewSuccess");
                mv.addObject("patron", patron);
            } else {
                throw new Exception("Error inesperado al guardar el patrón.");
            }

        } catch (Exception e) {
            mv.setViewName("patron/addPatronViewFail");
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }
}