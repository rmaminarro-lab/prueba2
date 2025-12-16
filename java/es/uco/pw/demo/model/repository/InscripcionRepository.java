package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.TipoInscripcion;
import es.uco.pw.demo.model.domain.Inscripcion;
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
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

@Repository
public class InscripcionRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public InscripcionRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        setSQLQueriesFileName("db/sql.properties");
    }

    public final void setSQLQueriesFileName(String sqlQueriesFileName){
        try {
            this.sqlQueries = loadProperties(sqlQueriesFileName);
        } catch (IOException e) {
            System.err.println("Error cargando SQL properties en InscripcionRepository");
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

    public List<Inscripcion> findAllInscripciones(){
        try{
            String query = sqlQueries.getProperty("select-findAllInscripciones");
            return jdbcTemplate.query(query, new InscripcionMapper());
        } catch(DataAccessException exception){
            System.err.println("Unable to find inscripciones");
            exception.printStackTrace();
            return null;
        }
    }

    public Inscripcion findInscripcionById(int id){
        try{
            String query = sqlQueries.getProperty("select-findInscripcionById");
            List<Inscripcion> result = jdbcTemplate.query(query, new InscripcionMapper(), id);
            return result.isEmpty() ? null : result.get(0);
        } catch(DataAccessException exception){
            System.err.println("Unable to find inscripcion with id=" + id);
            exception.printStackTrace();
            return null;
        }
    }

    public int addInscripcion(Inscripcion inscripcion){
        try{
            String query = sqlQueries.getProperty("insert-addInscripcion");
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setDate(1, Date.valueOf(inscripcion.getFechaCreacion()));
                ps.setString(2, inscripcion.getTipo().toString());
                ps.setDouble(3, inscripcion.getCuotaActual());
                ps.setString(4, inscripcion.getDniTitular());
                return ps;
            }, keyHolder);
            
            // Devuelve el ID generado
            if (keyHolder.getKey() != null) {
                return keyHolder.getKey().intValue();
            } else {
                return -1; // Fallo al obtener el ID
            }

        } catch(DataAccessException exception){
            System.err.println("Unable to insert inscripcion in the database");
            exception.printStackTrace();
            return -1;
        }
    }

    public boolean deleteInscripcion(int id){
        try{
            String query = sqlQueries.getProperty("delete-deleteInscripcionById"); 
            int result = jdbcTemplate.update(query, id);
            return result > 0;
        } catch(DataAccessException exception){
            System.err.println("Unable to delete inscripcion with id=" + id);
            exception.printStackTrace();
            return false;
        }
    }

    public boolean updateInscripcion(Inscripcion inscripcion) {
        try {
            String query = sqlQueries.getProperty("update-updateInscripcion");
            int result = jdbcTemplate.update(query,
                    Date.valueOf(inscripcion.getFechaCreacion()),
                    inscripcion.getTipo().toString(),
                    inscripcion.getCuotaActual(),
                    inscripcion.getDniTitular(),
                    inscripcion.getId() // El ID va al final, para el WHERE
            );
            return result > 0;
        } catch (DataAccessException exception) {
            System.err.println("Unable to update inscripcion with id=" + inscripcion.getId());
            exception.printStackTrace();
            return false;
        }
    }

    public List<Inscripcion> findInscripcionesByTipo(TipoInscripcion tipo) {
        try {
            String query = sqlQueries.getProperty("select-findInscripcionesByTipo");
            return jdbcTemplate.query(query, new InscripcionMapper(), tipo.name());
        } catch (DataAccessException e) {
            System.err.println("Error buscando inscripciones por tipo: " + tipo);
            e.printStackTrace();
            return null;
        }
    }

    public Inscripcion findInscripcionByDniTitular(String dniTitular) {
        try {
            // Necesitamos una query que no está en el properties, la definimos aquí
            String query = "SELECT * FROM Inscripcion WHERE dniTitular = ?";
            List<Inscripcion> result = jdbcTemplate.query(query, new InscripcionMapper(), dniTitular);
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException exception) {
            System.err.println("Unable to find inscripcion with dniTitular=" + dniTitular);
            exception.printStackTrace();
            return null;
        }
    }

    private static final class InscripcionMapper implements RowMapper<Inscripcion> {
        public Inscripcion mapRow(ResultSet rs, int rowNumber) throws SQLException{
            return new Inscripcion(
                rs.getInt("id"),
                rs.getDate("fechaCreacion").toLocalDate(),
                TipoInscripcion.valueOf(rs.getString("tipo")),
                rs.getDouble("cuotaActual"),
                rs.getString("dniTitular"));
        };
    }
}