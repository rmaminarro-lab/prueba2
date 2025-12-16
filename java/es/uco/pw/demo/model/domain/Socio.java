package es.uco.pw.demo.model.domain;

import java.time.LocalDate;

public class Socio {
    private String dni;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String direccion;
    private LocalDate fechaInscripcion;
    private boolean esTitular;
    private boolean tieneTituloPatron;

    //Constructor vacío
    public Socio() { }

    //Constructor completo
    public Socio(String dni, String nombre, String apellidos, LocalDate fechaNacimiento,
                 String direccion, LocalDate fechaInscripcion, boolean esTitular, boolean tieneTituloPatron) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.fechaInscripcion = fechaInscripcion;
        this.esTitular = esTitular;
        this.tieneTituloPatron = tieneTituloPatron;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    
    public boolean isEsTitular() { return esTitular; }
    public void setEsTitular(boolean esTitular) { this.esTitular = esTitular; }
    
    public boolean isTieneTituloPatron() { return tieneTituloPatron; }
    public void setTieneTituloPatron(boolean tieneTituloPatron) { this.tieneTituloPatron = tieneTituloPatron; }
}