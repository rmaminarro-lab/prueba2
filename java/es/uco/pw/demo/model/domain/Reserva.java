package es.uco.pw.demo.model.domain;


import java.time.LocalDate;

public class Reserva {
    private int id;
    private String dniSocio;
    private String matricula;
    private LocalDate fecha;
    private int plazasReservadas;
    private String proposito;
    private double precioTotal;

    public Reserva(int id, String dniSocio, String matricula, LocalDate fecha,
                   int plazasReservadas, String proposito, double precioTotal) {
        this.id = id;
        this.dniSocio = dniSocio;
        this.matricula = matricula;
        this.fecha = fecha;
        this.plazasReservadas = plazasReservadas;
        this.proposito = proposito;
        this.precioTotal = precioTotal;
    }

    public Reserva(String dniSocio, String matricula, LocalDate fecha,
                   int plazasReservadas, String proposito, double precioTotal) {
        this.dniSocio = dniSocio;
        this.matricula = matricula;
        this.fecha = fecha;
        this.plazasReservadas = plazasReservadas;
        this.proposito = proposito;
        this.precioTotal = precioTotal;
    }

    public Reserva() {}

    public int getId() { return id; }
    public String getDniSocio() { return dniSocio; }
    public String getMatricula() { return matricula; }
    public LocalDate getFecha() { return fecha; }
    public int getPlazasReservadas() { return plazasReservadas; }
    public String getProposito() { return proposito; }
    public double getPrecioTotal() { return precioTotal; }

    public void setId(int id) { this.id = id; }
    public void setDniSocio(String dniSocio) { this.dniSocio = dniSocio; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setPlazasReservadas(int plazasReservadas) { this.plazasReservadas = plazasReservadas; }
    public void setProposito(String proposito) { this.proposito = proposito; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }
}