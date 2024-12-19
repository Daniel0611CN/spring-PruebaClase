package com.example.democlase.dao;

import lombok.extern.slf4j.Slf4j;
import com.example.democlase.modelo.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ClienteDAOJDBCTemplateImpl implements ClienteDAO {


    //Plantilla jdbc inyectada automáticamente por el framework Spring, gracias a la anotación @Autowired. @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Inserta en base de datos el nuevo Cliente, actualizando el id en el bean Cliente.
     */
    @Override
    public void create(Cliente cliente) {

        //Desde java15+ se tiene la triple quote """ para bloques de texto como cadenas.
        String sqlInsert = """
        
                INSERT INTO cliente (nombre, apellido1, apellido2, ciudad, categoria)
                        VALUES ( ?, ?, ?, ?, ?)
        """;

        //Sin recuperación de id generado// int rows = jdbcTemplate.update(sqlInsert,// cliente.getNombre(),// cliente.getApellido1(),// cliente.getApellido2(),// cliente.getCiudad(),// cliente.getCategoria()// );

        PreparedStatementCreator psc = connection -> {
        PreparedStatement ps = connection.prepareStatement(sqlInsert, new String[]{"id"});
        int idx = 1;
        ps.setString(idx++, cliente.getNombre());
        ps.setString(idx++, cliente.getApellido1());
        ps.setString(idx++, cliente.getApellido2());
        ps.setString(idx++, cliente.getCiudad());
        ps.setInt(idx, cliente.getCategoria());
        return ps;
        };

        //Con recuperación de id generado
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(psc, keyHolder);

        cliente.setId(keyHolder.getKey().intValue());

        log.info("Insertados {} registros.", rows);
    }

    /**
     * Devuelve lista con todos los Clientes.
     */
    @Override
    public List<Cliente> getAll() {

        RowMapper<Cliente> rowMapperCliente = (rs, rowNum) -> new Cliente(rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("apellido1"),
            rs.getString("apellido2"),
            rs.getString("ciudad"),
            rs.getInt("categoría")
        );

        List<Cliente> listCli = jdbcTemplate.query("SELECT * FROM cliente", rowMapperCliente);

        log.info("Devueltos {} registros.", listCli.size());

        return listCli;
    }

    /**
     * Devuelve Optional de Cliente con el ID dado.
     */
    @Override
    public Optional<Cliente> find(int id) {
        Cliente fab = jdbcTemplate
        .queryForObject("SELECT * FROM cliente WHERE id = ?"
        , (rs, rowNum) -> new
                                Cliente(rs.getInt("id"),
        rs.getString("nombre"),
        rs.getString("apellido1"),
        rs.getString("apellido2"),
        rs.
                getString("ciudad"),
        rs.getInt("categoría"))
        , id
        );

        // if (fab != null) {// return Optional.of(fab);}// else {// log.info("Cliente no encontrado.");// return Optional.empty(); }

        return Optional.ofNullable(fab);
    }


    /**
     * Actualiza Cliente con campos del bean Cliente del mismo.
     */
    @Override
    public void update(Cliente cliente) {

        int rows = jdbcTemplate.update(
        """
        UPDATE cliente SET nombre = ?, apellido1 = ?, apellido2 = ?, ciudad = ?, categoria = ?
        WHERE id = ?
        """, cliente.getNombre()
           , cliente.getApellido1()
           , cliente.getApellido2()
           , cliente.getCiudad()
           , cliente.getCategoria()
           , cliente.getId());

        log.info("Update de Cliente con {} registros actualizados.", rows);

    }

    /**
     * Borra Cliente con ID proporcionado.
     */
    @Override
    public void delete(int id) {
        int rows = jdbcTemplate.update("DELETE FROM cliente WHERE id = ?", id);

        log.info("Delete de Cliente con {} registros eliminados.", rows);
    }

}