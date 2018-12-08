package com.gps.itunes.media.player.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by leogps on 12/08/2018.
 */
public interface TableDataPopulator {

    int populate(Connection connection) throws SQLException;

}
