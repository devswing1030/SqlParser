package com.devswing.sqlparser.mysql;


import com.devswing.sqlparser.*;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ConcreteParserListener extends MySqlParserBaseListener {

    private static final Logger LOGGER = LogManager.getLogger(ConcreteParserListener.class);

    private TreeMap<String, TableDefinition> tablesDefinition;

    private TreeMap<String, TableData> tablesData;
    private boolean isParseComment = false;
    private TableDefinition currentTable;
    private ColumnDefinition currentColumn;
    private int rowsLoaded = 0;


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

    public void setParseComment(boolean isParseComment) {
        this.isParseComment = isParseComment;
    }


    public void enterColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        LOGGER.debug("enterColumnCreateTable");

        LOGGER.info("Create table : " + ctx.tableName().getText());

        currentTable = new TableDefinition();
        currentTable.setProperty("name", (ctx.tableName().getText()));
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

        currentColumn.setProperty("primaryKey", "true");
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

    }

    public void exitAlterTable(MySqlParser.AlterTableContext ctx) {
        LOGGER.debug("exitAlterTable");

    }

    public void enterAlterByRename(MySqlParser.AlterByRenameContext ctx) {
        LOGGER.debug("enterAlterByRename");

    }

    public void enterDropTable(MySqlParser.DropTableContext ctx) {
        LOGGER.debug("enterDropTable");
    }

    public void enterAlterByDropColumn(MySqlParser.AlterByDropColumnContext ctx)  {
        LOGGER.debug("enterDropColumn");

    }

    public void enterAlterByRenameColumn(MySqlParser.AlterByRenameColumnContext ctx) {
        LOGGER.debug("enterAlterByRenameColumn");

    }

    public void enterAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        LOGGER.debug("enterAlterByModifyColumn");

    }

    public void exitAlterByModifyColumn(MySqlParser.AlterByModifyColumnContext ctx) {
        LOGGER.debug("exitAlterByModifyColumn");
    }


    public void enterAlterByAddColumn(MySqlParser.AlterByAddColumnContext ctx) {
        LOGGER.debug("enterAlterByAddColumn");

    }

    public void exitAlterByAddColumn(MySqlParser.AlterByAddColumnContext ctx) {
        LOGGER.debug("exitAlterByAddColumn");

    }

    public void enterAlterByAddForeignKey(MySqlParser.AlterByAddForeignKeyContext ctx) {
        LOGGER.debug("enterAlterByAddForeignKey");

    }

    public void enterAlterByDropForeignKey(MySqlParser.AlterByDropForeignKeyContext ctx) {
        LOGGER.debug("enterAlterByDropForeignKey");

    }

    public void enterAlterByDropIndex(MySqlParser.AlterByDropIndexContext ctx) {
        LOGGER.debug("enterAlterByDropIndex");

    }

    public void enterAlterByAddIndex(MySqlParser.AlterByAddIndexContext ctx) {
        LOGGER.debug("enterAlterByAddIndex");

    }

    public void enterAlterByDropPrimaryKey(MySqlParser.AlterByDropPrimaryKeyContext ctx) {
        LOGGER.debug("enterAlterByDropPrimaryKey");

    }

    public void enterAlterByAddPrimaryKey(MySqlParser.AlterByAddPrimaryKeyContext ctx) {
        LOGGER.debug("enterAlterByAddPrimaryKey");

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

        this.rowsLoaded++;

        if (this.rowsLoaded % 1000 == 0) {
            LOGGER.info("Loaded " + this.rowsLoaded + " rows");
        }
    }

    public void enterSingleUpdateStatement(MySqlParser.SingleUpdateStatementContext ctx) {
        LOGGER.debug("enterUpdateStatement");

    }

    public void enterSingleDeleteStatement(MySqlParser.SingleDeleteStatementContext ctx) {
        LOGGER.debug("enterDeleteStatement");

    }

    private String removeQuotes(String text) {
        if (text == null) {
            return null;
        }
        while(true) {
            if (!text.startsWith("'") && !text.startsWith("'''") && !text.startsWith("`") && !text.startsWith("\"")) {
                text = text.replace("''", "'");
                return text;
            }
            text = text.substring(1, text.length() - 1);
        }
    }

}
