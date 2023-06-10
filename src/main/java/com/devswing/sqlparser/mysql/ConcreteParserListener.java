package com.devswing.sqlparser.mysql;

import com.devswing.sqlparser.*;

import java.util.*;

public class ConcreteParserListener extends MySqlParserBaseListener {

    private TreeMap<String, TableDefinition> tables;
    private TableDefinition currentTable;
    private ColumnDefinition currentColumn;
    private Hashtable<String, String> currentColumnProperties;

    private boolean isAlterTable = false;

    public ConcreteParserListener(TreeMap<String, TableDefinition> tables) {
        this.tables = tables;
    }

    public Map<String, TableDefinition> getTables() {
        return tables;
    }

    public void setTables(TreeMap<String, TableDefinition> tables) {
        this.tables = tables;
    }

    public void enterColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        System.out.println("enterColumnCreateTable");

        isAlterTable = false;
        currentTable = new TableDefinition();
        currentTable.setProperty("name", (ctx.tableName().getText()));
        tables.put(currentTable.getProperty("name"), currentTable);
    }
    public void exitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        System.out.println("exitColumnCreateTable");
    }

    public void enterPrimaryKeyTableConstraint(MySqlParser.PrimaryKeyTableConstraintContext ctx) {
        System.out.println("enterPrimaryKeyTableConstraint");

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            ColumnDefinition column = currentTable.getColumn((indexColumnName.uid().getText()));
            column.setProperty("primaryKey", "true");
        });
    }

    public void enterUniqueKeyTableConstraint(MySqlParser.UniqueKeyTableConstraintContext ctx) {
        System.out.println("enterUniqueKeyTableConstraint");

        IndexDefinition key = new IndexDefinition();

        key.setProperty("unique", "true");

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            key.addColumn((indexColumnName.uid().getText()));
        });

        StringBuilder finalKeyName = new StringBuilder("uk_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append((indexColumnName.uid().getText()));
        });
        String keyName = finalKeyName.toString();

        if (ctx.uid() != null) {
            keyName = (ctx.uid(0).getText());
        }

        currentTable.addIndex(keyName, key);

    }

    public void enterForeignKeyTableConstraint(MySqlParser.ForeignKeyTableConstraintContext ctx) {
        System.out.println("enterForeignKeyTableConstraint");

        ForeignKeyDefinition foreignKey = new ForeignKeyDefinition();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            foreignKey.addColumn((indexColumnName.uid().getText()));
        });

        foreignKey.setReferencedTable((ctx.referenceDefinition().tableName().getText()));

        ctx.referenceDefinition().indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            foreignKey.addReferencedColumn((indexColumnName.uid().getText()));
        });

        StringBuilder finalKeyName = new StringBuilder("fk_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append((indexColumnName.uid().getText()));
        });
        String keyName = finalKeyName.toString();

        List<MySqlParser.UidContext> tmp = ctx.uid();



        if (ctx.uid() != null && ctx.uid().size() > 0) {
            keyName = (ctx.uid(0).getText());
        }

        if (ctx.referenceDefinition().referenceAction() != null) {
            if (ctx.referenceDefinition().referenceAction().onDelete != null) {
                foreignKey.setProperty("onDelete", (ctx.referenceDefinition().referenceAction().onDelete.getText()));
            }
            if (ctx.referenceDefinition().referenceAction().onUpdate != null) {
                foreignKey.setProperty("onUpdate", (ctx.referenceDefinition().referenceAction().onUpdate.getText()));
            }
        }

        currentTable.addForeignKey(keyName, foreignKey);



    }

    public void enterSimpleIndexDeclaration(MySqlParser.SimpleIndexDeclarationContext ctx) {
        System.out.println("enterSimpleIndexDeclaration");

        IndexDefinition key = new IndexDefinition();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            key.addColumn((indexColumnName.uid().getText()));

        });

        StringBuilder finalKeyName = new StringBuilder("idx_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append((indexColumnName.uid().getText()));
        });
        String keyName = finalKeyName.toString();

        if (ctx.uid() != null) {
            keyName = (ctx.uid().getText());
        }
        currentTable.addIndex(keyName, key);

    }


    public void exitTableOptionComment(MySqlParser.TableOptionCommentContext ctx) {
        System.out.println("exitTableOptionComment");

        currentTable.setProperty("comment", ctx.STRING_LITERAL().getText());
    }

    public  void enterColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        System.out.println("enterColumnDeclaration");

        currentColumn = new ColumnDefinition();
        currentColumn.setProperty("name", (ctx.fullColumnName().getText()));
        currentColumn.setProperty("type", ctx.columnDefinition().dataType().getText());
        currentColumn.setDefaultProperties();
        currentTable.addColumn((ctx.fullColumnName().getText()), currentColumn);
    }

    public void exitColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        System.out.println("exitColumnDeclaration");

    }

    public void enterCommentColumnConstraint(MySqlParser.CommentColumnConstraintContext ctx) {
        System.out.println("enterCommentColumnConstraint");

        currentColumn.setProperty("comment", ctx.STRING_LITERAL().getText());
    }

    public void enterNullColumnConstraint(MySqlParser.NullColumnConstraintContext ctx) {
        System.out.println("enterNullColumnConstraint");

        currentColumn.setProperty("notNull", "true");
    }

    public void enterPrimaryKeyColumnConstraint(MySqlParser.PrimaryKeyColumnConstraintContext ctx) {
        System.out.println("enterPrimaryKeyColumnConstraint");

        if (!isAlterTable) {
            currentColumn.setProperty("primaryKey", "true");
        }
        else {
            currentColumn.setProperty("oldPrimaryKey", "false");
        }
    }

    public void enterAutoIncrementColumnConstraint(MySqlParser.AutoIncrementColumnConstraintContext ctx) {
        System.out.println("enterAutoIncrementColumnConstraint");

        currentColumn.setProperty("autoIncrement", "true");
    }

    public void enterUniqueKeyColumnConstraint(MySqlParser.UniqueKeyColumnConstraintContext ctx) {
        System.out.println("enterUniqueKeyColumnConstraint");

        IndexDefinition key = new IndexDefinition();
        key.addColumn(currentColumn.getProperty("name"));
        key.setProperty("unique", "true");
        currentTable.addIndex("uk_" + currentColumn.getProperty("name"), key);
    }

    public void enterDefaultColumnConstraint(MySqlParser.DefaultColumnConstraintContext ctx) {
        System.out.println("enterDefaultColumnConstraint");

        currentColumn.setProperty("defaultValue", ctx.defaultValue().getText());
    }

    public void enterCollateColumnConstraint(MySqlParser.CollateColumnConstraintContext ctx) {
        System.out.println("enterCollateColumnConstraint");

    }

    public void enterAlterTable(MySqlParser.AlterTableContext ctx) {
        System.out.println("enterAlterTable");

        isAlterTable = true;

        if (tables.containsKey((ctx.tableName().getText()))) {
            currentTable = tables.get((ctx.tableName().getText()));
        } else {
            throw new RuntimeException("Table " + (ctx.tableName().getText()) + " not found");
        }
        currentTable.setProperty("altered", "true");
    }

    public void exitAlterTable(MySqlParser.AlterTableContext ctx) {
        System.out.println("exitAlterTable");

        isAlterTable = false;
    }

    public void enterAlterByRename(MySqlParser.AlterByRenameContext ctx) {
        System.out.println("enterAlterByRename");

        currentTable.setProperty("oldName", (currentTable.getProperty("name")));
        currentTable.setProperty("name", (ctx.uid().getText()));
    }

    public void enterDropTable(MySqlParser.DropTableContext ctx) {
        System.out.println("enterDropTable");

        ctx.tables().tableName().forEach(tableName -> {
            if (tables.containsKey((tableName.getText()))) {
                tables.get((tableName.getText())).setProperty("dropped", "true");
            } else {
                throw new RuntimeException("Table " + (tableName.getText()) + " not found");
            }
        });
    }

    public void enterAlterByDropColumn(MySqlParser.AlterByDropColumnContext ctx)  {
        System.out.println("enterDropColumn");

        ColumnDefinition column = getChangedColumn((ctx.uid().getText()));
        column.setProperty("dropped", "true");
    }

    public void enterAlterByRenameColumn(MySqlParser.AlterByRenameColumnContext ctx) {
        System.out.println("enterAlterByRenameColumn");

        if (Objects.equals((ctx.oldColumn.getText()), (ctx.newColumn.getText())))
            return;

        ColumnDefinition column = getChangedColumn((ctx.oldColumn.getText()));
        column.setProperty("oldName", (ctx.oldColumn.getText()));
        currentTable.renameColumn((ctx.oldColumn.getText()), (ctx.newColumn.getText()));
    }

    public void enterAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        System.out.println("enterAlterByModifyColumn");

        ColumnDefinition column = getChangedColumn((ctx.uid(0).getText()));

        currentColumnProperties = new Hashtable<>();
        currentColumnProperties.putAll(column.getProperties());
        column.setDefaultProperties();
        column.setProperty("type", ctx.columnDefinition().dataType().getText());
    }

    public void exitAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        System.out.println("exitAlterByModifyColumn");

        ColumnDefinition column = getChangedColumn((ctx.uid(0).getText()));
        for (String key : currentColumnProperties.keySet()) {
            String v = currentColumnProperties.get(key);
            //change the key to oldKey, for example from "notNull" to "oldNotNull"
            String name = "old" + key.substring(0, 1).toUpperCase() + key.substring(1);
            if (currentColumn.getProperty(key) == null) {
                column.setProperty(name, v);
            }
            else if (!Objects.equals(currentColumn.getProperty(key), v)) {
                column.setProperty(name, v);
            }
        }
    }

    private ColumnDefinition getChangedColumn(String name) {
        ColumnDefinition column = currentTable.getColumn(name);
        if (column == null){
            throw new RuntimeException("Column " + name + " not found");
        }
        currentColumn = column;
        return column;
    }

    public void enterAlterByAddColumn(MySqlParser.AlterByAddColumnContext ctx) {
        System.out.println("enterAlterByAddColumn");

        ColumnDefinition column = new ColumnDefinition();
        column.setProperty("name", (ctx.uid(0).getText()));
        column.setProperty("type", ctx.columnDefinition().dataType().getText());
        column.setDefaultProperties();
        column.setProperty("added", "true");

        if (ctx.FIRST() != null)
            currentTable.addColumnToFirst((ctx.uid(0).getText()), column);
        else if (ctx.AFTER() != null)
            currentTable.addColumnAfter((ctx.uid(0).getText()), column, (ctx.uid(1).getText()));
        else
            currentTable.addColumn((ctx.uid(0).getText()), column);

        currentColumn = column;
    }

    public void enterAlterByAddForeignKey(MySqlParser.AlterByAddForeignKeyContext ctx) {
        System.out.println("enterAlterByAddForeignKey");

        ForeignKeyDefinition foreignKey = new ForeignKeyDefinition();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            foreignKey.addColumn(indexColumnName.uid().getText());
        });

        foreignKey.setReferencedTable((ctx.referenceDefinition().tableName().getText()));

        ctx.referenceDefinition().indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            foreignKey.addReferencedColumn((indexColumnName.uid().getText()));
        });

        StringBuilder finalKeyName = new StringBuilder("fk_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append((indexColumnName.uid().getText()));
        });
        String keyName = finalKeyName.toString();

        List<MySqlParser.UidContext> tmp = ctx.uid();

        if (ctx.uid() != null && ctx.uid().size() > 0) {
            keyName = (ctx.uid(0).getText());
        }

        foreignKey.setProperty("added", "true");

        if (ctx.referenceDefinition().referenceAction() != null) {
            if (ctx.referenceDefinition().referenceAction().onDelete != null) {
                foreignKey.setProperty("onDelete", (ctx.referenceDefinition().referenceAction().onDelete.getText()));
            }
            if (ctx.referenceDefinition().referenceAction().onUpdate != null) {
                foreignKey.setProperty("onUpdate", (ctx.referenceDefinition().referenceAction().onUpdate.getText()));
            }
        }

        currentTable.addForeignKey(keyName, foreignKey);
    }

    public void enterAlterByDropForeignKey(MySqlParser.AlterByDropForeignKeyContext ctx) {
        System.out.println("enterAlterByDropForeignKey");

        ForeignKeyDefinition foreignKey = currentTable.getForeignKey((ctx.uid().getText()));
        if (foreignKey == null){
            throw new RuntimeException("Foreign key " + (ctx.uid().getText()) + " not found");
        }
        foreignKey.setProperty("dropped", "true");
    }

    public void enterAlterByDropIndex(MySqlParser.AlterByDropIndexContext ctx) {
        System.out.println("enterAlterByDropIndex");

        IndexDefinition index = currentTable.getIndex((ctx.uid().getText()));
        if (index == null){
            throw new RuntimeException("Index " + (ctx.uid().getText()) + " not found");
        }
        index.setProperty("dropped", "true");
    }

    public void enterAlterByAddIndex(MySqlParser.AlterByAddIndexContext ctx) {
        System.out.println("enterAlterByAddIndex");

        IndexDefinition index = new IndexDefinition();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            index.addColumn(indexColumnName.uid().getText());
        });

        StringBuilder finalKeyName = new StringBuilder("idx_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append((indexColumnName.uid().getText()));
        });
        String keyName = finalKeyName.toString();

        if (ctx.uid() != null) {
            keyName = ctx.uid().getText();
        }

        index.setProperty("added", "true");

        currentTable.addIndex(keyName, index);
    }

    public void enterAlterByDropPrimaryKey(MySqlParser.AlterByDropPrimaryKeyContext ctx) {
        System.out.println("enterAlterByDropPrimaryKey");

        List<ColumnDefinition> columns = currentTable.getColumnSequence();

        for (ColumnDefinition column : columns) {
            if (column.getProperty("primaryKey") != null) {
                column.setProperty("primaryKey", "false");
                column.setProperty("oldPrimaryKey", "true");
            }
        }

    }

    public void enterAlterByAddPrimaryKey(MySqlParser.AlterByAddPrimaryKeyContext ctx) {
        System.out.println("enterAlterByAddPrimaryKey");

        List<ColumnDefinition> columns = currentTable.getColumnSequence();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            ColumnDefinition column = currentTable.getColumn((indexColumnName.uid().getText()));
            column.setProperty("primaryKey", "true");
            column.setProperty("oldPrimaryKey", "false");
        });
    }













}
