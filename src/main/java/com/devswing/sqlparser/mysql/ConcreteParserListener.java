package com.devswing.sqlparser.mysql;

import com.devswing.sqlparser.*;

import java.util.*;

public class ConcreteParserListener extends MySqlParserBaseListener {

    private final Map<String, TableDefinition> tables = new TreeMap<>();
    private final Map<String, TableDefinition> alteredTables = new TreeMap<>();
    private final HashSet<String> droppedTables = new HashSet<>();

    private TableDefinition currentTable;
    private ColumnDefinition currentColumn;

    public Map<String, TableDefinition> getTables() {
        return tables;
    }

    public Map<String, TableDefinition> getAlteredTables() {
        return alteredTables;
    }

    public HashSet<String> getDroppedTables() {
        return droppedTables;
    }


    public void enterColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        System.out.println("enterColumnCreateTable");

        currentTable = new TableDefinition();
        currentTable.setProperty("name", ctx.tableName().getText());
    }
    public void exitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        System.out.println("exitColumnCreateTable");

        tables.put(currentTable.getProperty("name"), currentTable);
    }

    public void enterPrimaryKeyTableConstraint(MySqlParser.PrimaryKeyTableConstraintContext ctx) {
        System.out.println("enterPrimaryKeyTableConstraint");

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            ColumnDefinition column = currentTable.getColumn(indexColumnName.uid().getText());
            column.setProperty("primaryKey", "true");
        });
    }

    public void enterUniqueKeyTableConstraint(MySqlParser.UniqueKeyTableConstraintContext ctx) {
        System.out.println("enterUniqueKeyTableConstraint");

        KeyDefinition key = new KeyDefinition();
        key.setUnique(true);

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            key.addColumn(indexColumnName.uid().getText());
        });

        StringBuilder finalKeyName = new StringBuilder("uk_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append(indexColumnName.uid().getText());
        });
        String keyName = finalKeyName.toString();

        if (ctx.uid() != null) {
            keyName = ctx.uid(0).getText();
        }

        currentTable.addKey(keyName, key);

    }

    public void enterForeignKeyTableConstraint(MySqlParser.ForeignKeyTableConstraintContext ctx) {
        System.out.println("enterForeignKeyTableConstraint");

        ForeignKeyDefinition foreignKey = new ForeignKeyDefinition();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            foreignKey.addColumn(indexColumnName.uid().getText());
        });

        foreignKey.setReferencedTable(ctx.referenceDefinition().tableName().getText());

        ctx.referenceDefinition().indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            foreignKey.addReferencedColumn(indexColumnName.uid().getText());
        });

        StringBuilder finalKeyName = new StringBuilder("fk_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append(indexColumnName.uid().getText());
        });
        String keyName = finalKeyName.toString();

        List<MySqlParser.UidContext> tmp = ctx.uid();



        if (ctx.uid() != null && ctx.uid().size() > 0) {
            keyName = ctx.uid(0).getText();
        }

        currentTable.addForeignKey(keyName, foreignKey);



    }

    public void enterSimpleIndexDeclaration(MySqlParser.SimpleIndexDeclarationContext ctx) {
        System.out.println("enterSimpleIndexDeclaration");

        KeyDefinition key = new KeyDefinition();
        key.setUnique(false);

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            key.addColumn(indexColumnName.uid().getText());

        });

        StringBuilder finalKeyName = new StringBuilder("idx_");
        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            finalKeyName.append(indexColumnName.uid().getText());
        });
        String keyName = finalKeyName.toString();

        if (ctx.uid() != null) {
            keyName = ctx.uid().getText();
        }
        currentTable.addKey(keyName, key);

    }


    public void exitTableOptionComment(MySqlParser.TableOptionCommentContext ctx) {
        System.out.println("exitTableOptionComment");

        currentTable.setProperty("comment", ctx.STRING_LITERAL().getText());
    }

    public  void enterColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        System.out.println("enterColumnDeclaration");

        currentColumn = new ColumnDefinition();
        currentColumn.setProperty("name", ctx.fullColumnName().getText());
        currentColumn.setProperty("type", ctx.columnDefinition().dataType().getText());
        currentColumn.setDefaultProperties();
        currentTable.addColumn(ctx.fullColumnName().getText(), currentColumn);
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

        currentColumn.setProperty("primaryKey", "true");
    }

    public void enterAutoIncrementColumnConstraint(MySqlParser.AutoIncrementColumnConstraintContext ctx) {
        System.out.println("enterAutoIncrementColumnConstraint");

        currentColumn.setProperty("autoIncrement", "true");
    }

    public void enterUniqueKeyColumnConstraint(MySqlParser.UniqueKeyColumnConstraintContext ctx) {
        System.out.println("enterUniqueKeyColumnConstraint");

        currentColumn.setProperty("unique", "true");
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

        if (alteredTables.containsKey(ctx.tableName().getText())) {
            currentTable = alteredTables.get(ctx.tableName().getText());
        } else {
            currentTable = new TableDefinition();
            alteredTables.put(ctx.tableName().getText(), currentTable);
        }
    }

    public void enterAlterByRename(MySqlParser.AlterByRenameContext ctx) {
        System.out.println("enterAlterByRename");

        currentTable.setProperty("name", ctx.uid().getText());
    }

    public void enterDropTable(MySqlParser.DropTableContext ctx) {
        System.out.println("enterDropTable");

        ctx.tables().tableName().forEach(tableName -> droppedTables.add(tableName.getText()));
    }

    public void enterAlterByDropColumn(MySqlParser.AlterByDropColumnContext ctx)  {
        System.out.println("enterDropColumn");

        ColumnDefinition column = new ColumnDefinition();
        column.setProperty("dropped", "true");
        currentTable.addColumn(ctx.uid().getText(),column);
    }

    public void enterAlterByRenameColumn(MySqlParser.AlterByRenameColumnContext ctx) {
        System.out.println("enterAlterByRenameColumn");

        if (Objects.equals(ctx.oldColumn.getText(), ctx.newColumn.getText()))
            return;

        ColumnDefinition column = getChangedColumn(ctx.oldColumn.getText());
        column.setProperty("name", ctx.newColumn.getText());
    }

    public void enterAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        System.out.println("enterAlterByModifyColumn");

        ColumnDefinition column = getChangedColumn(ctx.uid(0).getText());
        column.setDefaultProperties();
        column.setProperty("type", ctx.columnDefinition().dataType().getText());
    }

    private ColumnDefinition getChangedColumn(String name) {
        ColumnDefinition column = currentTable.getColumn(name);
        if (column == null){
            column = new ColumnDefinition();
            currentTable.addColumn(name, column);
        }
        currentColumn = column;
        return column;
    }













}
