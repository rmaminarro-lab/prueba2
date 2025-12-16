package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.Socio;
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
public class SocioRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public SocioRepository(JdbcTemplate jdbcTemplate) {
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
        if (input == null) throw new IOException("No se encontró el archivo: " + fileName);
        properties.load(input);
        return properties;
    }

    public Socio findSocioByDni(String dni) {
        try {
            String query = sqlQueries.getProperty("select-findSocioByDni");
            List<Socio> result = jdbcTemplate.query(query, new SocioMapper(), dni);
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException exception) {
            return null;
        }
    }

    public List<Socio> findAllSocios() {
        try {
            String query = sqlQueries.getProperty("select-findAllSocios");
            return jdbcTemplate.query(query, new SocioMapper());
        } catch (DataAccessException exception) {
            return null;
        }
    }

    public boolean addSocio(Socio socio) {
        try {
            String query = sqlQueries.getProperty("insert-addSocio");
            int result = jdbcTemplate.update(query,
                    socio.getDni(), socio.getNombre(), socio.getApellidos(),
                    Date.valueOf(socio.getFechaNacimiento()), socio.getDireccion(),
                    Date.valueOf(socio.getFechaInscripcion()), socio.isEsTitular(), socio.isTieneTituloPatron());
            return result > 0;
        } catch (DataAccessException exception) { return false; }
    }

    // --- MÉTODO QUE FALTABA ---
    public boolean updateSocio(Socio socio) {
        try {
            String query = sqlQueries.getProperty("update-updateSocio");
            int result = jdbcTemplate.update(query,
                    socio.getNombre(), socio.getApellidos(),
                    Date.valueOf(socio.getFechaNacimiento()), socio.getDireccion(),
                    Date.valueOf(socio.getFechaInscripcion()), socio.isEsTitular(),
                    socio.isTieneTituloPatron(), socio.getDni());
            return result > 0;
        } catch (DataAccessException exception) { return false; }
    }

    public boolean setTituloPatron(String dni, boolean tieneTitulo) {
        try {
            String query = sqlQueries.getProperty("update-setTituloPatron");
            return jdbcTemplate.update(query, tieneTitulo, dni) > 0;
        } catch (DataAccessException exception) { return false; }
    }

    public boolean deleteSocio(String dni) {
        try {
            String query = sqlQueries.getProperty("delete-deleteSocioByDni");
            return jdbcTemplate.update(query, dni) > 0;
        } catch (DataAccessException exception) { return false; }
    }

    private static final class SocioMapper implements RowMapper<Socio> {
        public Socio mapRow(ResultSet rs, int rowNumber) throws SQLException {
            return new Socio(
                    rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"),
                    rs.getDate("fechaNacimiento").toLocalDate(), rs.getString("direccion"),
                    rs.getDate("fechaInscripcion").toLocalDate(), rs.getBoolean("esTitular"), rs.getBoolean("tieneTituloPatron"));
        }
    }
}