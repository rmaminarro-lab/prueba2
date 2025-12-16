package es.uco.pw.demo.model.domain;


import java.time.LocalDate;

public class Patron {
    private String dni;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private LocalDate fechaExpedicionTitulo;

    public Patron(String dni, String nombre, String apellidos, LocalDate fechaNacimiento, LocalDate fechaExpedicionTitulo) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaExpedicionTitulo = fechaExpedicionTitulo;
    }

    public Patron() {}

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public LocalDate getFechaExpedicionTitulo() { return fechaExpedicionTitulo; }

    public void setDni(String dni) { this.dni = dni; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setFechaExpedicionTitulo(LocalDate fechaExpedicionTitulo) { this.fechaExpedicionTitulo = fechaExpedicionTitulo; }
}