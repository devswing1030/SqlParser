package com.devswing.sqlparser;

import java.util.ArrayList;

public class KeyDefinition {
    private final ArrayList<String> columns = new ArrayList<>();
    private Boolean unique = false;

    private Boolean dropped = false;

    public void addColumn(String column) {
        columns.add(column);
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setDropped(Boolean dropped) {
        this.dropped = dropped;
    }

    public Boolean getDropped() {
        return dropped;
    }
}
