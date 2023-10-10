package saka1029.qstack;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

public class Reader {

    final java.io.Reader reader;
    int ch;

    Reader(java.io.Reader reader) {
        this.reader = reader;
        get();
    }

    public static Reader of(java.io.Reader reader) {
        return new Reader(reader);
    }

    public static Reader of(String source) {
        return new Reader(new StringReader(source));
    }

    int get() {
        try {
            return ch = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    List list() {
        get(); // skip '('
        spaces();
        java.util.List<Element> list = new java.util.ArrayList<>();
        while (ch != -1 && ch != ')' && ch != '.') {
            list.add(read());
            spaces();
        }
        switch (ch) {
            case ')':
                get(); // skip ')'
                return List.of(list, List.NIL);
            case '.':
                get(); // skip '.'
                Element tail = read();
                spaces();
                if (ch != ')')
                    throw error("')' expected");
                get(); // skip ')'
                return List.of(list, tail);
            default:
                throw error("')' or '.' expected");
        }
    }
    
    Quote quote() {
        get(); // skip '\''
        return Quote.of(read());
    }

    static final Pattern INT_PAT = Pattern.compile("[+-]?\\d+");

    static boolean isWord(int ch) {
        return switch (ch) {
            case '(', ')', '.', '\'', '"', -1 -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    Element element() {
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.append((char) ch);
            get();
        }
        String word = sb.toString();
        if (INT_PAT.matcher(word).matches())
            return Int.of(Integer.parseInt(word));
        else
            return Symbol.of(word);
    }

    Str str() {
        get(); // skip '"'
        StringBuilder sb = new StringBuilder();
        while (ch != -1 && ch != '"') {
            if (ch == '\\') {
                get(); // skip '\\'
                switch (ch) {
                    case 'r': sb.append('\r'); break;
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append((char)ch); break;
                }
            } else
                sb.append((char)ch);
            get();
        }
        if (ch != '"')
            throw error("'\"' expected");
        return Str.of(sb.toString());
    }

    public Element read() {
        spaces();
        switch (ch) {
            case -1:
                return null;
            case '(':
                return list();
            case ')':
            case '.':
                throw error("unexpected '%c'", (char)ch);
            case '\'':
                return quote();
            case '"':
                return str();
            default:
                return element();
        }
    }

}
