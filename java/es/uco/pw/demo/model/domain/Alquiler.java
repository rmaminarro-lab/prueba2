package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Alquiler {
    private int id;
    private int idInscripcion;
    private String matricula;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int plazasReservadas;
    private double precioTotal;

    // Campo nuevo para el listado
    private String dniTitular; 

    // Constructor completo
    public Alquiler(int id, int idInscripcion, String matricula, LocalDate fechaInicio,
                    LocalDate fechaFin, int plazasReservadas, double precioTotal) {
        this.id = id;
        this.idInscripcion = idInscripcion;
        this.matricula = matricula;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.plazasReservadas = plazasReservadas;
        this.precioTotal = precioTotal;
    }

    // Constructor para creación
    public Alquiler(int idInscripcion, String matricula, LocalDate fechaInicio,
                LocalDate fechaFin, int plazasReservadas, double precioTotal) {
        this.idInscripcion = idInscripcion;
        this.matricula = matricula;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.plazasReservadas = plazasReservadas;
        this.precioTotal = precioTotal;
    }

    public Alquiler() {}
    
    // --- GETTERS Y SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getPlazasReservadas() { return plazasReservadas; }
    public void setPlazasReservadas(int plazasReservadas) { this.plazasReservadas = plazasReservadas; }

    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }

    // Getter y Setter para el nuevo campo
    public String getDniTitular() { return dniTitular; }
    public void setDniTitular(String dniTitular) { this.dniTitular = dniTitular; }
}