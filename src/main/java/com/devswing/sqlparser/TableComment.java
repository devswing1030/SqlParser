package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.Hashtable;

public class TableComment {
    private final Hashtable<String, String> properties = new Hashtable<>();
    private final ArrayList<String> tags = new ArrayList<>();

    public void setProperty(String key, String value) {

        properties.put(key, value);
    }
    public String getProperty(String key) {
        return properties.get(key);
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

}
