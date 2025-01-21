# SqlParser

This is a java library for parsing sql. It can parse the sql into database, tables, columns, rows, etc. It can be used to analyze the sql and get the database structure and data.

## Quick Start

The following code shows how to parse a simple sql with the library.

```java
import com.devswing.sqlparser.*;

// a simple sql with table definition and data
String sql = "create table test (\n" +
        "  id int(11) NOT NULL COMMENT 'ID : 用户编码' ,\n" +
        "  type int(2) NOT NULL COMMENT ' : 用户类型 : 1-普通用户,2-VIP用户, 3-SVIP',\n" +
        "  product int(4) NOT NULL COMMENT '产品类型 : 1-产品1, 2-产品2, 3-产品3',\n" +
        "  permit int(2) NOT NULL COMMENT '许可 : 1-许可1, 2-许可2, 3-许可3',\n" +
        "  PRIMARY KEY (id, type, product)\n" +
        "); \n" +
        "Insert into test (id, type, product, permit) values (3,1,38,1);\n" +
        "Insert into test (id, type, product, permit) values (3,1,39,1);\n" +
        "Insert into test (id, type, product, permit) values (3,13,8,1);\n";

// create a database object
Database db = new Database("test");
// parse the sql
SqlParser parser = new SqlParser();
parser.parseFromString(sql, db);

// get the tables definition and data
TreeMap<String, TableDefinition> tables = db.getTablesDefinition();
TreeMap<String, TableData> tablesData = db.getTablesData();

Map<String, ColumnDefinition> columns = table.getColumns();
for (Map.Entry<String, ColumnDefinition> entry : columns.entrySet()) {
    String columnName = entry.getKey();
    ColumnDefinition column = entry.getValue();
    System.out.println(columnName + " : " + column.getProperty('type') + " : " + column.getProperty('comment'));
}

TableData tableData = tablesData.get("test");
Map<String, RowData> rows = tableData.getRows();
for (Map.Entry<String, RowData> entry : rows.entrySet()) {
    String rowId = entry.getKey();
    RowData row = entry.getValue();
    System.out.println(rowId + " : " + row.getField('id') + " : " + row.getField('type'));
}
```

## Reference

* SqlParser: the main class for parsing
* Database: the database object, which contains the tables definition and data
* TableDefinition: the table definition object, which contains the columns definition, primary key, etc.
* TableData: the table data object, which contains the data rows
* ColumnDefinition: the column definition object, which contains the column name, type, etc.
* Row: the row object, which contains the row data
* IndexDefinition: the index definition object, which contains the index name, columns, etc.
* ForeignKeyDefinition: the foreign key definition object, which contains the foreign key name, columns, etc.

