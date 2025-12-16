package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.Patron;
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
import java.util.List;
import java.util.Properties;

@Repository
public class PatronRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public PatronRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String sqlQueriesFileName) {
        try { this.sqlQueries = loadProperties(sqlQueriesFileName); } catch (IOException e) { e.printStackTrace(); }
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        if (input == null) throw new IOException("No se encontró: " + fileName);
        properties.load(input);
        return properties;
    }

    public List<Patron> findAllPatrones() {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findAllPatrones"), new PatronMapper());
        } catch (DataAccessException e) { return null; }
    }

    public Patron findPatronByDni(String dni) {
        try {
            List<Patron> r = jdbcTemplate.query(sqlQueries.getProperty("select-findPatronByDni"), new PatronMapper(), dni);
            return r.isEmpty() ? null : r.get(0);
        } catch (DataAccessException e) { return null; }
    }

    public boolean addPatron(Patron p) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("insert-addPatron"),
                    p.getDni(), p.getNombre(), p.getApellidos(),
                    Date.valueOf(p.getFechaNacimiento()), Date.valueOf(p.getFechaExpedicionTitulo())) > 0;
        } catch (DataAccessException e) { return false; }
    }

    // Update para PATCH
    public boolean updatePatron(Patron p) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("update-updatePatron"),
                    p.getNombre(), p.getApellidos(),
                    Date.valueOf(p.getFechaNacimiento()), Date.valueOf(p.getFechaExpedicionTitulo()),
                    p.getDni()) > 0;
        } catch (DataAccessException e) { return false; }
    }

    // Validación para DELETE
    public int countEmbarcacionesVinculadas(String dniPatron) {
        try {
            Integer count = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-countEmbarcacionesByPatron"), Integer.class, dniPatron);
            return count != null ? count : 0;
        } catch (DataAccessException e) { return 0; }
    }

    public boolean deletePatron(String dni) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("delete-deletePatronByDni"), dni) > 0;
        } catch (DataAccessException e) { return false; }
    }

    private static final class PatronMapper implements RowMapper<Patron> {
        public Patron mapRow(ResultSet rs, int i) throws SQLException {
            return new Patron(
                    rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"),
                    rs.getDate("fechaNacimiento").toLocalDate(), rs.getDate("fechaExpedicionTitulo").toLocalDate());
        }
    }
}