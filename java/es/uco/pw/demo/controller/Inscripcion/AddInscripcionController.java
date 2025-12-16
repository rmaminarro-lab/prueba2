package es.uco.pw.demo.controller.Inscripcion;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.uco.pw.demo.model.domain.Inscripcion;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.SocioInscripcion;
import es.uco.pw.demo.model.domain.TipoInscripcion;
import es.uco.pw.demo.model.repository.InscripcionRepository;
import es.uco.pw.demo.model.repository.SocioInscripcionRepository;
import es.uco.pw.demo.model.repository.SocioRepository;

@Controller
public class AddInscripcionController {
@Autowired
private InscripcionRepository inscripcionRepository;
@Autowired
private SocioRepository socioRepository;
@Autowired
private SocioInscripcionRepository socioInscripcionRepository;

private static final double CUOTA_FAMILIAR_ADULTO_EXTRA = 250.0;
private static final double CUOTA_FAMILIAR_HIJO = 100.0;

    @GetMapping("/addInscripcionForm")
    public String showAddInscripcionForm() {
        return "inscripcion/addInscripcionForm";
    }

    @PostMapping("/addInscripcion")
    @Transactional(rollbackFor = Exception.class)
    public ModelAndView addInscripcion(
            @RequestParam int idInscripcion, 
            @RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String fechaNacimiento,
            @RequestParam String direccion,
            @RequestParam(required = false, defaultValue = "false") boolean tieneTituloPatron,
            Model model
    ) {
        ModelAndView mv = new ModelAndView();
        LocalDate nacimientoDate = LocalDate.parse(fechaNacimiento);

        try {
            // 1. Validar DNI del nuevo miembro
            if (socioRepository.findSocioByDni(dni) != null) {
                throw new Exception("El DNI " + dni + " del nuevo miembro ya está registrado.");
            }

            // 2. Buscar la inscripción del titular
            Inscripcion inscripcion = inscripcionRepository.findInscripcionById(idInscripcion);
            if (inscripcion == null) {
                throw new Exception("No se encontró la inscripción con ID: " + idInscripcion);
            }

            // 3. Crear el nuevo Socio (familiar)
            boolean esAdulto = Period.between(nacimientoDate, LocalDate.now()).getYears() >= 18;
            Socio nuevoFamiliar = new Socio(
                    dni,
                    nombre,
                    apellidos,
                    nacimientoDate,
                    direccion,
                    LocalDate.now(), // fechaInscripcion
                    false, // Un familiar añadido nunca es titular
                    esAdulto && tieneTituloPatron // Solo adultos pueden tener título
            );
            
            if (!socioRepository.addSocio(nuevoFamiliar)) {
                throw new Exception("Error al guardar el nuevo socio familiar.");
            }

            // 4. Calcular nueva cuota y actualizar inscripción
            double cuotaAdicional = esAdulto ? CUOTA_FAMILIAR_ADULTO_EXTRA : CUOTA_FAMILIAR_HIJO;
            
            inscripcion.setTipo(TipoInscripcion.FAMILIAR); // Se convierte en familiar
            inscripcion.setCuotaActual(inscripcion.getCuotaActual() + cuotaAdicional);

            if (!inscripcionRepository.updateInscripcion(inscripcion)) {
                throw new Exception("Error al actualizar la cuota de la inscripción.");
            }

            // 5. Vincular el nuevo socio a la inscripción
            SocioInscripcion vinculo = new SocioInscripcion(idInscripcion, dni);
            if (!socioInscripcionRepository.addSocioInscripcion(vinculo)) {
                throw new Exception("Error al vincular el nuevo socio a la inscripción.");
            }

            mv.setViewName("inscripcion/addInscripcionViewSuccess");
            mv.addObject("inscripcion", inscripcion); // Muestra la inscripción actualizada
            mv.addObject("nuevoSocio", nuevoFamiliar); // Muestra el familiar añadido

        } catch (Exception e) {
            mv.setViewName("inscripcion/addInscripcionViewFail");
            mv.addObject("error", e.getMessage());
        }
        return mv;
    }
}