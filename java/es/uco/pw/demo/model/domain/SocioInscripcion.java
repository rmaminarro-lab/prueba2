package es.uco.pw.demo.model.domain;

public class SocioInscripcion {
    private int id;
    private int idInscripcion;
    private String dniSocio;

    // Constructor vacío (necesario para el RowMapper)
    public SocioInscripcion() { }
    
    //Constructor completo
    public SocioInscripcion(int id, int idInscripcion, String dniSocio) {
        this.id = id;
        this.idInscripcion = idInscripcion;
        this.dniSocio = dniSocio;
    }

    //Constructor para crear uno nuevo
    public SocioInscripcion(int idInscripcion, String dniSocio) {
        this.idInscripcion = idInscripcion;
        this.dniSocio = dniSocio;
    }
  
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }
    
    public String getDniSocio() { return dniSocio; }
    public void setDniSocio(String dniSocio) { this.dniSocio = dniSocio; }
}