package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TableDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();

    private ArrayList<String> tags = new ArrayList<>();

    private Hashtable<String, String> tagStatus = new Hashtable<>();
    private final Hashtable<String, ColumnDefinition> columns = new Hashtable<>();

    private final ArrayList<ColumnDefinition> columnSequence = new ArrayList<>();

    private final Hashtable<String, IndexDefinition> indexs = new Hashtable<>();

    private final Hashtable<String, ForeignKeyDefinition> foreignKeys = new Hashtable<>();



    public TableDefinition() {
        setDefaultProperties();
    }
    public void setDefaultProperties() {
        this.setProperty("comment", "");
    }

    public String getLocalName() {
        if (getProperty("localName") == null) {
            return "";
        }
        return getProperty("localName");
    }

    public String getDescription() {
        if (getProperty("description") == null) {
            return "";
        }
        return getProperty("description");
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

    public ArrayList<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }


    public Hashtable<String, String> getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(Hashtable<String, String> tagStatus) {
        this.tagStatus = tagStatus;
    }

    public String getName() {
        return getProperty("name");
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

    public List<ColumnDefinition> getColumnSequenceRevision() {
        return columnSequence;
    }

    public List<ColumnDefinition> getColumnSequence() {
        ArrayList<ColumnDefinition> columnSequence = new ArrayList<>();
        for (ColumnDefinition column : this.columnSequence) {
            if (column.getProperty("dropped") == null) {
                columnSequence.add(column);
            }
        }
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

    ArrayList<String> getPrimaryKeyColumns() {
        ArrayList<String> primaryKeyColumns = new ArrayList<>();
        for (ColumnDefinition column : columnSequence) {
            if (column.getProperty("primaryKey") != null) {
                if (column.getProperty("primaryKey").equals("true"))
                    primaryKeyColumns.add(column.getProperty("name"));
            }
        }
        return primaryKeyColumns;
    }

}

