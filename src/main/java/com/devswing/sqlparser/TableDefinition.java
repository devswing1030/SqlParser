package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class TableDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();
    private final Hashtable<String, ColumnDefinition> columns = new Hashtable<>();

    private final ArrayList<ColumnDefinition> columnSequence = new ArrayList<>();

    private final HashSet<String> primaryKey = new HashSet<>();
    private final Hashtable<String, KeyDefinition> keys = new Hashtable<>();

    private final Hashtable<String, ForeignKeyDefinition> foreignKeys = new Hashtable<>();

    public void setProperty(String key, String value) {

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

    public void addColumn(String name, ColumnDefinition column) {

        columns.put(name, column);

        if (column.getProperty("position") != null) {
            String position = column.getProperty("position");
            if (position.equals("FIRST")) {
                columnSequence.add(0, column);
            } else  {
                // if position not equal FIRST, then it is the after column name
                int index = 0;
                for (ColumnDefinition col : columnSequence) {
                    if (col.getProperty("name").equals(position)) {
                        columnSequence.add(index + 1, column);
                        break;
                    }
                    index++;
                }
            }
        } else {
            columnSequence.add(column);
        }

    }

    public void addKey(String name, KeyDefinition key) {
        keys.put(name, key);
    }

    public KeyDefinition getKey(String name) {
        return keys.get(name);
    }

    public Hashtable<String, KeyDefinition> getKeys() {
        return keys;
    }

    public ColumnDefinition getColumn(String name) {
        return columns.get(name);
    }

    public Hashtable<String, ColumnDefinition> getColumns() {
        return columns;
    }

    public List<ColumnDefinition> getColumnSequence() {
        return columnSequence;
    }

    public void addForeignKey(String name, ForeignKeyDefinition foreignKey) {
        foreignKeys.put(name, foreignKey);
    }

    public ForeignKeyDefinition getForeignKey(String name) {
        return foreignKeys.get(name);
    }

    public Hashtable<String, ForeignKeyDefinition> getForeignKeys() {
        return foreignKeys;
    }
}

