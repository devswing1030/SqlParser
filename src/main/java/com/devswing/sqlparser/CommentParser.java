package com.devswing.sqlparser;

public class CommentParser {

    /**
     * The table comment syntax is:
     * LocalTableName:Table Description: tag1 : tag2 : tag3
     */
    public static void parseTableComment(TableDefinition table) {
        TableComment comment = new TableComment();
        comment.parse(table.getProperty("comment"));

        if (comment.getProperty("localName") != null) {
            table.setProperty("localName", comment.getProperty("localName"));
        }
        if (comment.getProperty("description") != null) {
            table.setProperty("description", comment.getProperty("description"));
        }
        table.setTags(comment.getTags());
    }

    /**
     * The column comment syntax is:
     * LocalColumnName:Column Description: enum
     * The enum has two syntaxes:
     * - enumVal 1-AAA 2-BBB 3-CCC
     * - enumTable tablename(enum val descption column's name)
     */
    public static void parseColumnComment(ColumnDefinition column) {
        column.setProperty("parseComment", null);
        ColumnComment comment = new ColumnComment();
        if (column.getProperty("comment") != null) {
            if (!comment.parse(column.getProperty("comment"))) {
                column.setProperty("description", column.getProperty("comment"));
                return;
            }
        }
        column.setProperty("localName", comment.getLocalName());
        column.setProperty("description", comment.getDescription());
        column.setEnums(comment.getEnums());
    }

}
