package com.gps.itunes.media.player.db;

/**
 * Created by leogps on 12/08/2018.
 */
public enum DBTable {

    CONFIG_PROPERTY("config_property", "player", new ConfigPropertyTableDataPopulator());

    private final String tableName, schema;
    private final TableDataPopulator tableDataPopulator;

    DBTable(String tableName, String schema, TableDataPopulator tableDataPopulator) {
        this.tableName = tableName;
        this.schema = schema;
        this.tableDataPopulator = tableDataPopulator;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchema() {
        return schema;
    }

    public TableDataPopulator getTableDataPopulator() {
        return tableDataPopulator;
    }
}
