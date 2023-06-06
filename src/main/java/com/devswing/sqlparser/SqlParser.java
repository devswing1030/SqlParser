package com.devswing.sqlparser;

import com.devswing.sqlparser.mysql.ConcreteParserListener;
import com.devswing.sqlparser.mysql.MySqlLexer;
import com.devswing.sqlparser.mysql.MySqlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class SqlParser {

    ConcreteParserListener listener;
    public SqlParser(String sql, TreeMap<String, TableDefinition> tables) {
        listener = getConcreteParserListener(sql, tables);
    }

    public Map<String, TableDefinition> getTables() {
        return listener.getTables();
    }

    private static ConcreteParserListener getConcreteParserListener(String sql, TreeMap<String, TableDefinition> tables) {
        MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(sql));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener(tables);

        MySqlParser.RootContext tree = parser.root();
        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return listener;
    }
}
