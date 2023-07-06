package com.devswing.sqlparser.mysql;

import com.devswing.sqlparser.*;
import org.junit.jupiter.api.Test;

import java.util.*;

class ConcreteParserListenerTest {
    private static TreeMap<String, TableDefinition>  getTables(String sql) {
        TreeMap<String, TableDefinition> tables = new TreeMap<>();

        SqlParser parser = new SqlParser();
        parser.parseFromString(sql, tables);

        return tables;
    }

    @org.junit.jupiter.api.Test
    void createTables() {
        String sql = "CREATE TABLE  `test` (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                "  name varchar(255) DEFAULT NULL,\n" +
                "  comment varchar(255) DEFAULT NULL\n" +
                ")\n" +
                "ENGINE=InnoDB\n" +
                "DEFAULT CHARSET=utf8mb4\n" +
                "COLLATE=utf8mb4_unicode_ci\n" +
                "COMMENT='test comment';\n" +
                "CREATE TABLE test2 (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY);"
                ;

        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 2);
        TableDefinition table = tables.get("test");
        assert(table != null);
        assert(table.getProperty("name").equals("test"));
        //assert(table.getProperty("engine").equals("InnoDB"));
        //assert(table.getProperty("charset").equals("utf8mb4"));
        //assert(table.getProperty("collate").equals("utf8mb4_unicode_ci"));
        assert(table.getProperty("comment").equals("test comment"));

        Map<String, ColumnDefinition> columns = table.getColumns();
        assert(columns.size() == 3);

        ColumnDefinition column = columns.get("id");
        assert(column != null);
        assert(column.getProperty("name").equals("id"));
        assert(column.getProperty("type").equals("int(11)"));
        assert(column.getProperty("autoIncrement").equals("true"));
        assert(column.getProperty("primaryKey").equals("true"));
        assert(column.getProperty("notNull").equals("true"));
        assert(column.getProperty("defaultValue").equals("NULL"));

        column = columns.get("name");
        assert(column != null);
        assert(column.getProperty("name").equals("name"));
        assert(column.getProperty("type").equals("varchar(255)"));
        assert(column.getProperty("autoIncrement").equals("false"));
        assert(column.getProperty("notNull").equals("false"));
        assert(column.getProperty("defaultValue").equals("NULL"));

        column = columns.get("comment");
        assert(column != null);
        assert(column.getProperty("name").equals("comment"));
        assert(column.getProperty("type").equals("varchar(255)"));
        assert(column.getProperty("autoIncrement").equals("false"));
        assert(column.getProperty("notNull").equals("false"));
        assert(column.getProperty("defaultValue").equals("NULL"));
    }

    @Test
    void CreateTable_PrimaryKey() {
        String sql = "CREATE TABLE test (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT ,\n" +
                "  type int(2) NOT NULL,\n" +
                "  name varchar(255) DEFAULT NULL,\n" +
                "  comment varchar(255) DEFAULT NULL,\n" +
                "\n" +
                "PRIMARY KEY (id, type)\n" +
                ");\n"
                ;

        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);
        Map<String, ColumnDefinition> columns = table.getColumns();
        assert(columns.size() == 4);
        ColumnDefinition column = columns.get("id");
        assert(column != null);
        assert(column.getProperty("primaryKey").equals("true"));
        column = columns.get("type");
        assert(column != null);
        assert(column.getProperty("primaryKey").equals("true"));
    }

    @Test
    void CreateTable_Key() {
        String sql = "CREATE TABLE test (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT ,\n" +
                "  type int(2) NOT NULL,\n" +
                "  name varchar(255) DEFAULT NULL,\n" +
                "  person_id int(11) NOT NULL UNIQUE,\n" +
                "  comment varchar(255) DEFAULT NULL,\n" +
                "\n" +
                "KEY key1 (id, type),\n" +
                "INDEX (type),\n" +
                "UNIQUE KEY key2 (name)," +
                "CONSTRAINT  FOREIGN KEY (id, type) REFERENCES Type (id, type) ON DELETE CASCADE,\n" +
                "CONSTRAINT FK_key1 FOREIGN KEY (id) REFERENCES ID (id)\n" +
                ");\n"
                ;

        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        // check keys
        Hashtable<String, IndexDefinition> keys = table.getIndexs();
        assert(keys.size() == 4);

        IndexDefinition key = keys.get("key1");
        assert(key != null);
        assert(key.getProperty("unique") == null);
        ArrayList<String> columns = key.getColumns();
        assert(columns.size() == 2);
        assert(columns.get(0).equals("id"));
        assert(columns.get(1).equals("type"));

        key = keys.get("idx_type");
        assert(key != null);
        assert(key.getProperty("unique")==null);
        columns = key.getColumns();
        assert(columns.size() == 1);
        assert(columns.get(0).equals("type"));

        key = keys.get("key2");
        assert(key != null);
        assert(key.getProperty("unique").equals("true"));
        columns = key.getColumns();
        assert(columns.size() == 1);
        assert(columns.get(0).equals("name"));

        key = keys.get("uk_person_id");
        assert(key != null);
        assert(key.getProperty("unique").equals("true"));
        columns = key.getColumns();
        assert(columns.size() == 1);
        assert(columns.get(0).equals("person_id"));



        // check foreign keys
        Hashtable<String, ForeignKeyDefinition> foreignKeys = table.getForeignKeys();
        assert(foreignKeys.size() == 2);

        ForeignKeyDefinition foreignKey = foreignKeys.get("fk_idtype");
        assert(foreignKey != null);
        assert(foreignKey.getReferencedTable().equals("Type"));
        assert(foreignKey.getReferencedColumns().size() == 2);
        assert(foreignKey.getReferencedColumns().get(0).equals("id"));
        assert(foreignKey.getReferencedColumns().get(1).equals("type"));
        assert(foreignKey.getColumns().size() == 2);
        assert(foreignKey.getColumns().get(0).equals("id"));
        assert(foreignKey.getColumns().get(1).equals("type"));
        assert(foreignKey.getProperty("onDelete").equals("CASCADE"));

        foreignKey = foreignKeys.get("FK_key1");
        assert(foreignKey != null);
        assert(foreignKey.getReferencedTable().equals("ID"));
        assert(foreignKey.getReferencedColumns().size() == 1);
        assert(foreignKey.getReferencedColumns().get(0).equals("id"));
        assert(foreignKey.getColumns().size() == 1);
        assert(foreignKey.getColumns().get(0).equals("id"));

    }


    @Test
    void ParseTableComment() {
        String sql = "create table test (id int(12)) \n" +
                "comment '测试表 : this is a test table : 应用数据 :  增量更新';\n" +
                "create table user(id int(11)) comment '用户表 : this is a test table : 应用数据 :  增量更新';\n"
                ;
        TreeMap<String, TableDefinition> tables = new TreeMap<>();
        SqlParser parser = new SqlParser();
        parser.setParseComment(true);
        parser.parseFromString(sql, tables);

        assert(tables.size() == 2);
        TableDefinition table = tables.get("test");
        assert(table != null);

        assert(table.getProperty("localName").equals("测试表"));
        assert(table.getProperty("description").equals("this is a test table"));
        assert(table.getTags().get(0).equals("应用数据"));
        assert(table.getTags().get(1).equals("增量更新"));

        table = tables.get("user");
        assert(table != null);

        assert(table.getProperty("localName").equals("用户表"));
        assert(table.getProperty("description").equals("this is a test table"));
        assert(table.getTags().get(0).equals("应用数据"));
        assert(table.getTags().get(1).equals("增量更新"));
    }

    @Test
    void parseColumnComment() {
        String sql = "create table test (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID : 用户编码' ,\n" +
                "  type int(2) NOT NULL COMMENT ' : 用户类型 : 1-普通用户,2-VIP用户, 3-SVIP',\n" +
                "  name varchar(255) DEFAULT NULL COMMENT '姓名: 用户姓名'\n" +
                "); \n"
                ;
        TreeMap<String, TableDefinition> tables = new TreeMap<>();
        SqlParser parser = new SqlParser();
        parser.setParseComment(true);
        parser.parseFromString(sql, tables);

        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        List<ColumnDefinition> columns = table.getColumnSequenceRevision();
        assert(columns.size() == 3);


        ColumnDefinition column = columns.get(0);
        assert(column.getProperty("localName").equals("ID"));
        assert(column.getProperty("description").equals("用户编码"));

        column = columns.get(1);
        assert(column.getProperty("localName")==null);
        assert(column.getProperty("description").equals("用户类型"));
        TreeMap<String, String> enums = column.getEnums();
        assert(enums.size() == 3);
        assert(enums.get("1").equals("普通用户"));
        assert(enums.get("2").equals("VIP用户"));
        assert(enums.get("3").equals("SVIP"));

        column = columns.get(2);
        assert(column.getProperty("localName").equals("姓名"));
        assert(column.getProperty("description").equals("用户姓名"));
    }

    @Test
    void loadData() {
        String sql = "create table test (\n" +
                "  id int(11) NOT NULL Primary Key COMMENT 'ID : 用户编码' ,\n" +
                "  type int(2) NOT NULL COMMENT ' : 用户类型 : 1-普通用户,2-VIP用户, 3-SVIP',\n" +
                "  name varchar(255) DEFAULT NULL\n" +
                "); \n" +
                "Insert into test (id, type, name) values (1, 1, \"te'st\");\n" +
                "Insert into test (id, type, name) values (2, 2, 'tes\"t2');\n" +
                "Insert into test (id, type, name) values (3, 3, 'test3');\n" +
                "Insert into test (id, type, name) values (4, 2, 'test4');\n"
                ;
        TreeMap<String, TableDefinition> tables = new TreeMap<>();
        TreeMap<String, TableData> tablesData = new TreeMap<>();
        SqlParser parser = new SqlParser();
        parser.setParseComment(true);
        parser.parseFromString(sql, tables, tablesData);

        assert(tablesData.size() == 1);
        TableData tableData = tablesData.get("test");
        assert(tableData != null);



        assert(tableData.getRows().size() == 4);
        assert(tableData.getRows().get("1").getField("id").equals("1"));
        assert(tableData.getRows().get("1").getField("type").equals("1"));
        assert(tableData.getRows().get("1").getField("name").equals("te'st"));

        assert(tableData.getRows().get("2").getField("id").equals("2"));
        assert(tableData.getRows().get("2").getField("type").equals("2"));
        assert(tableData.getRows().get("2").getField("name").equals("tes\"t2"));

        assert(tableData.getRows().get("3").getField("id").equals("3"));
        assert(tableData.getRows().get("3").getField("type").equals("3"));
        assert(tableData.getRows().get("3").getField("name").equals("test3"));

        assert(tableData.getRows().get("4").getField("id").equals("4"));
        assert(tableData.getRows().get("4").getField("type").equals("2"));
        assert(tableData.getRows().get("4").getField("name").equals("test4"));



    }
}