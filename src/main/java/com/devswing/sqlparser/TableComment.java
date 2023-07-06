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

    /**
     * comment syntax is :
     *    localname : description: tag1 : tag2 : tag3
     * @param commentStr
     */
    public void parse(String commentStr) {
        String[] parts = commentStr.split(":");
        if (parts.length > 0) {
            setProperty("localName", parts[0].trim());
        }
        if (parts.length > 1) {
            setProperty("description", parts[1].trim());
        }
        if (parts.length > 2) {
            for (int i = 2; i < parts.length; i++) {
                addTag(parts[i].trim());
            }
        }


    }

}
