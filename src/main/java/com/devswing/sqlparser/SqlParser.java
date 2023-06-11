package com.devswing.sqlparser;

import com.devswing.sqlparser.mysql.ConcreteParserListener;
import com.devswing.sqlparser.mysql.MySqlLexer;
import com.devswing.sqlparser.mysql.MySqlParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.TreeMap;

public class SqlParser {
    public static void parseFromString(String sql, TreeMap<String, TableDefinition> tables ) {
        parseFromString(sql, tables, false);
    }

    public static void parseFromString(String sql, TreeMap<String, TableDefinition> tables, boolean isAlter) {
        parse(CharStreams.fromString(sql), tables, isAlter);
    }

    public static void parseFromFile(String filePath, TreeMap<String, TableDefinition> tables) throws IOException {
        parseFromFile(filePath, tables, false);
    }

    public static void parseFromFile(String filePath, TreeMap<String, TableDefinition> tables, boolean isAlter) throws IOException {
        parse(CharStreams.fromFileName(filePath), tables, isAlter);
    }


    private static void parse(CharStream s, TreeMap<String, TableDefinition> tables, boolean isAlter) {
        MySqlLexer lexer = new MySqlLexer(s);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener(tables, isAlter);

        MySqlParser.RootContext tree = parser.root();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
    }
}
