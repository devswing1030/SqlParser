package com.devswing.sqlparser;

import org.junit.jupiter.api.Test;

class ColumnCommentTest {

    @Test
    void parse() {
        String commentStr = "用户类别: : enumVal 1-普通用户, 2-管理员";
        ColumnComment comment = new ColumnComment();
        comment.parse(commentStr);

        assert(comment.getLocalName().equals("用户类别"));
        assert(comment.getDescription() == null);
        assert(comment.getEnums().get("1").equals("普通用户"));
        assert(comment.getEnums().get("2").equals("管理员"));

        commentStr = "用户类别: 用户的类别 : enumTable user_type(name)";
        comment = new ColumnComment();
        comment.parse(commentStr);

        assert(comment.getLocalName().equals("用户类别"));
        assert(comment.getDescription().equals("用户的类别"));
        assert(comment.getEnumRefTable().equals("user_type"));
        assert(comment.getEnumRefColumn().equals("name"));
    }
}