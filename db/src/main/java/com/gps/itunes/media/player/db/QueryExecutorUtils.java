package com.gps.itunes.media.player.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by leogps on 12/08/2018.
 */
public abstract class QueryExecutorUtils {

    public static <T> T executePreparedStatement(Connection connection, String sql, QueryExecutor<T> queryExecutor) throws SQLException {

        Statement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            T t = queryExecutor.executeUsingAutoCloseableStatement(statement);
            return t;
        } finally {
            if(statement != null) {
                statement.close();
            }
        }
    }

    public static <T> T executeStatement(Connection connection, QueryExecutor<T> queryExecutor) throws SQLException {

        Statement statement = null;
        try {
            statement = connection.createStatement();
            T t = queryExecutor.executeUsingAutoCloseableStatement(statement);
            return t;
        } finally {
            if(statement != null) {
                statement.close();
            }
        }
    }
}
