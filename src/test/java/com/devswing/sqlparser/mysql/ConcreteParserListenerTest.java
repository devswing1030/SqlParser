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
                ");\n" +
                "alter table test drop primary key;\n"

                ;

        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);
        Map<String, ColumnDefinition> columns = table.getColumns();
        assert(columns.size() == 4);
        ColumnDefinition column = columns.get("id");
        assert(column != null);
        assert(column.getProperty("primaryKey").equals("false"));
        assert(column.getProperty("oldPrimaryKey").equals("true"));
        column = columns.get("type");
        assert(column != null);
        assert(column.getProperty("primaryKey").equals("false"));
        assert(column.getProperty("oldPrimaryKey").equals("true"));
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
                ");\n" +
                "Alter table test drop foreign key FK_key1;\n" +
                "Alter table test add foreign key FK_key2 (id) REFERENCES ID (id);\n" +
                "Alter table test drop index key1;\n" +
                "Alter table test add index idx_comment (comment);"
                ;

        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        // check keys
        Hashtable<String, IndexDefinition> keys = table.getIndexs();
        assert(keys.size() == 5);

        IndexDefinition key = keys.get("key1");
        assert(key != null);
        assert(key.getProperty("unique") == null);
        ArrayList<String> columns = key.getColumns();
        assert(columns.size() == 2);
        assert(columns.get(0).equals("id"));
        assert(columns.get(1).equals("type"));
        assert(key.getProperty("dropped").equals("true"));

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

        key = keys.get("idx_comment");
        assert(key != null);
        assert(key.getProperty("unique")==null);
        columns = key.getColumns();
        assert(columns.size() == 1);
        assert(columns.get(0).equals("comment"));

        // check foreign keys
        Hashtable<String, ForeignKeyDefinition> foreignKeys = table.getForeignKeys();
        assert(foreignKeys.size() == 3);

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
        assert(foreignKey.getProperty("dropped").equals("true"));

        foreignKey = foreignKeys.get("FK_key2");
        assert(foreignKey != null);
        assert(foreignKey.getProperty("added").equals("true"));




    }


    @Test
    void ChangedTables() {
        String sql = "create table test (id int(2));\n" +
                "alter table test RENAME TO `user`;\n" +
                "alter table test comment 'test';\n"
                ;
        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);
        assert(table.getProperty("name").equals("user"));
        assert(table.getProperty("oldName").equals("test"));

        assert(table.getProperty("altered").equals("true"));

        assert(table.getProperty("comment").equals("test"));
        assert(table.getProperty("oldComment").equals(""));
    }

    @Test
    void ChangeTables_Alter_Column() {
        String sql = "CREATE TABLE test (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
                "  type int(2) NOT NULL,\n" +
                "  name varchar(255) DEFAULT NULL,\n" +
                "  comment varchar(255) DEFAULT NULL);\n" +
                "alter table test drop column name;\n" +
                "alter table test rename column `comment` to `description`;\n" +
                "alter table test modify column id int(18) not null COMMENT 'User ID';\n"
                ;
        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        Hashtable<String, ColumnDefinition> columns = table.getColumns();
        assert(columns.size() == 4);

        ColumnDefinition column = columns.get("name");
        assert(column != null);
        assert(column.getProperty("dropped").equals("true"));

        column = columns.get("description");
        assert(column != null);
        assert(column.getProperty("name").equals("description"));
        assert(column.getProperty("oldName").equals("comment"));

        column = columns.get("id");
        assert(column != null);
        assert(column.getProperty("type").equals("int(18)"));
        assert(column.getProperty("oldType").equals("int(11)"));
        assert(column.getProperty("notNull").equals("true"));
        assert(column.getProperty("oldNotNull")==null);
        assert(column.getProperty("autoIncrement").equals("false"));
        assert(column.getProperty("oldAutoIncrement").equals("true"));

        assert(column.getProperty("comment").equals("User ID"));
        assert(column.getProperty("oldComment").equals("ID"));
    }

    @Test
    void DroppedTables() {
        String sql = "create table test (id int(11));\n" +
                "create table user (id int(11));\n" +
                "drop table test, user;\n" +
                "drop table if exists user1;"
                ;
        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 2);
        TableDefinition table = tables.get("test");
        assert(table != null);
        assert(table.getProperty("dropped").equals("true"));

        table = tables.get("user");
        assert(table != null);
        assert(table.getProperty("dropped").equals("true"));

    }

    @Test
    void AddColumn() {
        String sql = "create table test (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT ,\n" +
                "  type int(2) NOT NULL\n" +
                ");\n" +
                "alter table test add column name varchar(255) first;\n" +
                "alter table test add column course varchar(255) after id;\n" +
                "alter table test add column description varchar(255) ;\n" ;


        Map<String, TableDefinition> tables = getTables(sql);
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");


        List<ColumnDefinition> columnSeq = table.getColumnSequence();

        assert(columnSeq.size() == 5);
        assert(columnSeq.get(0).getProperty("name").equals("name"));
        assert(columnSeq.get(0).getProperty("added").equals("true"));
        assert(columnSeq.get(1).getProperty("name").equals("id"));
        assert(columnSeq.get(2).getProperty("name").equals("course"));
        assert(columnSeq.get(2).getProperty("added").equals("true"));
        assert(columnSeq.get(3).getProperty("name").equals("type"));
        assert(columnSeq.get(4).getProperty("name").equals("description"));
        assert(columnSeq.get(4).getProperty("added").equals("true"));

    }

    @Test
    void ParseTableComment() {
        String sql = "create table test (id int(12)) \n" +
                "comment '测试表 : this is a test table : 应用数据 :  增量更新';\n" +
                "alter table test comment '测试表a : this is a test table a : 元数据 :  增量更新';\n"
                ;
        TreeMap<String, TableDefinition> tables = new TreeMap<>();
        SqlParser parser = new SqlParser();
        parser.setParseComment(true);
        parser.parseFromString(sql, tables);

        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        assert(table.getProperty("localName").equals("测试表a"));
        assert(table.getProperty("oldLocalName").equals("测试表"));
        assert(table.getProperty("description").equals("this is a test table a"));
        assert(table.getProperty("oldDescription").equals("this is a test table"));
        assert(table.getTags().get(0).equals("元数据"));
        assert(table.getTags().get(1).equals("增量更新"));

        Hashtable<String, String> tagStatus = table.getTagStatus();
        assert(tagStatus.get("元数据").equals("added"));
        assert(tagStatus.get("应用数据").equals("dropped"));
        assert(tagStatus.get("增量更新").equals(""));

    }

    @Test
    void parseColumnComment() {
        String sql = "create table test (\n" +
                "  id int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID : 用户编码' ,\n" +
                "  type int(2) NOT NULL COMMENT ' : 用户类型 : enumVal 1-普通用户,2-VIP用户, 3-SVIP',\n" +
                "  name varchar(255) DEFAULT NULL\n" +
                "); \n" +
                "alter table test modify column type int(2) NOT NULL COMMENT '类型 : : enumVal 1-普通用户,2-VIP客户, 4-SuperVIP';\n" +
                "alter table test modify column id int(12) NOT NULL AUTO_INCREMENT COMMENT 'ID : 用户编码 ';\n"
                ;
        TreeMap<String, TableDefinition> tables = new TreeMap<>();
        SqlParser parser = new SqlParser();
        parser.setParseComment(true);
        parser.parseFromString(sql, tables);

        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        List<ColumnDefinition> columns = table.getColumnSequence();
        assert(columns.size() == 3);


        ColumnDefinition column = columns.get(0);
        assert(column.getProperty("localName").equals("ID"));
        assert(column.getProperty("oldLocalName") == null);
        assert(column.getProperty("description").equals("用户编码"));
        assert(column.getProperty("oldDescription") == null);

        column = columns.get(1);
        assert(column.getProperty("localName").equals("类型"));
        assert(column.getProperty("oldLocalName") == null);
        assert(column.getProperty("description") == null);
        assert(column.getProperty("oldDescription").equals("用户类型"));

        TreeMap enums = column.getEnums();
        assert(enums.size() == 3);
        assert(enums.get("1").equals("普通用户"));
        assert(enums.get("2").equals("VIP客户"));
        assert(enums.get("4").equals("SuperVIP"));

        TreeMap oldEnums = column.getOldEnums();
        assert(oldEnums.size() == 3);
        assert(oldEnums.get("1").equals("普通用户"));
        assert(oldEnums.get("2").equals("VIP用户"));
        assert(oldEnums.get("3").equals("SVIP"));



    }
}