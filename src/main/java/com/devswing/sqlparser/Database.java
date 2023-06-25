package com.devswing.sqlparser;

import java.util.Hashtable;
import java.util.TreeMap;

public class Database {
    private String name;

    private TreeMap<String, TableDefinition> tablesDefinition = new TreeMap<>();
    private final TreeMap<String, TableData> tablesData = new TreeMap<>();

    private final Hashtable<String, String> properties = new Hashtable<>();


    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public TreeMap<String, TableDefinition> getTablesDefinition() {
        return tablesDefinition;
    }

    public TreeMap<String, TableData> getTablesData() {
        return tablesData;
    }

    public void setTablesDefinition(TreeMap<String, TableDefinition> tablesDefinition) {
        this.tablesDefinition = tablesDefinition;
    }

    public void setTableDefinition(String tableName, TableDefinition tableDefinition) {
        this.tablesDefinition.put(tableName, tableDefinition);
    }

    public TableDefinition getTableDefinition(String tableName) {
        return this.tablesDefinition.get(tableName);
    }

    public void setTableData(String tableName, TableData tableData) {
        this.tablesData.put(tableName, tableData);
    }

    public TableData getTableData(String tableName) {
        return this.tablesData.get(tableName);
    }

    public void setProperty(String key, String value) {
        if (value == null) {
            properties.remove(key);
        }
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public void setProperties(Hashtable<String, String> properties) {
        this.properties.putAll(properties);
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }
}
