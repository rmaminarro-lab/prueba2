package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.AlquilerSocio;
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
public class AlquilerSocioRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public AlquilerSocioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String sqlQueriesFileName) {
        try {
            this.sqlQueries = loadProperties(sqlQueriesFileName);
        } catch (IOException e) {
            System.err.println("Error cargando SQL properties en AlquilerSocioRepository");
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
    private static final class AlquilerSocioMapper implements RowMapper<AlquilerSocio> {
        @Override
        public AlquilerSocio mapRow(ResultSet rs, int rowNum) throws SQLException {
            // BUG CORREGIDO: Usamos la columna 'id' que añadimos al SQL
            return new AlquilerSocio(
                    rs.getInt("id"), 
                    rs.getInt("idAlquiler"),
                    rs.getString("dniSocio")
            );
        }
    }

    public List<AlquilerSocio> findAllAlquilerSocios() {
        try {
            String query = sqlQueries.getProperty("select-findAllAlquilerSocios");
            // Tu SQL no selecciona 'id', vamos a añadirlo
            query = "SELECT id, idAlquiler, dniSocio FROM AlquilerSocio";
            return jdbcTemplate.query(query, new AlquilerSocioMapper());
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AlquilerSocio> findByIdAlquiler(int idAlquiler) {
        try {
            String query = sqlQueries.getProperty("select-findAlquilerSocioByIdAlquiler");
            // Tu SQL no selecciona 'id', vamos a añadirlo
            query = "SELECT id, idAlquiler, dniSocio FROM AlquilerSocio WHERE idAlquiler = ?";
            return jdbcTemplate.query(query, new AlquilerSocioMapper(), idAlquiler);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<AlquilerSocio> findByDniSocio(String dniSocio) {
        try {
            String query = sqlQueries.getProperty("select-findAlquilerSocioByDni");
            // Tu SQL no selecciona 'id', vamos a añadirlo
            query = "SELECT id, idAlquiler, dniSocio FROM AlquilerSocio WHERE dniSocio = ?";
            return jdbcTemplate.query(query, new AlquilerSocioMapper(), dniSocio);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addAlquilerSocio(AlquilerSocio alquilerSocio) {
        try {
            String query = sqlQueries.getProperty("insert-addAlquilerSocio");
            int rows = jdbcTemplate.update(query,
                    alquilerSocio.getIdAlquiler(),
                    alquilerSocio.getDniSocio()
            );
            return rows > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAlquilerSocio(int idAlquiler, String dniSocio) {
        try {
            String query = sqlQueries.getProperty("delete-deleteAlquilerSocio");
            int result = jdbcTemplate.update(query, idAlquiler, dniSocio);
            return result > 0;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}