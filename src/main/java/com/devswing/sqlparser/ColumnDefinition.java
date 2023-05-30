package com.devswing.sqlparser;

import java.util.Hashtable;

public class ColumnDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }

    public void setDefaultProperties() {
        this.setProperty("defaultValue", "NULL");
        this.setProperty("notNull", "false");
        this.setProperty("autoIncrement", "false");
        this.setProperty("comment", "");
    }
}
