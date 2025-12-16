package es.uco.pw.demo.model.domain;

public class AlquilerSocio {
    private int id;          
    private int idAlquiler;
    private String dniSocio;

    // Constructor para consultas (id ya conocido)
    public AlquilerSocio(int id, int idAlquiler, String dniSocio) {
        this.id = id;
        this.idAlquiler = idAlquiler;
        this.dniSocio = dniSocio;
    }

    // Constructor para inserciones (id generado por la BD)
    public AlquilerSocio(int idAlquiler, String dniSocio) {
        this.idAlquiler = idAlquiler;
        this.dniSocio = dniSocio;
    }

    public AlquilerSocio() {}

    // Getters y setters
    public int getId() { return id; }
    public int getIdAlquiler() { return idAlquiler; }
    public String getDniSocio() { return dniSocio; }

    public void setId(int id) { this.id = id; }
    public void setIdAlquiler(int idAlquiler) { this.idAlquiler = idAlquiler; }
    public void setDniSocio(String dniSocio) { this.dniSocio = dniSocio; }
}