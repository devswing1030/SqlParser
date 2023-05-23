package com.devswing.sqlparser.mysql;

import java.util.ArrayList;
import java.util.List;

public class MySqlTableDefinition extends com.devswing.sqlparser.TableDefinition {
    private final List<MySqlColumnDefinition> columns;

    public MySqlTableDefinition() {
        columns = new ArrayList<>();
    }

    public void addColumn(MySqlColumnDefinition column) {
        columns.add(column);
    }

    public List<MySqlColumnDefinition> getColumns() {
        return columns;
    }
}
