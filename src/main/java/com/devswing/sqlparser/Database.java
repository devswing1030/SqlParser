package com.devswing.sqlparser;

import java.util.TreeMap;

public class Database {
    private String name;

    private final TreeMap<String, TableDefinition> tablesDefinition = new TreeMap<>();
    private final TreeMap<String, TableData> tablesData = new TreeMap<>();


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
}
