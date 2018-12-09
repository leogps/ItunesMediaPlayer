package com.gps.itunes.media.player.db;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by leogps on 12/08/2018.
 */
public interface Dao<K extends Serializable> {

    K findById(long id) throws SQLException;

    List<K> list() throws SQLException;

    K insert(K k) throws SQLException;

    void update(K k) throws SQLException;

    void insertOrUpdate(K k) throws SQLException;

    void delete(long id) throws SQLException;

    void deleteAll() throws SQLException;

}
