package com.devswing.sqlparser;

import java.util.Hashtable;

public class RowData {
    private Hashtable<String, String> fields = new Hashtable<>();

    private String insertSql;

    public void addField(String key, String value) {
        fields.put(key, value);
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }

    public String getField(String key) {
        return fields.get(key);
    }

    public void setField(String key, String value) {
        fields.put(key, value);
    }

    public Hashtable<String, String> getFields() {
        return fields;
    }

    public void setFields(Hashtable<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public RowData clone() {
        RowData rowData = new RowData();
        rowData.setFields((Hashtable<String, String>) fields.clone());
        return rowData;
    }
}
