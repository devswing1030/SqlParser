package com.devswing.sqlparser.mysql;


import com.devswing.sqlparser.*;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ConcreteParserListener extends MySqlParserBaseListener {

    private static final Logger LOGGER = LogManager.getLogger(ConcreteParserListener.class);

    private TreeMap<String, TableDefinition> tablesDefinition;

    private TreeMap<String, TableData> tablesData;
    private boolean isAlterSchema = false;
    private boolean isParseComment = false;
    private TableDefinition currentTable;
    private ColumnDefinition currentColumn;
    private Hashtable<String, String> currentColumnProperties;

    private boolean isAlterTable = false;


    public ConcreteParserListener(TreeMap<String, TableDefinition> tablesDefinition, TreeMap<String, TableData> tablesData) {
        if (tablesDefinition == null) {
            throw new IllegalArgumentException("tablesDefinition cannot be null");
        }
        if (tablesData == null) {
            tablesData = new TreeMap<>();
        }
        this.tablesDefinition = tablesDefinition;
        this.tablesData = tablesData;
    }

    public void setAlterSchema(boolean isAlterSchema) {
        this.isAlterSchema = isAlterSchema;
    }

    public void setParseComment(boolean isParseComment) {
        this.isParseComment = isParseComment;
    }


    public void enterColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        LOGGER.debug("enterColumnCreateTable");

        LOGGER.info("Create table : " + ctx.tableName().getText());

        isAlterTable = false;
        currentTable = new TableDefinition();
        currentTable.setProperty("name", (ctx.tableName().getText()));
        if (isAlterSchema) {
            currentTable.setProperty("new", "true");
        }
        tablesDefinition.put(currentTable.getProperty("name"), currentTable);
    }
    public void exitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        LOGGER.debug("exitColumnCreateTable");

        if (isParseComment)
            CommentParser.parseTableComment(currentTable);
    }

    public void enterPrimaryKeyTableConstraint(MySqlParser.PrimaryKeyTableConstraintContext ctx) {
        LOGGER.debug("enterPrimaryKeyTableConstraint");

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            ColumnDefinition column = currentTable.getColumn((indexColumnName.uid().getText()));
            column.setProperty("primaryKey", "true");
        });
    }

    public void enterUniqueKeyTableConstraint(MySqlParser.UniqueKeyTableConstraintContext ctx) {
        LOGGER.debug("enterUniqueKeyTableConstraint");

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
        LOGGER.debug("enterForeignKeyTableConstraint");

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
        LOGGER.debug("enterSimpleIndexDeclaration");

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
        LOGGER.debug("exitTableOptionComment");

        if (isAlterTable) {
            currentTable.setProperty("oldComment", currentTable.getProperty("comment"));
        }

        String comment = ctx.STRING_LITERAL().getText();
        comment = comment.substring(1, comment.length() - 1);

        currentTable.setProperty("comment", comment);
    }

    public  void enterColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        LOGGER.debug("enterColumnDeclaration");

        currentColumn = new ColumnDefinition();
        currentColumn.setProperty("name", (ctx.fullColumnName().getText()));
        currentColumn.setProperty("type", ctx.columnDefinition().dataType().getText());
        currentColumn.setDefaultProperties();
        currentTable.addColumn((ctx.fullColumnName().getText()), currentColumn);
    }

    public void exitColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        LOGGER.debug("exitColumnDeclaration");

        if (isParseComment)
            CommentParser.parseColumnComment(currentColumn);

    }

    public void enterCommentColumnConstraint(MySqlParser.CommentColumnConstraintContext ctx) {
        LOGGER.debug("enterCommentColumnConstraint");

        String comment = ctx.STRING_LITERAL().getText();
        comment = comment.substring(1, comment.length() - 1);

        currentColumn.setProperty("comment", comment);
    }

    public void enterNullColumnConstraint(MySqlParser.NullColumnConstraintContext ctx) {
        LOGGER.debug("enterNullColumnConstraint");

        currentColumn.setProperty("notNull", "true");
    }

    public void enterPrimaryKeyColumnConstraint(MySqlParser.PrimaryKeyColumnConstraintContext ctx) {
        LOGGER.debug("enterPrimaryKeyColumnConstraint");

        if (!isAlterTable) {
            currentColumn.setProperty("primaryKey", "true");
        }
        else {
            currentColumn.setProperty("oldPrimaryKey", "false");
        }
    }

    public void enterAutoIncrementColumnConstraint(MySqlParser.AutoIncrementColumnConstraintContext ctx) {
        LOGGER.debug("enterAutoIncrementColumnConstraint");

        currentColumn.setProperty("autoIncrement", "true");
    }

    public void enterUniqueKeyColumnConstraint(MySqlParser.UniqueKeyColumnConstraintContext ctx) {
        LOGGER.debug("enterUniqueKeyColumnConstraint");

        IndexDefinition key = new IndexDefinition();
        key.addColumn(currentColumn.getProperty("name"));
        key.setProperty("unique", "true");
        currentTable.addIndex("uk_" + currentColumn.getProperty("name"), key);
    }

    public void enterDefaultColumnConstraint(MySqlParser.DefaultColumnConstraintContext ctx) {
        LOGGER.debug("enterDefaultColumnConstraint");

        currentColumn.setProperty("defaultValue", ctx.defaultValue().getText());
    }

    public void enterCollateColumnConstraint(MySqlParser.CollateColumnConstraintContext ctx) {
        LOGGER.debug("enterCollateColumnConstraint");

    }

    public void enterAlterTable(MySqlParser.AlterTableContext ctx) {
        LOGGER.debug("enterAlterTable");

        LOGGER.info("Alter table: " + ctx.tableName().getText() );

        isAlterTable = true;

        if (tablesDefinition.containsKey((ctx.tableName().getText()))) {
            currentTable = tablesDefinition.get((ctx.tableName().getText()));
        } else {
            throw new RuntimeException("Table " + (ctx.tableName().getText()) + " not found");
        }
        currentTable.setProperty("altered", "true");
    }

    public void exitAlterTable(MySqlParser.AlterTableContext ctx) {
        LOGGER.debug("exitAlterTable");

        isAlterTable = false;

        if (isParseComment)
            CommentParser.parseTableComment(currentTable);
    }

    public void enterAlterByRename(MySqlParser.AlterByRenameContext ctx) {
        LOGGER.debug("enterAlterByRename");

        currentTable.setProperty("oldName", (currentTable.getProperty("name")));
        currentTable.setProperty("name", (ctx.uid().getText()));
    }

    public void enterDropTable(MySqlParser.DropTableContext ctx) {
        LOGGER.debug("enterDropTable");


        ctx.tables().tableName().forEach(tableName -> {
            if (tablesDefinition.containsKey((tableName.getText()))) {
                tablesDefinition.get((tableName.getText())).setProperty("dropped", "true");
            } else {
                if (ctx.ifExists() == null)
                    throw new RuntimeException("Table " + (tableName.getText()) + " not found");
            }
        });
    }

    public void enterAlterByDropColumn(MySqlParser.AlterByDropColumnContext ctx)  {
        LOGGER.debug("enterDropColumn");

        ColumnDefinition column = getChangedColumn((ctx.uid().getText()));
        column.setProperty("dropped", "true");
    }

    public void enterAlterByRenameColumn(MySqlParser.AlterByRenameColumnContext ctx) {
        LOGGER.debug("enterAlterByRenameColumn");

        if (Objects.equals((ctx.oldColumn.getText()), (ctx.newColumn.getText())))
            return;

        ColumnDefinition column = getChangedColumn((ctx.oldColumn.getText()));
        column.setProperty("oldName", (ctx.oldColumn.getText()));
        currentTable.renameColumn((ctx.oldColumn.getText()), (ctx.newColumn.getText()));
    }

    public void enterAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        LOGGER.debug("enterAlterByModifyColumn");

        ColumnDefinition column = getChangedColumn((ctx.uid(0).getText()));

        currentColumnProperties = new Hashtable<>();
        currentColumnProperties.putAll(column.getProperties());
        column.setDefaultProperties();
        column.setProperty("type", ctx.columnDefinition().dataType().getText());
    }

    public void exitAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        LOGGER.debug("exitAlterByModifyColumn");

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

        if (isParseComment)
            CommentParser.parseColumnComment(column);
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
        LOGGER.debug("enterAlterByAddColumn");

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
        LOGGER.debug("enterAlterByAddForeignKey");

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
        LOGGER.debug("enterAlterByDropForeignKey");

        ForeignKeyDefinition foreignKey = currentTable.getForeignKey((ctx.uid().getText()));
        if (foreignKey == null){
            throw new RuntimeException("Foreign key " + (ctx.uid().getText()) + " not found");
        }
        foreignKey.setProperty("dropped", "true");
    }

    public void enterAlterByDropIndex(MySqlParser.AlterByDropIndexContext ctx) {
        LOGGER.debug("enterAlterByDropIndex");

        IndexDefinition index = currentTable.getIndex((ctx.uid().getText()));
        if (index == null){
            throw new RuntimeException("Index " + (ctx.uid().getText()) + " not found");
        }
        index.setProperty("dropped", "true");
    }

    public void enterAlterByAddIndex(MySqlParser.AlterByAddIndexContext ctx) {
        LOGGER.debug("enterAlterByAddIndex");

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
        LOGGER.debug("enterAlterByDropPrimaryKey");

        List<ColumnDefinition> columns = currentTable.getColumnSequenceRevision();

        for (ColumnDefinition column : columns) {
            if (column.getProperty("primaryKey") != null) {
                column.setProperty("primaryKey", "false");
                column.setProperty("oldPrimaryKey", "true");
            }
        }

    }

    public void enterAlterByAddPrimaryKey(MySqlParser.AlterByAddPrimaryKeyContext ctx) {
        LOGGER.debug("enterAlterByAddPrimaryKey");

        List<ColumnDefinition> columns = currentTable.getColumnSequenceRevision();

        ctx.indexColumnNames().indexColumnName().forEach(indexColumnName -> {
            ColumnDefinition column = currentTable.getColumn((indexColumnName.uid().getText()));
            column.setProperty("primaryKey", "true");
            column.setProperty("oldPrimaryKey", "false");
        });
    }

    public void enterInsertStatement(MySqlParser.InsertStatementContext ctx) {
        LOGGER.debug("enterInsertStatement");

        currentTable = tablesDefinition.get(ctx.tableName().getText());
        if (currentTable == null){
            throw new RuntimeException("Table " + (ctx.tableName().getText()) + " not found");
        }

        ArrayList<String> columns = new ArrayList<String>();
        if (ctx.columns != null) {
            ctx.columns.fullColumnName().forEach(fullColumnName -> {
                columns.add(fullColumnName.getText());
            });
        }
        else {
            currentTable.getColumnSequence().forEach(column -> {
                columns.add(column.getProperty("name"));
            });
        }

        List<MySqlParser.ExpressionOrDefaultContext> values = ctx.insertStatementValue().expressionsWithDefaults().get(0).expressionOrDefault();

        if (columns.size() != values.size()) {
            throw new RuntimeException("Number of columns and values don't match");
        }


        RowData rowData = new RowData();
        for (int i = 0; i < columns.size(); i++) {
            rowData.addField(columns.get(i), removeQuotes(values.get(i).getText()));
        }

        TableData tableData = tablesData.get(currentTable.getProperty("name"));
        if (tableData == null) {
            tableData = new TableData(currentTable);
            tablesData.put(currentTable.getProperty("name"), tableData);
        }
        tableData.addRow(rowData);;
    }

    private String removeQuotes(String text) {
        if (text == null) {
            return null;
        }
        if (!text.startsWith("'") && !text.startsWith("'''") && !text.startsWith("`") && !text.startsWith("\"")) {
            return text;
        }

        text = text.substring(1, text.length() - 1);
        text = text.replace("''", "'");
        return text;
    }

    public void enterStringLiteral(MySqlParser.StringLiteralContext ctx) {
        LOGGER.debug("enterStringLiteral");

        String text = ctx.getText();
        text = text.substring(1, text.length() - 1);
        text = text.replace("''", "'");

    }













}
