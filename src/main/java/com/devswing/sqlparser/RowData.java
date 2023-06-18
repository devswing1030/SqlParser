package com.devswing.sqlparser;

import java.util.Hashtable;

public class RowData {
    private Hashtable<String, String> fields = new Hashtable<>();

    public void addField(String key, String value) {
        fields.put(key, value);
    }

    public String getField(String key) {
        return fields.get(key);
    }

    public Hashtable<String, String> getFields() {
        return fields;
    }

    public void setFields(Hashtable<String, String> fields) {
        this.fields = fields;
    }
}
