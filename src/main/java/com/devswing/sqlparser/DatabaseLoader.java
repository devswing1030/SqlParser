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

    public boolean loadDatabase(Database db, ArrayList<String> fileList) {
        boolean success = true;
        for (String path : fileList) {
            if (!loadFromFile(path, db, false)) {
                success = false;
            }
        }
        return success;
    }

    public boolean loadDatabase(Database db, String sql) {
        return loadFromString(sql, db, false);
    }

    public boolean loadAlterSchema(Database db, ArrayList<String> fileList) {
        boolean success = true;
        Database tmpDb = new Database(db.getName());
        tmpDb.setTablesDefinition(db.getTablesDefinition());
        for (String path : fileList) {
            if (!loadFromFile(path, tmpDb, true)) {
                success = false;
            }
        }
        return success;
    }

    private boolean loadFromFile(String path, Database db, boolean isAlter) {
        LOGGER.debug("Begin loading file: " + path);

        boolean success = true;

        try {
            String sql = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)));
            success = loadFromString(sql, db, isAlter);
            LOGGER.debug("End loading file: " + path);
        } catch (Exception e) {
            success = false;
            LOGGER.error("Loading " + path + " failed:" + e.getMessage());
        }

        return success;
    }

    private boolean loadFromString(String sql, Database db, boolean isAlter) {

        boolean success = true;

        SqlParser parser = new SqlParser();
        parser.setAlter(isAlter);
        parser.setParseComment(isParseComment);
        try {
            success = parser.parseFromString(sql, db);
        } catch (Exception e) {
            success = false;
        }

        return success;
    }
}
