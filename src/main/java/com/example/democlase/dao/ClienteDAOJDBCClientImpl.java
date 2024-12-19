package com.example.democlase.dao;

import lombok.extern.slf4j.Slf4j;
import com.example.democlase.modelo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ClienteDAOJDBCClientImpl implements ClienteDAO{

    @Autowired
    private JdbcClient jdbcClient;

    @Override
    public void create(Cliente cliente) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        //Facade
        int rowsUpdated = jdbcClient.sql(
        """
        INSERT INTO cliente (nombre, apellido1, apellido2, ciudad, categoria)
        VALUES ( ?, ?, ?, ?, ?)
        """)
        .param(cliente.getNombre())
                .param(cliente.getApellido1())
                .param(cliente.getApellido2())
                .param(cliente.getCiudad())
                .param(cliente.getCategoria())
                .update(keyHolder);

        log.info("Insertados {} registros", rowsUpdated);
    }

    @Override
    public List<Cliente> getAll() {

        String query = """
SELECT * FROM cliente
""";

        RowMapper<Cliente> rowMapperCliente = (rs, rowNum) -> new Cliente(rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido1"),
                rs.getString("apellido2"),
                rs.getString("ciudad"),
                rs.getInt("categoría")
        );

        List<Cliente> listCli = jdbcClient.sql(query)
                .query(rowMapperCliente)
                .list();

        return listCli;
    }

    @Override
    public Optional<Cliente> find(int id) {

        String query = """
        SELECT * FROM cliente WHERE ID = :id
        """;

        Optional<Cliente> optCliente = jdbcClient.sql(query)
                .param("id", id)
                //Si los nombres de las columnas del
                // resultado coincide con los nombres de los
                // campos del bean, se pueden pasar directamente
                // la clase para que construya el objeto de salida.
                .query(Cliente.class)
                .optional();

        return optCliente;
    }

    @Override
    public void update(Cliente cliente) {

        String query = """
        UPDATE cliente
        SET
        nombre = :nombre,
        apellido1 = :apellido1,
        apellido2 = :apellido2,
        ciudad = :ciudad,
        categoria = :categoria
        WHERE
        id = :id
        """;
        int rowsUpdated = jdbcClient.sql(query)
        // .param("nombre", cliente.getNombre())
        // .param("apellido1", cliente.getApellido1())
        // .param("apellido2", cliente.getApellido2())
        // .param("ciudad", cliente.getCiudad())
        // .param("categoría", cliente.getCategoria())
        // .param("id", cliente.getId())
        // Si los parámetros tienen el mismo nombre que los atributos del bean cliente
        // se puede pasar directamente. .paramSource(cliente)
                .update();

        log.info("Actualizados {} registros", rowsUpdated);

    }

    @Override
    public void delete(int id) {

        int rowsUpdated = jdbcClient.sql("DELETE FROM cliente WHERE id = ?")
                .param(id)
                .update();

        log.info("Borrados {} registros", rowsUpdated);
    }
}