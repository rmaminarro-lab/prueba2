package es.uco.pw.demo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.Patron;
import es.uco.pw.demo.model.domain.Socio;
import es.uco.pw.demo.model.domain.TipoEmbarcacion;

public class DemoAPI {

    public static void main(String[] args) {
        RestTemplate rest = new RestTemplate();
        String baseURL = "http://localhost:8080/api";

        System.out.println("\n=============================================");
        System.out.println("   DEMO API 1: RECURSOS BÁSICOS (FORMATO REAL)");
        System.out.println("=============================================\n");

        // --- 1. GESTIÓN DE SOCIOS ---
        System.out.println("--> 1. Creando Socio (POST)...");
        Socio nuevoSocio = new Socio(
            "12345678Z", 
            "Antonio", 
            "García Márquez", 
            LocalDate.of(1985, 3, 15), 
            "Av. Gran Capitán 23, Córdoba", 
            LocalDate.now(), 
            true,
            true 
        );

        try {
            ResponseEntity<Socio> res = rest.postForEntity(baseURL + "/socios", nuevoSocio, Socio.class);
            System.out.println("   [OK] Socio creado: " + res.getBody().getNombre());
        } catch (HttpClientErrorException e) {
            System.out.println("   [INFO] El socio ya existía (" + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("   [ERROR CRÍTICO] " + e.getMessage());
            System.err.println("   (Si es un error 500, revisa que Socio.java tenga constructor vacío)");
        }

        // --- 2. GESTIÓN DE PATRONES ---
        System.out.println("\n--> 2. Creando Patrón (POST)...");
        Patron nuevoPatron = new Patron(
            "87654321X", 
            "María", 
            "López Fernández", 
            LocalDate.of(1978, 11, 20), 
            LocalDate.of(2005, 6, 10)
        );

        try {
            ResponseEntity<Patron> res = rest.postForEntity(baseURL + "/patrones", nuevoPatron, Patron.class);
            System.out.println("   [OK] Patrón creado: " + res.getBody().getNombre());
        } catch (HttpClientErrorException e) {
            System.out.println("   [INFO] El patrón ya existía (" + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("   [ERROR] " + e.getMessage());
        }

        // --- 3. GESTIÓN DE EMBARCACIONES ---
        System.out.println("\n--> 3. Creando Embarcación (POST)...");
        
        Embarcacion nuevaEmb = new Embarcacion(
            "MAT050", 
            "El Intrépido", 
            TipoEmbarcacion.YATE, 
            8, 
            "12x4 m", 
            null // Sin patrón al principio
        );

        try {
            ResponseEntity<Embarcacion> res = rest.postForEntity(baseURL + "/embarcaciones", nuevaEmb, Embarcacion.class);
            System.out.println("   [OK] Embarcación creada: " + res.getBody().getNombre());
        } catch (HttpClientErrorException e) {
            System.out.println("   [INFO] La embarcación ya existía (" + e.getStatusCode() + ")");
        } catch (Exception e) {
            System.err.println("   [ERROR] " + e.getMessage());
            System.err.println("   (Si es un error 500, revisa que Embarcacion.java tenga constructor vacío)");
        }

        // --- 4. LISTADOS ---
        System.out.println("\n--> 4. Verificando datos en BD...");
        try {
            ResponseEntity<Socio[]> socios = rest.getForEntity(baseURL + "/socios", Socio[].class);
            int numSocios = socios.getBody() != null ? socios.getBody().length : 0;
            System.out.println("   Socios totales: " + numSocios);
        } catch (Exception e) {}

        System.out.println("\n=== FIN DEMO 1 ===");
    }
}