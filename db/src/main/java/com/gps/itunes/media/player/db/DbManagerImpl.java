package com.gps.itunes.media.player.db;

import com.gps.itunes.lib.parser.utils.PropertyManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;

/**
 * Created by leogps on 12/07/2018.
 */
public class DbManagerImpl implements DbManager {

    private static final DbManager DB_MANAGER = new DbManagerImpl();

    private DbManagerImpl(){}

    public static DbManager getInstance() {
        return DB_MANAGER;
    }

    private static Logger LOGGER = Logger.getLogger(DbManagerImpl.class);

    String protocol = "jdbc:derby:;";
    private Connection connection;
    private String dbName = "imp";

    @Override
    public synchronized void initialize() throws SQLException, ClassNotFoundException {
        String derbySystemHome = PropertyManager.getConfigurationMap().get("derby.system.home");
        if(derbySystemHome == null) {
            derbySystemHome = System.getProperty("user.home") + File.separator + ".imp";
        }
        LOGGER.debug("Derby System Home: " + derbySystemHome);
        File dbDirectory = new File(derbySystemHome);
        if(!dbDirectory.exists()) {
            LOGGER.debug("Derby db directory does not exist. Creating...");
            boolean created = dbDirectory.mkdir();
            LOGGER.debug("Derby db directory created? " + created);
        }
        System.setProperty("derby.system.home", derbySystemHome);

        Class.forName(org.apache.derby.jdbc.EmbeddedDriver.class.getName());

        connection = DriverManager.getConnection(String.format("%sdatabaseName=%s;create=true", protocol, dbName));
        createTableIfNotExists(DBTable.CONFIG_PROPERTY);
    }

    @Override
    public boolean isInitiated() {
        return connection != null;
    }

    private void createTableIfNotExists(DBTable table) throws SQLException {
        if(exists(table)) {
            return;
        }

        Statement stmt = null;
        try {
            stmt = connection.createStatement();


            stmt.executeUpdate(String.format("CREATE TABLE %s.%s (ID INT PRIMARY KEY, PROPERTY VARCHAR(256) NOT NULL UNIQUE, VALUE VARCHAR(4000) NOT NULL)", table.getSchema(), table.getTableName()));
            LOGGER.debug(String.format("Created Table: `%s.%s`", table.getSchema(), table.getTableName()));
        } finally {
            if(stmt != null) {
                stmt.close();
            }
        }

        table.getTableDataPopulator().populate(connection);
    }

    private boolean exists(DBTable table) {
        final String sql = String.format("select count(*) count from %s.%s", table.getSchema(), table.getTableName());
        Long count;
        try {
            count = QueryExecutorUtils.executeStatement(connection, new QueryExecutor<Long>() {
                @Override
                public Long executeUsingAutoCloseableStatement(Statement statement) throws SQLException {
                    ResultSet resultSet = statement.executeQuery(sql);
                    if(resultSet.next()) {
                        return resultSet.getLong("count");
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            LOGGER.debug("Could not count table.", e);
            return false;
        }
        return count != null;
    }

    @Override
    public void shutdown() {
        try {
            if(isInitiated()) {
                connection.close();
                connection = null;
            }
            DriverManager.getConnection("%sdatabaseName=%s;shutdown=true", protocol, dbName);
        } catch (SQLException e) {
            LOGGER.debug("Shutdown completed.", e);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
