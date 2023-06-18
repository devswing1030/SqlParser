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

    public void loadSchema(Database db, ArrayList<String> fileList) {
        for (String path : fileList) {
            loadFromFile(path, db, false);
        }
    }

    public void loadAlterSchema(Database db, ArrayList<String> fileList) {
        for (String path : fileList) {
            loadFromFile(path, db, true);
        }
    }

    public void loadData(Database db, ArrayList<String> fileList) {
        for (String path : fileList) {
            loadFromFile(path, db, false);
        }
    }


    private void loadFromFile(String path, Database db, boolean isAlter) {
        LOGGER.info("Loading file: " + path);

        SqlParser parser = new SqlParser();
        parser.setAlter(isAlter);
        parser.setParseComment(isParseComment);
        try {
            parser.parseFromFile(path, db.getTablesDefinition(), db.getTablesData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
