package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.Alquiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

@Repository
public class AlquilerRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public AlquilerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String file) {
        try { this.sqlQueries = loadProperties(file); } catch (IOException e) { e.printStackTrace(); }
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties props = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        if (input == null) throw new IOException("No se encontró: " + fileName);
        props.load(input);
        return props;
    }

    public List<Alquiler> findAllAlquileres() {
        try { return jdbcTemplate.query(sqlQueries.getProperty("select-findAllAlquileres"), new AlquilerMapper()); }
        catch (DataAccessException e) { return null; }
    }

    public List<Alquiler> findAlquileresFuturos(LocalDate fecha) {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findAlquileresFuturos"), new AlquilerMapper(), Date.valueOf(fecha));
        } catch (DataAccessException e) { return null; }
    }

    public Alquiler findAlquilerById(int id) {
        try {
            List<Alquiler> r = jdbcTemplate.query(sqlQueries.getProperty("select-findAlquilerById"), new AlquilerMapper(), id);
            return r.isEmpty() ? null : r.get(0);
        } catch (DataAccessException e) { return null; }
    }
    
    public List<Alquiler> findAlquileresByMatricula(String matricula) {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findAlquileresByMatricula"), new AlquilerMapper(), matricula);
        } catch (DataAccessException e) { return null; }
    }

    public boolean addAlquiler(Alquiler a) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sqlQueries.getProperty("insert-addAlquiler"), new String[]{"id"});
                ps.setInt(1, a.getIdInscripcion());
                ps.setString(2, a.getMatricula());
                ps.setDate(3, Date.valueOf(a.getFechaInicio()));
                ps.setDate(4, Date.valueOf(a.getFechaFin()));
                ps.setInt(5, a.getPlazasReservadas());
                ps.setDouble(6, a.getPrecioTotal());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) a.setId(keyHolder.getKey().intValue());
            return rows > 0;
        } catch (DataAccessException e) { return false; }
    }

    public boolean updateAlquilerPlazasPrecio(int id, int plazas, double precio) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("update-updateAlquilerPlazasPrecio"), plazas, precio, id) > 0;
        } catch (DataAccessException e) { return false; }
    }

    // --- RENOMBRADO A deleteAlquilerById PARA COMPATIBILIDAD ---
    public boolean deleteAlquilerById(int id) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("delete-alquilerById"), id) > 0;
        } catch (DataAccessException e) { return false; }
    }
    
    // Método alias para que funcione también si el Controller nuevo llama a deleteAlquiler
    public boolean deleteAlquiler(int id) {
        return deleteAlquilerById(id);
    }

    private static class AlquilerMapper implements RowMapper<Alquiler> {
        public Alquiler mapRow(ResultSet rs, int i) throws SQLException {
            return new Alquiler(rs.getInt("id"), rs.getInt("idInscripcion"), rs.getString("matricula"),
                    rs.getDate("fechaInicio").toLocalDate(), rs.getDate("fechaFin").toLocalDate(),
                    rs.getInt("plazasReservadas"), rs.getDouble("precioTotal"));
        }
    }
}