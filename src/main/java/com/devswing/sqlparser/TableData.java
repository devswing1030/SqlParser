package com.devswing.sqlparser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeMap;

public class TableData {
    private TreeMap<String, RowData> rows = new TreeMap<>();

    private TableDefinition tableDefinition;

    public TableData() {
    }

    public TableData(TableDefinition tableDefinition) {
        this.tableDefinition = tableDefinition;
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    public void addRow(String key, RowData value) {
        rows.put(key, value);
    }

    public RowData getRow(String key) {
        return rows.get(key);
    }

    public TreeMap<String, RowData> getRows() {
        return rows;
    }

    public void addRow(RowData value) {
        ArrayList<String> primaryKeys = tableDefinition.getPrimaryKeyColumns();
        if (primaryKeys.size() > 0) {
            String key = "";
            for (String primaryKey : primaryKeys) {
                key += value.getField(primaryKey);
            }
            rows.put(key, value);
        }
        else {
            //calculate md5 of the data and use it as key
            String key = "";
            for (ColumnDefinition columns : tableDefinition.getColumnSequenceRevision()){
                key += value.getField(columns.getProperty("name"));
            }
            //calculate md5
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] mdBytes = md.digest(key.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte mdByte : mdBytes) {
                sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
            }
            String md5Hash = sb.toString();

            rows.put(md5Hash, value);
        }
    }

}
