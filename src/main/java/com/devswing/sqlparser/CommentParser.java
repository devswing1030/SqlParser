package com.devswing.sqlparser;

import com.devswing.sqlparser.comment.ConcreteTableCommentListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

public class CommentParser {

    /**
     * The table comment syntax is:
     * LocalTableName:Table Description: tag1 : tag2 : tag3
     */
    public static void parseTableComment(TableDefinition table) {
        TableComment comment = ConcreteTableCommentListener.parse(table.getProperty("comment"));

        if (comment.getProperty("localName") != null) {
            table.setProperty("localName", comment.getProperty("localName"));
        }
        if (comment.getProperty("description") != null) {
            table.setProperty("description", comment.getProperty("description"));
        }
        table.setTags(comment.getTags());

        if (table.getProperty("oldComment") == null) {
            Hashtable<String, String> tagStatus = new Hashtable<>();
            for (String tag : comment.getTags()) {
                tagStatus.put(tag, "");
            }
            table.setTagStatus(tagStatus);
            return;
        }

        TableComment oldComment = ConcreteTableCommentListener.parse(table.getProperty("oldComment"));
        if (oldComment.getProperty("localName") != null) {
            table.setProperty("oldLocalName", oldComment.getProperty("localName"));
        }
        if (oldComment.getProperty("description") != null) {
            table.setProperty("oldDescription", oldComment.getProperty("description"));
        }

        Hashtable<String, String> tagStatus = new Hashtable<>();
        ArrayList<String> oldTags = oldComment.getTags();
        for (String tag : oldTags) {
            tagStatus.put(tag, "dropped");
        }

        for (String tag : comment.getTags()) {
            if (tagStatus.get(tag) == null) {
                tagStatus.put(tag, "added");
            }
            else {
                tagStatus.put(tag, "");
            }
        }

        table.setTagStatus(tagStatus);



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

        if (column.getProperty("oldComment") == null) {
            column.setProperty("parseComment", "true");
            TreeMap<String, String> enumStatus = new TreeMap<>();
            for (String enumName : comment.getEnums().keySet()) {
                enumStatus.put(enumName, "unchanged");
            }
            column.setEnumStatus(enumStatus);
            return;
        }

        ColumnComment oldComment = new ColumnComment();
        if (!oldComment.parse(column.getProperty("oldComment")))
            return;

        if (oldComment.getLocalName() != null) {
            if (!oldComment.getLocalName().equals(comment.getLocalName())) {
                column.setProperty("oldLocalName", oldComment.getLocalName());
            }
        }
        if (oldComment.getDescription() != null) {
            if (!oldComment.getDescription().equals(comment.getDescription())) {
                column.setProperty("oldDescription", oldComment.getDescription());
            }
        }

        column.setOldEnums(oldComment.getEnums());

        TreeMap<String, String> enumStatus = new TreeMap<>();
        for (String enumName : oldComment.getEnums().keySet()) {
            enumStatus.put(enumName, "dropped");
        }
        for (String enumName : comment.getEnums().keySet()) {
            if (enumStatus.get(enumName) == null) {
                enumStatus.put(enumName, "added");
            }
            else {
                if (!comment.getEnum(enumName).equals(oldComment.getEnum(enumName))) {
                    enumStatus.put(enumName, "changed");
                }
                else {
                    enumStatus.put(enumName, "unchanged");
                }
            }
        }
        column.setEnumStatus(enumStatus);

        column.setProperty("parseComment", "true");

    }

}
