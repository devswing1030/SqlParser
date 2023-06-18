package com.devswing.sqlparser;

import java.util.Hashtable;
import java.util.TreeMap;

public class ColumnDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();

    private TreeMap<String, String> enums = new TreeMap<>();

    private TreeMap<String, String> oldEnums = new TreeMap<>();

    private TreeMap<String, String> enumStatus = new TreeMap<>();

    public void setProperty(String name, String value) {
        if (value == null) {
            properties.remove(name);
            return;
        }

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

    public void setEnum(String name, String value) {
        enums.put(name, value);
    }

    public String getEnum(String name) {
        return  enums.get(name);
    }

    public TreeMap<String, String> getEnums() {
        return enums;
    }

    public void setEnums(TreeMap<String, String> enums) {
        this.enums = enums;
    }

    public void setOldEnums(TreeMap<String, String> oldEnums) {
        this.oldEnums = oldEnums;
    }

    public TreeMap<String, String> getOldEnums() {
        return oldEnums;
    }

    public void setEnumStatus(TreeMap<String, String> enumStatus) {
        this.enumStatus = enumStatus;
    }

    public TreeMap<String, String> getEnumStatus() {
        return enumStatus;
    }
}
