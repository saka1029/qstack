package saka1029.qstack;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Reader {
    
    public static final Symbol LP = Symbol.of("(");
    public static final Symbol RP = Symbol.of(")");
    public static final Symbol QUOTE = Symbol.of("'");
    public static final Symbol COLON = Symbol.of(":");

    final java.io.Reader reader;
    int ch;
    Element token;
    
    Reader(java.io.Reader reader) {
        this.reader = reader;
        getCh();
        get();
    }
    
    public static Reader of(String s) {
        return new Reader(new StringReader(s));
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
    
    Str str() {
        getCh(); // skip '\'"
        StringBuilder sb = new StringBuilder();
        while (ch != -1 && ch != '\"') {
            if (ch == '\\') {
                getCh(); // skip '\\'
                switch (ch) {
                    case 'r': sb.append('\r'); break;
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append((char)ch); break;
                }
            } else
                sb.append((char)ch);
            getCh();
        }
        if (ch != '\"')
            throw error("'\"' expected");
        getCh(); // skip '\"'
        return Str.of(sb.toString());
    }

    Element get() {
        spaces();
        return token = switch (ch) {
            case -1 -> null;
            case '(' -> get(LP);
            case ')' -> get(RP);
            case '\'' -> get(QUOTE);
            case '\"' -> str();
            default -> symbolOrInt();
        };
    }
    
    RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }

    Element list() {
        java.util.List<Element> list = new ArrayList<>();
        get(); // skip '('
        while (token != null && token != RP)
            list.add(read());
        if (token != RP)
            throw error("')' expected");
        get(); // skip ')'
        return List.of(list);
    }
    
    public Element read() {
        if (token == null) {
            return null;
        } else if (token == LP) {
            return list();
        } else if (token == QUOTE) {
            get(); // sip '\''
            return Quote.of(read());
        } else if (token == RP) {
            throw error("unexpected '%s'", token);
        } else if (token instanceof Symbol || token instanceof Int || token instanceof Str) {
            Element r = token;
            get();
            return r;
        } else {
            throw error("unknown token '%s'", token);
        }
    }
}
