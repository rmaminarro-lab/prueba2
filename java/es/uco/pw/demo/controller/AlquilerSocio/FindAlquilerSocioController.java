package es.uco.pw.demo.controller.AlquilerSocio;

import es.uco.pw.demo.model.domain.AlquilerSocio;
import es.uco.pw.demo.model.repository.AlquilerSocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Controlador para buscar relaciones Alquiler-Socio.
 */
@Controller
public class FindAlquilerSocioController {

    @Autowired
    private AlquilerSocioRepository alquilerSocioRepository;

    /**
     * Muestra el formulario de búsqueda por ID de alquiler.
     * @return Vista del formulario.
     */
    @GetMapping("/findAlquilerSocioByIdForm")
    public ModelAndView showFindByIdForm() {
        return new ModelAndView("alquilerSocios/findAlquilerSocioByIdForm");
    }

    /**
     * Procesa la búsqueda por ID de alquiler.
     * @param idAlquiler ID del alquiler a buscar.
     * @return Vista de resultados (éxito o fallo).
     */
    @GetMapping("/findAlquilerSocioById")
    public ModelAndView findByIdAlquiler(@RequestParam("idAlquiler") int idAlquiler) {
        List<AlquilerSocio> results = alquilerSocioRepository.findByIdAlquiler(idAlquiler);
        ModelAndView mv = results != null && !results.isEmpty()
                ? new ModelAndView("alquilerSocios/findAlquilerSocioByIdViewSuccess")
                : new ModelAndView("alquilerSocios/findAlquilerSocioByIdViewFail");
        mv.addObject("alquilerSocios", results);
        mv.addObject("idAlquiler", idAlquiler);
        return mv;
    }

    /**
     * Muestra el formulario de búsqueda por DNI de socio.
     * @return Vista del formulario.
     */
    @GetMapping("/findAlquilerSocioByDniForm")
    public ModelAndView showFindByDniForm() {
        return new ModelAndView("alquilerSocios/findAlquilerSocioByDniForm");
    }

    /**
     * Procesa la búsqueda por DNI de socio.
     * @param dni DNI del socio a buscar.
     * @return Vista de resultados (éxito o fallo).
     */
    @GetMapping("/findAlquilerSocioByDni")
    public ModelAndView findByDni(@RequestParam("dni") String dni) {
        List<AlquilerSocio> results = alquilerSocioRepository.findByDniSocio(dni);
        ModelAndView mv = results != null && !results.isEmpty()
                ? new ModelAndView("alquilerSocios/findAlquilerSocioByDniViewSuccess")
                : new ModelAndView("alquilerSocios/findAlquilerSocioByDniViewFail");
        mv.addObject("alquilerSocios", results);
        mv.addObject("dni", dni);
        return mv;
    }
}