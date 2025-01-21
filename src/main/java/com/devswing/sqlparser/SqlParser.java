package com.devswing.sqlparser;

import com.devswing.sqlparser.mysql.ConcreteParserListener;
import com.devswing.sqlparser.mysql.MySqlLexer;
import com.devswing.sqlparser.mysql.MySqlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Hashtable;
import java.util.TreeMap;

public class SqlParser {
    private static final Logger LOGGER = LogManager.getLogger(SqlParser.class);

    private boolean isAlter = false;
    private boolean parseComment = false;

    public SqlParser() {
    }

    public void setAlter(boolean alter) {
        isAlter = alter;
    }

    public boolean isAlter() {
        return isAlter;
    }

    public void setParseComment(boolean parseComment) {
        this.parseComment = parseComment;
    }

    public boolean isParseComment() {
        return parseComment;
    }

    public boolean parseFromString(String sql, Database db) {
        return parse(CharStreams.fromString(sql), db, null);
    }

    public boolean parseFromFile(String filePath, Database db) throws IOException {
        return parse(CharStreams.fromFileName(filePath), db, filePath);
    }

    private boolean parse(CharStream s, Database db, String fileName) {
        MySqlLexer lexer = new MySqlLexer(s);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener(db);
        listener.setParseComment(parseComment);
        listener.setParsedFile(fileName);

        MySqlParser.RootContext tree = parser.root();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        if (listener.hasError()) {
            if (listener.errorMessage().length() > 0) {
                if (fileName != null) {
                    LOGGER.error("Parse error in file " + fileName + ": " + listener.errorMessage());
                }
                else {
                    LOGGER.error("Parse error: " + listener.errorMessage());
                }
            }
        }
        return !listener.hasError();
    }
}
