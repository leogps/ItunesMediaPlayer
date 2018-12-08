package com.gps.itunes.media.player.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by leogps on 12/07/2018.
 */
public interface DbManager {

    void initialize() throws SQLException, ClassNotFoundException;

    boolean isInitiated();

    void shutdown() throws SQLException;

    Connection getConnection();

}
