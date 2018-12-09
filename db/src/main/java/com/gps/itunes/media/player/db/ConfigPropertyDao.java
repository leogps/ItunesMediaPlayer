package com.gps.itunes.media.player.db;

import com.gps.itunes.media.player.db.model.ConfigProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leogps on 12/08/2018.
 */
public class ConfigPropertyDao implements Dao<ConfigProperty> {

    private final Connection connection;

    private String table = DBTable.CONFIG_PROPERTY.getTableName();
    private String schema = DBTable.CONFIG_PROPERTY.getSchema();

    public ConfigPropertyDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ConfigProperty findById(final long id) throws SQLException {

        String sql = String.format("SELECT * FROM %s.%s where id = ?", schema, table);
        return QueryExecutorUtils.executePreparedStatement(connection, sql, new QueryExecutor<ConfigProperty>() {

            @Override
            public ConfigProperty executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                PreparedStatement preparedStatement = (PreparedStatement) statement;
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                return retrieveProperty(resultSet);
            }
        });
    }

    private ConfigProperty retrieveProperty(ResultSet resultSet) throws SQLException {
        try {
            if (resultSet.next()) {
                return retrieve(resultSet);
            }
        } finally {
            resultSet.close();
        }
        return null;
    }

    private ConfigProperty retrieve(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        String property = resultSet.getString("property");
        String value = resultSet.getString("value");

        ConfigProperty configProperty = new ConfigProperty();
        configProperty.setId(id);
        configProperty.setProperty(property);
        configProperty.setValue(value);
        return configProperty;
    }

    public ConfigProperty findByKey(final String key) throws SQLException {
        String sql = String.format("SELECT * FROM %s.%s where property = ?", schema, table);
        return QueryExecutorUtils.executePreparedStatement(connection, sql, new QueryExecutor<ConfigProperty>() {

            @Override
            public ConfigProperty executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                PreparedStatement preparedStatement = (PreparedStatement) statement;
                preparedStatement.setString(1, key);
                ResultSet resultSet = preparedStatement.executeQuery();
                return retrieveProperty(resultSet);
            }
        });
    }

    @Override
    public List<ConfigProperty> list() throws SQLException {
        final String sql = String.format("SELECT * FROM %s.%s", schema, table);
        return QueryExecutorUtils.executeStatement(connection, new QueryExecutor<List<ConfigProperty>>() {

            @Override
            public List<ConfigProperty> executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                ResultSet resultSet = null;
                try {
                    resultSet = statement.executeQuery(sql);
                    return retrieveProperties(resultSet);
                } finally {
                    if(resultSet != null) {
                        resultSet.close();
                    }
                }
            }
        });
    }

    private List<ConfigProperty> retrieveProperties(ResultSet resultSet) throws SQLException {
        List<ConfigProperty> configProperties = new ArrayList<ConfigProperty>();
        while(resultSet.next()) {
            ConfigProperty configProperty = retrieve(resultSet);
            if(configProperty != null) {
                configProperties.add(configProperty);
            }
        }
        return configProperties;
    }

    @Override
    public ConfigProperty insert(final ConfigProperty configProperty) throws SQLException {
        String sql = String.format("INSERT INTO %s.%s (id, property, value) VALUES " +
                " (NEXT VALUE FOR config_property_id, ?, ?)", schema, table);
        return QueryExecutorUtils.executePreparedStatement(connection, sql, new QueryExecutor<ConfigProperty>() {

            @Override
            public ConfigProperty executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                PreparedStatement preparedStatement = (PreparedStatement) statement;
                preparedStatement.setString(1, configProperty.getProperty());
                preparedStatement.setString(2, configProperty.getValue());
                preparedStatement.execute();
                return findByKey(configProperty.getProperty());
            }
        });
    }

    @Override
    public void update(final ConfigProperty configProperty) throws SQLException {
        String sql = String.format("UPDATE %s.%s SET id = ?, property = ?, value = ?", schema, table);
        QueryExecutorUtils.executePreparedStatement(connection, sql, new QueryExecutor<Void>() {
            @Override
            public Void executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                PreparedStatement preparedStatement = (PreparedStatement) statement;
                preparedStatement.setLong(1, configProperty.getId());
                preparedStatement.setString(2, configProperty.getProperty());
                preparedStatement.setString(3, configProperty.getValue());
                preparedStatement.executeUpdate();
                return null;
            }
        });
    }

    @Override
    public void insertOrUpdate(ConfigProperty configProperty) throws SQLException {

        ConfigProperty retrieved = findByKey(configProperty.getProperty());

        if(retrieved != null) {
            update(configProperty);
        } else {
            insert(configProperty);
        }
    }

    @Override
    public void delete(final long id) throws SQLException {
        final String sql = String.format("delete from %s.%s where id = ?", schema, table);
        QueryExecutorUtils.executePreparedStatement(connection, sql, new QueryExecutor<Void>() {
            @Override
            public Void executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                PreparedStatement preparedStatement = (PreparedStatement) statement;
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
                return null;
            }
        });
    }

    @Override
    public void deleteAll() throws SQLException {
        final String sql = String.format("delete from %s.%s", schema, table);
        QueryExecutorUtils.executeStatement(connection, new QueryExecutor<Void>() {
            @Override
            public Void executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                statement.execute(sql);
                return null;
            }
        });
    }
}
