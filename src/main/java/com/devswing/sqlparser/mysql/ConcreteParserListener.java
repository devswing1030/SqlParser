package com.devswing.sqlparser.mysql;


import com.devswing.sqlparser.*;

import java.util.*;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ConcreteParserListener extends MySqlParserBaseListener {

    private static final Logger LOGGER = LogManager.getLogger(ConcreteParserListener.class);

    private Database db;

    private TreeMap<String, TableDefinition> tablesDefinition;

    private TreeMap<String, TableData> tablesData;
    private boolean isParseComment = false;
    private TableDefinition currentTable;
    private ColumnDefinition currentColumn;
    private int rowsLoaded = 0;

    private StringBuilder errorMessages = new StringBuilder();

    private boolean hasError = false;
    private int errLine = -1;
    private String parsedFile = null;


    public ConcreteParserListener(Database db) {
        this.db = db;
        this.tablesDefinition = db.getTablesDefinition();
        this.tablesData = db.getTablesData();
    }

    public void setParseComment(boolean isParseComment) {
        this.isParseComment = isParseComment;
    }

    public void setParsedFile(String parsedFile) {
        this.parsedFile = parsedFile;
    }

    public boolean hasError() {
        return hasError;
    }

    public String errorMessage() {
        return this.errorMessages.toString();
    }


    public void enterColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        LOGGER.debug("enterColumnCreateTable");

        LOGGER.debug("Parse table schema: " + ctx.tableName().getText());

        currentTable = new TableDefinition();
        currentTable.setProperty("name", (ctx.tableName().getText()));
        CharStream in = ctx.getStart().getInputStream();
        String createSql = in.getText(Interval.of(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex()+1));
        currentTable.setCreateSql(createSql);
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

        LOGGER.debug("Alter table: " + ctx.tableName().getText() );

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

        CharStream in = ctx.getStart().getInputStream();
        String insertSql = in.getText(Interval.of(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex()+1));
        int line = ctx.getStart().getLine();

        currentTable = tablesDefinition.get(ctx.tableName().getText());
        if (currentTable == null){
            throw new RuntimeException("Table " + (ctx.tableName().getText()) + " not found");
        }

        ArrayList<String> columns = new ArrayList<String>();
        if (ctx.columns != null) {
            if (ctx.columns.fullColumnName().size() != currentTable.getColumns().size()) {
                hasError = true;
                if (this.parsedFile !=null) {
                    LOGGER.error("Number of columns dose not equal to table definition in file " + parsedFile + " in line " + line + ": " + insertSql);

                }
                else {
                    LOGGER.error("Number of columns dose not equal to table definition in line " + line + ": " + insertSql);
                }
                return;
            }
            for (int i = 0; i < ctx.columns.fullColumnName().size(); i++) {
                if (currentTable.getColumn(ctx.columns.fullColumnName(i).getText()) == null) {
                    hasError = true;
                    if (this.parsedFile != null)
                    {
                        LOGGER.error("Column " + ctx.columns.fullColumnName(i).getText() + " not found in table " + currentTable.getProperty("name") + " in file " + parsedFile + " in line " + line + ": " + insertSql);
                    }
                    else {
                        LOGGER.error("Column " + ctx.columns.fullColumnName(i).getText() + " not found in table " + currentTable.getProperty("name") + " in line " + line + ": " + insertSql);
                    }
                    return;
                }
                columns.add(ctx.columns.fullColumnName(i).getText());
            }
        }
        else {
            currentTable.getColumnSequence().forEach(column -> {
                columns.add(column.getProperty("name"));
            });
        }

        TableData tableData = tablesData.get(currentTable.getProperty("name"));
        if (tableData == null) {
            tableData = new TableData(currentTable);
            tablesData.put(currentTable.getProperty("name"), tableData);
        }

        List<MySqlParser.ExpressionsWithDefaultsContext> rowValues = ctx.insertStatementValue().expressionsWithDefaults();
        for (MySqlParser.ExpressionsWithDefaultsContext rowValue : rowValues) {
            List<MySqlParser.ExpressionOrDefaultContext> values = rowValue.expressionOrDefault();
            if (columns.size() != values.size()) {
                hasError = true;
                if (this.parsedFile != null) {
                    LOGGER.error("Number of columns and values don't match in file " + parsedFile + " in line " + line + ": " + insertSql);
                }
                else {
                    LOGGER.error("Number of columns and values don't match in line " + line + ": " + insertSql);
                }
                return;
            }

            RowData rowData = new RowData();
            for (int i = 0; i < columns.size(); i++) {
                rowData.addField(columns.get(i), removeQuotes(values.get(i).getText()));
            }

            rowData.setInsertSql(insertSql);

            tableData.addRow(rowData);

            this.rowsLoaded++;

            if (this.rowsLoaded % 1000 == 0) {
                LOGGER.debug("Loaded " + this.rowsLoaded + " rows, current table: " + currentTable.getProperty("name"));
            }
        }
    }

    public void enterSingleUpdateStatement(MySqlParser.SingleUpdateStatementContext ctx) {
        LOGGER.debug("enterUpdateStatement");

    }

    public void enterSingleDeleteStatement(MySqlParser.SingleDeleteStatementContext ctx) {
        LOGGER.debug("enterDeleteStatement");

    }


    public void enterCreateProcedure(MySqlParser.CreateProcedureContext ctx) {
        LOGGER.debug("enterCreateProcedure");
        CharStream in = ctx.getStart().getInputStream();
        String procedureSql = in.getText(Interval.of(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex()+1));
        String name = ctx.fullId().getText();
        this.db.setProcedure(name, procedureSql);
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

    public void visitTerminal(TerminalNode node) {
        LOGGER.debug("Terminal node: " + node.getText());
    }
    public void visitErrorNode(ErrorNode node) {
        hasError = true;

        int currentLine = node.getSymbol().getLine();
        if (errLine == -1) { // 第一行错误
            errLine = currentLine;
            errorMessages.append("line:").append(node.getSymbol().getLine()).append(":");
        }
        // 解析器会将第一行错误开始之后的所有文本都当作错误，因此第一行错误处理完后，不再处理后续行
        if (currentLine == errLine) {
            errorMessages.append(node.getText()).append(" ");
        }
    }

    public void enterShowErrors(MySqlParser.ShowErrorsContext ctx) {
    }

    public void enterShowCountErrors(MySqlParser.ShowCountErrorsContext ctx) {
    }

}
