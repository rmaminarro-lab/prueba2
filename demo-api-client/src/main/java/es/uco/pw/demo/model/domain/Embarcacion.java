package es.uco.pw.demo.model.domain;

public class Embarcacion {
    private String matricula;
    private String nombre;
    private TipoEmbarcacion tipo;
    private int numPlazas;
    private String dimensiones;
    private String dniPatron; // opcional

    public Embarcacion(String matricula, String nombre, TipoEmbarcacion tipo, int numPlazas, String dimensiones, String dniPatron) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.tipo = tipo;
        this.numPlazas = numPlazas;
        this.dimensiones = dimensiones;
        this.dniPatron = dniPatron;
    }

    public Embarcacion() {}

    public String getMatricula() { return matricula; }
    public String getNombre() { return nombre; }
    public TipoEmbarcacion getTipo() { return tipo; }
    public int getNumPlazas() { return numPlazas; }
    public String getDimensiones() { return dimensiones; }
    public String getDniPatron() { return dniPatron; }

    public void setMatricula(String matricula) { this.matricula = matricula; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipo(TipoEmbarcacion tipo) { this.tipo = tipo; }
    public void setNumPlazas(int numPlazas) { this.numPlazas = numPlazas; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    public void setDniPatron(String dniPatron) { this.dniPatron = dniPatron; }
}