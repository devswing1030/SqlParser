package com.devswing.sqlparser;

import java.util.Hashtable;
import java.util.TreeMap;

public class ColumnDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();

    private TreeMap<String, String> enums = new TreeMap<>();

    private TreeMap<String, String> oldEnums = new TreeMap<>();

    private TreeMap<String, String> enumStatus = new TreeMap<>();

    public String getLocalName() {
        if (properties.get("localName") == null) {
            return "";
        }

        return properties.get("localName");
    }

    public String getType() {
        return properties.get("type");
    }

    public boolean isPrimaryKey() {
        if (properties.get("primaryKey") == null) {
            return false;
        }
        return properties.get("primaryKey").equals("true");
    }

    public boolean isNotNull() {
        if (properties.get("notNull") == null) {
            return false;
        }
        return properties.get("notNull").equals("true");
    }

    public boolean isAutoIncrement() {
        if (properties.get("autoIncrement") == null) {
            return false;
        }
        return properties.get("autoIncrement").equals("true");
    }

    public String getDefaultValue() {
        if (properties.get("defaultValue") == null) {
            return "";
        }
        return properties.get("defaultValue");
    }

    public String getDescription() {
        if (properties.get("description") == null) {
            return "";
        }

        return properties.get("description");
    }

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

    public String getName() {
        return getProperty("name");
    }
}
