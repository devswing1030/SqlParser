package com.devswing.sqlparser;

import java.util.Hashtable;

public class ColumnDefinition {
    private final Hashtable<String, String> properties = new Hashtable<>();
    private boolean dropped = false;

    public void setDropped(boolean dropped) {
        this.dropped = dropped;
    }

    public boolean getDropped() {
        return dropped;
    }


    public void setProperty(String key, String value) {
        properties.put(key, value);
    }


    public String getProperty(String key) {
        return properties.get(key);
    }
}
