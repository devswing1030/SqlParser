package com.devswing.sqlparser.mysql;

import java.util.ArrayList;
import java.util.List;

public class ConcreteParserListener extends MySqlParserBaseListener {
    private final List<MySqlTableDefinition> tables = new ArrayList<>();
    private MySqlTableDefinition currentTable;
    private MySqlColumnDefinition currentColumn;

    public List<MySqlTableDefinition> getTables() {
        return tables;
    }


    public void enterColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        System.out.println("enterColumnCreateTable");

        currentTable = new MySqlTableDefinition();
        currentTable.setName(ctx.tableName().getText());
    }
    public void exitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        System.out.println("exitColumnCreateTable");

        tables.add(currentTable);
    }

    public void exitTableOptionComment(MySqlParser.TableOptionCommentContext ctx) {
        System.out.println("exitTableOptionComment");

        currentTable.setComment(ctx.STRING_LITERAL().getText());
    }

    public  void enterColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        System.out.println("enterColumnDeclaration");

        currentColumn = new MySqlColumnDefinition();
        currentColumn.setName(ctx.fullColumnName().getText());
        currentColumn.setType(ctx.columnDefinition().dataType().getText());
    }

    public void exitColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        System.out.println("exitColumnDeclaration");

        currentTable.addColumn(currentColumn);
    }

    public void enterCommentColumnConstraint(MySqlParser.CommentColumnConstraintContext ctx) {
        System.out.println("enterCommentColumnConstraint");

        currentColumn.setComment(ctx.STRING_LITERAL().getText());
    }

    public void enterNullColumnConstraint(MySqlParser.NullColumnConstraintContext ctx) {
        System.out.println("enterNullColumnConstraint");

        currentColumn.setNotNull(true);
    }

    public void enterPrimaryKeyColumnConstraint(MySqlParser.PrimaryKeyColumnConstraintContext ctx) {
        System.out.println("enterPrimaryKeyColumnConstraint");

        currentColumn.setPrimaryKey(true);
    }

    public void enterAutoIncrementColumnConstraint(MySqlParser.AutoIncrementColumnConstraintContext ctx) {
        System.out.println("enterAutoIncrementColumnConstraint");

        currentColumn.setAutoIncrement(true);
    }

    public void enterUniqueKeyColumnConstraint(MySqlParser.UniqueKeyColumnConstraintContext ctx) {
        System.out.println("enterUniqueKeyColumnConstraint");

        currentColumn.setUnique(true);
    }

    public void enterDefaultColumnConstraint(MySqlParser.DefaultColumnConstraintContext ctx) {
        System.out.println("enterDefaultColumnConstraint");

        currentColumn.setDefaultValue(ctx.defaultValue().getText());
    }

    public void enterCollateColumnConstraint(MySqlParser.CollateColumnConstraintContext ctx) {
        System.out.println("enterCollateColumnConstraint");

        currentColumn.setCollate(ctx.collationName().getText());
    }


}
