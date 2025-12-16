package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.SocioInscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Repository
public class SocioInscripcionRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public SocioInscripcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String file) {
        try { this.sqlQueries = loadProperties(file); } catch (IOException e) { e.printStackTrace(); }
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties props = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        if (input == null) throw new IOException("Archivo no encontrado: " + fileName);
        props.load(input);
        return props;
    }

    // --- MÉTODOS QUE FALTABAN ---
    
    public List<SocioInscripcion> findAllSocioInscripciones() {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findAllSocioInscripciones"), new SIMapper());
        } catch (DataAccessException e) { return null; }
    }

    public List<SocioInscripcion> findByIdInscripcion(int idInscripcion) {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findSocioInscripcionByIdInscripcion"), new SIMapper(), idInscripcion);
        } catch (DataAccessException e) { return null; }
    }
    
    public List<SocioInscripcion> findByDniSocio(String dni) {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findSocioInscripcionByDni"), new SIMapper(), dni);
        } catch (DataAccessException e) { return null; }
    }

    public boolean addSocioInscripcion(SocioInscripcion si) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("insert-addSocioInscripcion"), si.getIdInscripcion(), si.getDniSocio()) > 0;
        } catch (DataAccessException e) { return false; }
    }

    public boolean deleteSocioInscripcion(int idInscripcion, String dniSocio) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("delete-deleteSocioInscripcion"), idInscripcion, dniSocio) > 0;
        } catch (DataAccessException e) { return false; }
    }
    
    // Alias para compatibilidad
    public boolean removeSocioFromInscripcion(int id, String dni) {
        return deleteSocioInscripcion(id, dni);
    }

    public int countInscripcionesBySocio(String dniSocio) {
        try {
            Integer count = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-countSocioInscripcionesByDniSocio"), Integer.class, dniSocio);
            return count != null ? count : 0;
        } catch (DataAccessException e) { return 0; }
    }

    public int countByInscripcionId(int idInscripcion) {
        try {
            Integer count = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-countByInscripcionId"), Integer.class, idInscripcion);
            return count != null ? count : 0;
        } catch (DataAccessException e) { return 0; }
    }
    
    // Alias para compatibilidad
    public int countMiembrosInInscripcion(int id) {
        return countByInscripcionId(id);
    }

    private static final class SIMapper implements RowMapper<SocioInscripcion> {
        public SocioInscripcion mapRow(ResultSet rs, int i) throws SQLException {
            SocioInscripcion si = new SocioInscripcion();
            si.setId(rs.getInt("id"));
            si.setIdInscripcion(rs.getInt("idInscripcion"));
            si.setDniSocio(rs.getString("dniSocio"));
            return si;
        }
    }
}