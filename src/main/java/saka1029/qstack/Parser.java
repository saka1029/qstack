package saka1029.qstack;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Parser {
    
    public static final Symbol LP = Symbol.of("(");
    public static final Symbol RP = Symbol.of(")");
    public static final Symbol DOT = Symbol.of(".");
    public static final Symbol QUOTE = Symbol.of("'");

    final java.io.Reader reader;
    int ch;
    Element token;
    
    Parser(java.io.Reader reader) {
        this.reader = reader;
        getCh();
        get();
    }
    
    public static Parser of(String s) {
        return new Parser(new StringReader(s));
    }
    
    int getCh() {
        try {
            return ch = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    void spaces() {
        while (Character.isWhitespace(ch))
            getCh();
    }
    
    static final Pattern INT_PAT = Pattern.compile("[+-]?\\d+");

    static boolean isWord(int ch) {
        return switch (ch) {
            case '(', ')', '\'', -1 -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    Element symbolOrInt() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append((char)ch);
            getCh();
        } while (isWord(ch));
        String s = sb.toString();
        return INT_PAT.matcher(s).matches() ? Int.of(Integer.parseInt(s)) : Symbol.of(s);
    }
    
    Element get(Element e) {
        getCh();
        return e;
    }

    Element get() {
        spaces();
        return token = switch (ch) {
            case -1 -> null;
            case '(' -> get(LP);
            case ')' -> get(RP);
            case '\'' -> get(QUOTE);
            default -> symbolOrInt();
        };
    }
    
    RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }

    Element list() {
        java.util.List<Element> list = new ArrayList<>();
        get(); // skip '('
        Element tail = List.NIL;
        while (token != null && token != RP)
            list.add(read());
        if (token != RP)
            throw error("')' expected");
        get(); // skip ')'
        return List.of(list, tail);
    }
    
    public Element read() {
        if (token == null) {
            return null;
        } else if (token == LP) {
            return list();
        } else if (token == QUOTE) {
            get(); // sip '\''
            return Quote.of(read());
        } else if (token == RP || token == DOT) {
            throw error("unexpected '%s'", token);
        } else if (token instanceof Int || token instanceof Symbol) {
            Element e = token;
            get();
            return e;
        } else {
            throw error("unknown token '%s'", token);
        }
    }

}
