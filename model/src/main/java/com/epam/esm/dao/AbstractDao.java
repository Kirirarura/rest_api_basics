package com.epam.esm.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T> {

    protected final String findByIdQuery;
    protected final String findByColumnQuery;
    protected final String deleteByIdQuery;
    protected final String getAllQuery;
    private final RowMapper<T> rowMapper;
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";

    protected AbstractDao(RowMapper<T> rowMapper,String tableName, JdbcTemplate jdbcTemplate) {
        this.rowMapper = rowMapper;
        this.jdbcTemplate = jdbcTemplate;

        getAllQuery =  SELECT_ALL_FROM + tableName;
        findByIdQuery = SELECT_ALL_FROM + tableName + " WHERE id=?";
        findByColumnQuery = SELECT_ALL_FROM + tableName + " WHERE %s=?";
        deleteByIdQuery = "DELETE FROM " + tableName + " WHERE id=?";
    }

    public List<T> getAll() {
        return jdbcTemplate.query(getAllQuery, rowMapper);
    }

    public Optional<T> findById(Long id) {
        return jdbcTemplate.query(findByIdQuery, rowMapper, id)
                .stream().findAny();
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(deleteByIdQuery, id);
    }

    public Optional<T> findByColumn(String columnName, String value) {
        String query = String.format(findByColumnQuery, columnName);
        return jdbcTemplate.query(query, rowMapper, value).stream().findAny();
    }


}
