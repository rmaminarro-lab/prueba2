package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.Embarcacion;
import es.uco.pw.demo.model.domain.TipoEmbarcacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

@Repository
public class EmbarcacionRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public EmbarcacionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String sqlQueriesFileName) {
        try {
            this.sqlQueries = loadProperties(sqlQueriesFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        if (input == null) throw new IOException("No se encontró: " + fileName);
        properties.load(input);
        return properties;
    }

    public List<Embarcacion> findAllEmbarcaciones() {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findAllEmbarcaciones"), new EmbarcacionMapper());
        } catch (DataAccessException e) { return null; }
    }

    public Embarcacion findEmbarcacionByMatricula(String matricula) {
        try {
            List<Embarcacion> r = jdbcTemplate.query(sqlQueries.getProperty("select-findEmbarcacionByMatricula"), new EmbarcacionMapper(), matricula);
            return r.isEmpty() ? null : r.get(0);
        } catch (DataAccessException e) { return null; }
    }

    public List<Embarcacion> findEmbarcacionByTipo(TipoEmbarcacion tipo) {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findEmbarcacionByTipo"), new EmbarcacionMapper(), tipo.name());
        } catch (DataAccessException e) { return null; }
    }
    
    public Embarcacion findEmbarcacionByNombre(String nombre) {
        try {
            List<Embarcacion> r = jdbcTemplate.query(sqlQueries.getProperty("select-findEmbarcacionByNombre"), new EmbarcacionMapper(), nombre);
            return r.isEmpty() ? null : r.get(0);
        } catch (DataAccessException e) { return null; }
    }

    public List<Embarcacion> findEmbarcacionesDisponibles(LocalDate inicio, LocalDate fin) {
        try {
            String query = sqlQueries.getProperty("select-findEmbarcacionesDisponibles");
            Date dInicio = Date.valueOf(inicio);
            Date dFin = Date.valueOf(fin);
            return jdbcTemplate.query(query, new EmbarcacionMapper(), dFin, dInicio, dInicio, dFin, dInicio, dFin, dInicio, dFin);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addEmbarcacion(Embarcacion e) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("insert-addEmbarcacion"),
                    e.getMatricula(), e.getNombre(), e.getTipo().toString(), e.getNumPlazas(), e.getDimensiones(), e.getDniPatron()) > 0;
        } catch (DataAccessException ex) { return false; }
    }

    public boolean updateEmbarcacion(Embarcacion e) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("update-updateEmbarcacion"),
                    e.getNombre(), e.getTipo().toString(), e.getNumPlazas(), e.getDimensiones(), e.getDniPatron(), e.getMatricula()) > 0;
        } catch (DataAccessException ex) { return false; }
    }

    // --- MÉTODOS QUE FALTABAN ---
    public int countAlquileres(String matricula) {
        try {
            Integer c = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-countAlquileresByMatricula"), Integer.class, matricula);
            return c != null ? c : 0;
        } catch (DataAccessException e) { return 0; }
    }

    public int countReservas(String matricula) {
        try {
            Integer c = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-countReservasByMatricula"), Integer.class, matricula);
            return c != null ? c : 0;
        } catch (DataAccessException e) { return 0; }
    }

    public boolean deleteEmbarcacion(String matricula) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("delete-deleteEmbarcacionByMatricula"), matricula) > 0;
        } catch (DataAccessException e) { return false; }
    }

    private static final class EmbarcacionMapper implements RowMapper<Embarcacion> {
        public Embarcacion mapRow(ResultSet rs, int i) throws SQLException {
            return new Embarcacion(
                    rs.getString("matricula"), rs.getString("nombre"),
                    TipoEmbarcacion.valueOf(rs.getString("tipo")),
                    rs.getInt("numPlazas"), rs.getString("dimensiones"), rs.getString("dniPatron"));
        }
    }
}