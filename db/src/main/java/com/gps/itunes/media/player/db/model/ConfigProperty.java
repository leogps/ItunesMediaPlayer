package com.gps.itunes.media.player.db.model;

import java.io.Serializable;

/**
 * Created by leogps on 12/08/2018.
 */
public class ConfigProperty implements Serializable {

    private long id;
    private String property;
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigProperty that = (ConfigProperty) o;

        if (getId() != that.getId()) return false;
        if (getProperty() != null ? !getProperty().equals(that.getProperty()) : that.getProperty() != null)
            return false;
        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getProperty() != null ? getProperty().hashCode() : 0);
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        return result;
    }
}
