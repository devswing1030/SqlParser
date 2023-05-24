package com.devswing.sqlparser;

import java.util.ArrayList;

public class ForeignKeyDefinition {
    private final ArrayList<String> columns = new ArrayList<String>();

    private String referencedTable;

    private final ArrayList<String> referencedColumns = new ArrayList<String>();

    private Boolean dropped = false;

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

    public void setDropped(Boolean dropped) {
        this.dropped = dropped;
    }

    public Boolean getDropped() {
        return dropped;
    }

}
