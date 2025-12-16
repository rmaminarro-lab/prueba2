package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Inscripcion {
    private int id;
    private LocalDate fechaCreacion;
    private TipoInscripcion tipo;
    private double cuotaActual;
    private String dniTitular;

    // Constructor completo (el que ya tenías)
    public Inscripcion(int id, LocalDate fechaCreacion, TipoInscripcion tipo, double cuotaActual, String dniTitular) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.tipo = tipo;
        this.cuotaActual = cuotaActual;
        this.dniTitular = dniTitular;
    }

    public Inscripcion() {}
    
    //Constructor para crear una nueva inscripción (sin ID)
    public Inscripcion(LocalDate fechaCreacion, TipoInscripcion tipo, double cuotaActual, String dniTitular) {
        this.fechaCreacion = fechaCreacion;
        this.tipo = tipo;
        this.cuotaActual = cuotaActual;
        this.dniTitular = dniTitular;
    }

    // Getters y Setters (los que ya tenías)
    public int getId() { return id; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public TipoInscripcion getTipo() { return tipo; }
    public double getCuotaActual() { return cuotaActual; }
    public String getDniTitular() { return dniTitular; }

    public void setId(int id) { this.id = id; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public void setTipo(TipoInscripcion tipo) { this.tipo = tipo; }
    public void setCuotaActual(double cuotaActual) { this.cuotaActual = cuotaActual; }
    public void setDniTitular(String dniTitular) { this.dniTitular = dniTitular; }
}