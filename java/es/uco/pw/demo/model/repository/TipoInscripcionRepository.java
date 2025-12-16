package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.TipoInscripcion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

@Repository
public class TipoInscripcionRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public TipoInscripcionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String sqlQueriesFileName) {
        try {
            this.sqlQueries = loadProperties(sqlQueriesFileName);
        } catch (IOException e) {
            System.err.println("Error cargando SQL properties en TipoInscripcionRepository");
            e.printStackTrace();
        }
    }

    private Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            throw new IOException("No se encontró el archivo: " + fileName);
        }
        properties.load(input);
        return properties;
    }

    // Mapper reutilizable
    private static final class TipoInscripcionMapper implements RowMapper<TipoInscripcion> {
        @Override
        public TipoInscripcion mapRow(ResultSet rs, int rowNum) throws SQLException {
            String tipoStr = rs.getString("tipo");
            return TipoInscripcion.valueOf(tipoStr);
        }
    }

    public List<TipoInscripcion> findAllTiposInscripcion() {
        try {
            // Asumiendo que quieres los tipos DISTINTOS de la tabla Inscripcion
            String query = "SELECT DISTINCT tipo FROM Inscripcion";
            return jdbcTemplate.query(query, new TipoInscripcionMapper());
        } catch (DataAccessException e) {
            System.err.println("Unable to find tipos de inscripción");
            e.printStackTrace();
            return null;
        }
    }

    public TipoInscripcion findTipoInscripcionByNombre(String tipoNombre) {
        try {
            // Asumiendo que quieres comprobar si existe un tipo en la tabla Inscripcion
            String query = "SELECT DISTINCT tipo FROM Inscripcion WHERE UPPER(tipo) = ?";
            List<TipoInscripcion> results = jdbcTemplate.query(query, new TipoInscripcionMapper(), tipoNombre.toUpperCase());
            return results.isEmpty() ? null : results.get(0);
        } catch (DataAccessException e) {
            System.err.println("Unable to find tipo de inscripción: " + tipoNombre);
            e.printStackTrace();
            return null;
        }
    }
}