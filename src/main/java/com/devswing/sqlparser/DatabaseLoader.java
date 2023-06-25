package com.devswing.sqlparser;

import com.devswing.sqlparser.mysql.ConcreteParserListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class DatabaseLoader {

    //define logger
    private static final Logger LOGGER = LogManager.getLogger(DatabaseLoader.class);

    private boolean isParseComment = false;

    public void setParseComment(boolean parseComment) {
        this.isParseComment = parseComment;
    }

    public boolean isParseComment() {
        return isParseComment;
    }

    public void loadDatabase(Database db, ArrayList<String> fileList) {
        for (String path : fileList) {
            loadFromFile(path, db, false);
        }
    }

    public void loadAlterSchema(Database db, ArrayList<String> fileList) {
        Database tmpDb = new Database(db.getName());
        tmpDb.setTablesDefinition(db.getTablesDefinition());
        for (String path : fileList) {
            loadFromFile(path, tmpDb, true);
        }
    }

    private void loadFromFile(String path, Database db, boolean isAlter) {
        LOGGER.info("Begin loading file: " + path);

        SqlParser parser = new SqlParser();
        parser.setAlter(isAlter);
        parser.setParseComment(isParseComment);
        try {
            parser.parseFromFile(path, db.getTablesDefinition(), db.getTablesData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("End loading file: " + path);
    }
}
