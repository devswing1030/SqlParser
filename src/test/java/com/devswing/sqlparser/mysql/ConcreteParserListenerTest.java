package com.devswing.sqlparser.mysql;

import com.devswing.sqlparser.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.util.*;

class ConcreteParserListenerTest {
    private static ConcreteParserListener getConcreteParserListener(String sql) {
        MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(sql));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener();

        MySqlParser.RootContext tree = parser.root();
        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return listener;
    }

    @org.junit.jupiter.api.Test
    void createTables() {
        String sql = "CREATE TABLE test (\n" +
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

        ConcreteParserListener listener = getConcreteParserListener(sql);

        Map<String, TableDefinition> tables = listener.getTables();
        assert(tables.size() == 2);
        TableDefinition table = tables.get("test");
        assert(table != null);
        assert(table.getProperty("name").equals("test"));
        //assert(table.getProperty("engine").equals("InnoDB"));
        //assert(table.getProperty("charset").equals("utf8mb4"));
        //assert(table.getProperty("collate").equals("utf8mb4_unicode_ci"));
        assert(table.getProperty("comment").equals("'test comment'"));

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
                ");"
                ;

        ConcreteParserListener listener = getConcreteParserListener(sql);
        Map<String, TableDefinition> tables = listener.getTables();
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
                "  comment varchar(255) DEFAULT NULL,\n" +
                "\n" +
                "KEY key1 (id, type),\n" +
                "KEY (type),\n" +
                "UNIQUE KEY key2 (name)," +
                "CONSTRAINT  FOREIGN KEY (id, type) REFERENCES Type (id, type),\n" +
                "CONSTRAINT FK_key1 FOREIGN KEY (id) REFERENCES ID (id)\n" +
                ");"
                ;

        ConcreteParserListener listener = getConcreteParserListener(sql);
        Map<String, TableDefinition> tables = listener.getTables();
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        // check keys
        Hashtable<String, KeyDefinition> keys = table.getKeys();
        assert(keys.size() == 3);

        KeyDefinition key = keys.get("key1");
        assert(key != null);
        assert(!key.getUnique());
        ArrayList<String> columns = key.getColumns();
        assert(columns.size() == 2);
        assert(columns.get(0).equals("id"));
        assert(columns.get(1).equals("type"));

        key = keys.get("idx_type");
        assert(key != null);
        assert(!key.getUnique());
        columns = key.getColumns();
        assert(columns.size() == 1);
        assert(columns.get(0).equals("type"));

        key = keys.get("key2");
        assert(key != null);
        assert(key.getUnique());
        columns = key.getColumns();
        assert(columns.size() == 1);
        assert(columns.get(0).equals("name"));

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

        foreignKey = foreignKeys.get("FK_key1");
        assert(foreignKey != null);
        assert(foreignKey.getReferencedTable().equals("ID"));
        assert(foreignKey.getReferencedColumns().size() == 1);
        assert(foreignKey.getReferencedColumns().get(0).equals("id"));
        assert(foreignKey.getColumns().size() == 1);
        assert(foreignKey.getColumns().get(0).equals("id"));




    }


    @Test
    void ChangedTables() {
        String sql = "alter table test RENAME TO user;";
        ConcreteParserListener listener = getConcreteParserListener(sql);
        Map<String, TableDefinition> tables = listener.getAlteredTables();
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);
        assert(table.getProperty("name").equals("user"));
    }

    @Test
    void ChangeTables_Alter_Column() {
        String sql = "alter table test drop column name;\n" +
                "alter table test rename column comment to description;\n" +
                "alter table test modify column id int(18) not null;\n"
                ;
        ConcreteParserListener listener = getConcreteParserListener(sql);
        Map<String, TableDefinition> tables = listener.getAlteredTables();
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");
        assert(table != null);

        Hashtable<String, ColumnDefinition> columns = table.getColumns();
        assert(columns.size() == 3);

        ColumnDefinition column = columns.get("name");
        assert(column != null);
        assert(column.getProperty("dropped").equals("true"));

        column = columns.get("comment");
        assert(column != null);
        assert(column.getProperty("name").equals("description"));
        assert(column.getProperty("oldName").equals("comment"));

        column = columns.get("id");
        assert(column != null);
        assert(column.getProperty("type").equals("int(18)"));
        assert(column.getProperty("notNull").equals("true"));
        assert(column.getProperty("autoIncrement").equals("false"));
    }

    @Test
    void DroppedTables() {
        String sql = "drop table test, user;";
        ConcreteParserListener listener = getConcreteParserListener(sql);
        HashSet<String> tables = listener.getDroppedTables();
        assert(tables.size() == 2);
        assert(tables.contains("test"));
        assert(tables.contains("user"));
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

        System.out.println(sql);
        ConcreteParserListener listener = getConcreteParserListener(sql);

        Map<String, TableDefinition> tables = listener.getTables();
        assert(tables.size() == 1);
        TableDefinition table = tables.get("test");

        Map<String, TableDefinition> alterTables = listener.getAlteredTables();
        assert(alterTables.size() == 1);
        TableDefinition alterTable = alterTables.get("test");
        assert(alterTable != null);
        Hashtable<String, ColumnDefinition> columns = alterTable.getColumns();
        assert(columns.size() == 3);
        ColumnDefinition column = columns.get("name");
        assert(column != null);
        assert(column.getProperty("type").equals("varchar(255)"));
        assert(column.getProperty("position").equals("FIRST"));
        table.addColumn(column.getProperty("name"), column);

        column = columns.get("course");
        assert(column != null);
        assert(column.getProperty("type").equals("varchar(255)"));
        assert(column.getProperty("position").equals("id"));
        table.addColumn(column.getProperty("name"), column);

        column = columns.get("description");
        assert(column != null);
        table.addColumn(column.getProperty("name"), column);


        assert(table.getColumns().size() == 5);
        List<ColumnDefinition> columnSeq = table.getColumnSequence();
        assert(columnSeq.size() == 5);
        assert(columnSeq.get(0).getProperty("name").equals("name"));
        assert(columnSeq.get(1).getProperty("name").equals("id"));
        assert(columnSeq.get(2).getProperty("name").equals("course"));
        assert(columnSeq.get(3).getProperty("name").equals("type"));
        assert(columnSeq.get(4).getProperty("name").equals("description"));

    }
}