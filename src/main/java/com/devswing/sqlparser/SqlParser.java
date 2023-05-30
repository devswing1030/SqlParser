package com.devswing.sqlparser;

import com.devswing.sqlparser.mysql.ConcreteParserListener;
import com.devswing.sqlparser.mysql.MySqlLexer;
import com.devswing.sqlparser.mysql.MySqlParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashSet;
import java.util.Map;

public class SqlParser {

    ConcreteParserListener listener;
    public SqlParser(String sql) {
        listener = getConcreteParserListener(sql);
    }

    // Get dropped tables
    public HashSet<String> getDroppedTables() {
        return listener.getDroppedTables();
    }

    // Get created tables
    public Map<String, TableDefinition> getTables() {
        return listener.getTables();
    }

    public Map<String, TableDefinition> getChangedTables() {
        return listener.getAlteredTables();
    }

    private static ConcreteParserListener getConcreteParserListener(String sql) {
        MySqlLexer lexer = new MySqlLexer(CharStreams.fromString(sql));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(tokens);

        ConcreteParserListener listener = new ConcreteParserListener();

        MySqlParser.RootContext tree = parser.root();
        System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return listener;
    }
}
