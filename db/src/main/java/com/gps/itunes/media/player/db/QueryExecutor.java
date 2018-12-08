package com.gps.itunes.media.player.db;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by leogps on 12/08/2018.
 */
public interface QueryExecutor<T> {

    T executeUsingAutoCloseableStatement(Statement statement) throws SQLException;
}
