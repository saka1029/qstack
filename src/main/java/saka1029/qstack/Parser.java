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
    public static final Symbol COLON = Symbol.of(":");

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

    Element frame(Bind bind) {
        get(); // skip frame
        if (!(token instanceof Int i))
            throw error("int expected after frame");
        int returns = i.value;
        get(); // skip int
        if (token != LP)
            throw error("'(' expected");
        get(); // skip '('
        java.util.List<Symbol> args = new ArrayList<>();
        while (token != null && token instanceof Symbol s && s != LP && s != RP && s != COLON && s != DOT) {
            args.add(s);
            get(); // skip argument
        }
        Bind newBind = Bind.of(bind, args);
        Element tail = List.NIL;
        java.util.List<Element> elements = new ArrayList<>();
        int offset = 2;
        if (token == COLON) {
            get(); // skip ':'
            while (token != null && token != RP && token != DOT) {
                elements.add(read(newBind));
                if (token != null && token instanceof Symbol s && s != LP && s != RP && s != COLON && s != DOT) {
                    newBind.add(s, offset++);
                    get(); // skip variable
                } else
                    throw error("local variable expected");
            }
        }
        if (token != RP)
            throw error("')' expected");
        get(); // skip ')'
        while (token != null && token != RP && token != DOT)
            elements.add(read(newBind));
        if (token != RP)
            throw error("')' expected");
        get(); // skip ')'
        return Block.of(elements, tail, args.size(), returns);
    }

    Element list(Bind bind) {
        java.util.List<Element> list = new ArrayList<>();
        get(); // skip '('
        if (token == Symbol.of("frame"))
            return frame(bind);
        Element tail = List.NIL;
        while (token != null && token != RP && token != DOT)
            list.add(read(bind));
        if (token == DOT) {
            get(); // skip '.'
            tail = read(bind);
        }
        if (token != RP)
            throw error("')' expected");
        get(); // skip ')'
        return List.of(list, tail);
    }
    
    public Element read(Bind bind) {
        if (token == null) {
            return null;
        } else if (token == LP) {
            return list(bind);
        } else if (token == QUOTE) {
            get(); // sip '\''
            return Quote.of(read(bind));
        } else if (token == RP || token == DOT) {
            throw error("unexpected '%s'", token);
        } else if (token instanceof Symbol s) {
            get();
            return bind == null ? s : bind.get(s, 0);
        } else if (token instanceof Int i) {
            get();
            return i;
        } else {
            throw error("unknown token '%s'", token);
        }
    }
}
