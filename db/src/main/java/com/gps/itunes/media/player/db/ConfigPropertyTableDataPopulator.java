package com.gps.itunes.media.player.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by leogps on 12/08/2018.
 */
public class ConfigPropertyTableDataPopulator implements TableDataPopulator {

    @Override
    public int populate(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE SEQUENCE config_property_id START WITH 1");
        return 0;
    }
}
