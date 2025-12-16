package es.uco.pw.demo.model.repository;

import es.uco.pw.demo.model.domain.Reserva;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

@Repository
public class ReservaRepository {

    private final JdbcTemplate jdbcTemplate;
    private Properties sqlQueries;

    @Autowired
    public ReservaRepository(JdbcTemplate jdbcTemplate) {
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

    public List<Reserva> findAllReservas() {
        try { return jdbcTemplate.query(sqlQueries.getProperty("select-findAllReservas"), new ReservaMapper()); }
        catch (DataAccessException e) { return null; }
    }

    public List<Reserva> findReservasFuturas(LocalDate fecha) {
        try {
            return jdbcTemplate.query(sqlQueries.getProperty("select-findReservasFuturas"), new ReservaMapper(), Date.valueOf(fecha));
        } catch (DataAccessException e) { return null; }
    }

    public Reserva findReservaById(int id) {
        try {
            List<Reserva> r = jdbcTemplate.query(sqlQueries.getProperty("select-findReservaById"), new ReservaMapper(), id);
            return r.isEmpty() ? null : r.get(0);
        } catch (DataAccessException e) { return null; }
    }

    public boolean addReserva(Reserva r) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rows = jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sqlQueries.getProperty("insert-addReserva"), new String[]{"id"});
                ps.setString(1, r.getDniSocio());
                ps.setString(2, r.getMatricula());
                ps.setDate(3, Date.valueOf(r.getFecha()));
                ps.setInt(4, r.getPlazasReservadas());
                ps.setString(5, r.getProposito());
                ps.setDouble(6, r.getPrecioTotal());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) r.setId(keyHolder.getKey().intValue());
            return rows > 0;
        } catch (DataAccessException e) { return false; }
    }

    public boolean updateReserva(Reserva r) {
        try {
            return jdbcTemplate.update(sqlQueries.getProperty("update-updateReserva"),
                    r.getDniSocio(), r.getMatricula(), Date.valueOf(r.getFecha()),
                    r.getPlazasReservadas(), r.getProposito(), r.getPrecioTotal(),
                    r.getId()) > 0;
        } catch (DataAccessException e) { return false; }
    }

    // --- RENOMBRADO A deleteReservaById ---
    public boolean deleteReservaById(int id) {
        try { return jdbcTemplate.update(sqlQueries.getProperty("delete-deleteReservaById"), id) > 0; }
        catch (DataAccessException e) { return false; }
    }
    
    // Alias
    public boolean deleteReserva(int id) {
        return deleteReservaById(id);
    }

    public boolean isReservada(String matricula, LocalDate fecha) {
        try {
            Integer count = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-isReservaDisponible"), Integer.class, matricula, Date.valueOf(fecha));
            return count != null && count > 0;
        } catch (DataAccessException e) { return true; }
    }

    public boolean isAlquilada(String matricula, LocalDate fecha) {
        try {
            Integer count = jdbcTemplate.queryForObject(sqlQueries.getProperty("select-isAlquilerDisponible"), Integer.class, matricula, Date.valueOf(fecha));
            return count != null && count > 0;
        } catch (DataAccessException e) { return true; }
    }

    private static class ReservaMapper implements RowMapper<Reserva> {
        public Reserva mapRow(ResultSet rs, int i) throws SQLException {
            return new Reserva(rs.getInt("id"), rs.getString("dniSocio"), rs.getString("matricula"),
                    rs.getDate("fecha").toLocalDate(), rs.getInt("plazasReservadas"), rs.getString("proposito"), rs.getDouble("precioTotal"));
        }
    }
}