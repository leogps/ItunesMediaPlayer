package com.gps.itunes.media.player.db.model;

import java.io.Serializable;
import java.util.Objects;

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
        return getId() == that.getId() &&
                Objects.equals(getProperty(), that.getProperty()) &&
                Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProperty(), getValue());
    }
}
