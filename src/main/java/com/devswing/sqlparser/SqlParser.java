package com.devswing.sqlparser;

import com.devswing.sqlparser.mysql.ConcreteParserListener;
import com.devswing.sqlparser.mysql.MySqlLexer;
import com.devswing.sqlparser.mysql.MySqlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.Hashtable;
import java.util.TreeMap;

public class SqlParser {
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

    public void parseFromString(String sql, TreeMap<String, TableDefinition> tables) {
        parse(CharStreams.fromString(sql), tables, null);
    }

    public void parseFromFile(String filePath, TreeMap<String, TableDefinition> tables) throws IOException {
        parse(CharStreams.fromFileName(filePath), tables, null);
    }

    public void parseFromString(String sql, TreeMap<String, TableDefinition> tables, TreeMap<String, TableData> tablesData) {
        parse(CharStreams.fromString(sql), tables, tablesData);
    }

    public void parseFromFile(String filePath, TreeMap<String, TableDefinition> tables, TreeMap<String, TableData> tablesData) throws IOException {
        parse(CharStreams.fromFileName(filePath), tables, tablesData);
    }


    private void parse(CharStream s, TreeMap<String, TableDefinition> tablesDefinition, TreeMap<String, TableData> tablesData) {
        MySqlLexer lexer = new MySqlLexer(s);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener(tablesDefinition, tablesData);
        listener.setParseComment(parseComment);

        MySqlParser.RootContext tree = parser.root();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

    }
}
