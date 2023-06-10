package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class TableDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();
    private final Hashtable<String, ColumnDefinition> columns = new Hashtable<>();

    private final ArrayList<ColumnDefinition> columnSequence = new ArrayList<>();

    private final Hashtable<String, IndexDefinition> indexs = new Hashtable<>();

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

    public void removeColumn(String name) {
        columns.remove(name);
        int index = 0;
        for (ColumnDefinition col : columnSequence) {
            if (col.getProperty("name").equals(name)) {
                columnSequence.remove(index);
                break;
            }
            index++;
        }
    }

    public void renameColumn(String oldName, String newName) {
        ColumnDefinition column = columns.get(oldName);
        column.setProperty("name", newName);
        columns.remove(oldName);
        columns.put(newName, column);
        int index = 0;
        for (ColumnDefinition col : columnSequence) {
            if (col.getProperty("name").equals(oldName)) {
                columnSequence.remove(index);
                columnSequence.add(index, column);
                break;
            }
            index++;
        }
    }

    public void addColumnToFirst(String name, ColumnDefinition column) {
        columns.put(name, column);
        columnSequence.add(0, column);
    }

    public void addColumnAfter(String name, ColumnDefinition column, String after) {
        columns.put(name, column);
        int index = 0;
        for (ColumnDefinition col : columnSequence) {
            if (col.getProperty("name").equals(after)) {
                columnSequence.add(index + 1, column);
                break;
            }
            index++;
        }
    }

    public void addIndex(String name, IndexDefinition key) {
        indexs.put(name, key);
    }

    public IndexDefinition getIndex(String name) {
        return indexs.get(name);
    }

    public Hashtable<String, IndexDefinition> getIndexs() {
        return indexs;
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

    public void removeForeignKey(String name) {
        foreignKeys.remove(name);
    }
}

