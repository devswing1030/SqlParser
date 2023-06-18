package com.devswing.sqlparser.comment;

import com.devswing.sqlparser.TableComment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteTableCommentListenerTest {

    @Test
    void comment() {
        String commentStr = "User : this is a table for store user : metadata : every day";

        TableComment comment = ConcreteTableCommentListener.parse(commentStr);

        assert(comment.getProperty("localName").equals("User"));
        assert(comment.getProperty("description").equals("this is a table for store user"));
        assert(comment.getTags().get(0).equals("metadata"));
        assert(comment.getTags().get(1).equals("every day"));
    }

}