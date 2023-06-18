package com.devswing.sqlparser.comment;

import com.devswing.sqlparser.TableComment;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class ConcreteTableCommentListener extends TableCommentBaseListener {

    TableComment comment = new TableComment();

    public TableComment getComment() {
        return comment;
    }

    public static TableComment parse(String commentStr) {
        TableCommentLexer lexer = new TableCommentLexer(CharStreams.fromString(commentStr));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TableCommentParser parser = new TableCommentParser(tokens);

        ConcreteTableCommentListener listener = new ConcreteTableCommentListener();

        TableCommentParser.CommentContext tree = parser.comment();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        return listener.getComment();
    }

    public void enterLocalName(TableCommentParser.LocalNameContext ctx) {
        comment.setProperty("localName", ctx.getText().trim());
    }

    public void enterDescription(TableCommentParser.DescriptionContext ctx) {
        comment.setProperty("description", ctx.getText().trim());
    }

    public void enterTag(TableCommentParser.TagContext ctx) {
        String tag = ctx.getText().trim();
        //if end with '\r' , '\t', '\n' then remove it
        if (tag.endsWith("\r") || tag.endsWith("\t") || tag.endsWith("\n")) {
            tag = tag.substring(0, tag.length() - 1);
        }

        comment.addTag(tag);
    }
}
