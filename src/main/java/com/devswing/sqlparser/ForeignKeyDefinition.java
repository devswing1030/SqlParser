package com.devswing.sqlparser;

import java.util.ArrayList;
import java.util.Hashtable;

public class ForeignKeyDefinition {
    private final ArrayList<String> columns = new ArrayList<String>();

    private String referencedTable;

    private final ArrayList<String> referencedColumns = new ArrayList<String>();

    private final Hashtable<String, String> properties = new Hashtable<String, String>();

    public String getOnDelete() {
        if (properties.get("onDelete") == null) {
            return "NO ACTION";
        }
        return properties.get("onDelete");
    }

    public String getOnUpdate() {
        if (properties.get("onUpdate") == null) {
            return "NO ACTION";
        }
        return properties.get("onUpdate");
    }

    public void addColumn(String column) {
        columns.add(column);
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setReferencedTable(String referencedTable) {
        this.referencedTable = referencedTable;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public void addReferencedColumn(String referencedColumn) {
        referencedColumns.add(referencedColumn);
    }

    public ArrayList<String> getReferencedColumns() {
        return referencedColumns;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }
}
