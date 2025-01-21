package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TableDefinition {

    private final Hashtable<String, String> properties = new Hashtable<>();

    private ArrayList<String> tags = new ArrayList<>();

    private final Hashtable<String, ColumnDefinition> columns = new Hashtable<>();

    private final ArrayList<ColumnDefinition> columnSequence = new ArrayList<>();

    private final Hashtable<String, IndexDefinition> indexs = new Hashtable<>();

    private final Hashtable<String, ForeignKeyDefinition> foreignKeys = new Hashtable<>();

    private String createSql;



    public TableDefinition() {
        setDefaultProperties();
    }
    private void setDefaultProperties() {
        this.setProperty("comment", "");
    }

    public void setCreateSql(String createSql) {
        this.createSql = createSql;
    }

    public String getCreateSql() {
        return createSql;
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

    public String getName() {
        return getProperty("name");
    }

    public void addColumn(String name, ColumnDefinition column) {
        columns.put(name, column);
        columnSequence.add(column);
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

    public ArrayList<String> getPrimaryKeyColumns() {
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

