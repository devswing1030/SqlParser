package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.Hashtable;

public class IndexDefinition {
    private final ArrayList<String> columns = new ArrayList<>();

    private final Hashtable<String, String> properties = new Hashtable<>();

    public void addColumn(String column) {
        columns.add(column);
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }
}
