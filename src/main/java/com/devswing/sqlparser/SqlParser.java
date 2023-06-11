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
    public static void parseFromString(String sql, TreeMap<String, TableDefinition> tables) {
        parse(CharStreams.fromString(sql), tables);
    }

    public static void parseFromFile(String filePath, TreeMap<String, TableDefinition> tables) throws IOException {
        parse(CharStreams.fromFileName(filePath), tables);
    }


    private static void parse(CharStream s, TreeMap<String, TableDefinition> tables) {
        MySqlLexer lexer = new MySqlLexer(s);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener(tables);

        MySqlParser.RootContext tree = parser.root();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
    }
}
