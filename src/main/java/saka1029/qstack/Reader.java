package saka1029.qstack;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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

    Element namedFrame(Bind bind) {
        spaces();
        if (ch == -1 || ch == ')' || ch == '.')
            throw error("Int (number of returns) expected");
        java.util.List<Symbol> args = new ArrayList<>();
        Element e = read();
        if (!(e instanceof Int i))
            throw error("Int (number of returns) expected");
        int returns = i.value;
        for (e = read(); e != null && e instanceof Symbol s && !e.equals(Symbol.of(":")); e = read())
            args.add(s);
        if (e == null || !e.equals(Symbol.of(":")))
            throw error("':' expected");
        bind = Bind.of(bind, args);
        java.util.List<Element> list = new ArrayList<>();
        spaces();
        while (ch != -1 && ch != ')' && ch != '.') {
            list.add(read(bind));
            spaces();
        }
        switch (ch) {
            case ')':
                get(); // skip ')'
                return Block.of(list, List.NIL, args.size(), returns);
            case '.':
                get(); // skip '.'
                Element tail = read(bind);
                spaces();
                if (ch != ')')
                    throw error("')' expected");
                get(); // skip ')'
                return Block.of(list, tail, args.size(), returns);
            default:
                throw error("')' or '.' expected");
        }
    }
    
    Element frame(java.util.List<Element> elements, Element tail) {
        int size = elements.size();
        if (size >= 3
            && elements.get(0) instanceof Int args
            && elements.get(1) instanceof Int returns
            && elements.get(2).equals(Symbol.of(":")))
            return Block.of(elements.subList(3, size), tail, args.value, returns.value);
        else
            return List.of(elements, tail);
    }

    Element list(Bind bind) {
        get(); // skip '('
        spaces();
        java.util.List<Element> list = new ArrayList<>();
        if (ch != -1 && ch != ')' && ch != '.') {
            Element first = read(bind);
            if (first.equals(Symbol.of("F")))
                return namedFrame(bind);
            list.add(first);
        }
        spaces();
        while (ch != -1 && ch != ')' && ch != '.') {
            list.add(read(bind));
            spaces();
        }
        switch (ch) {
            case ')':
                get(); // skip ')'
                return frame(list, List.NIL);
            case '.':
                get(); // skip '.'
                Element tail = read(bind);
                spaces();
                if (ch != ')')
                    throw error("')' expected");
                get(); // skip ')'
                return frame(list, tail);
            default:
                throw error("')' or '.' expected");
        }
    }
    
    Quote quote(Bind bind) {
        get(); // skip '\''
        return Quote.of(read(bind));
    }

    static final Pattern INT_PAT = Pattern.compile("[+-]?\\d+");

    static boolean isWord(int ch) {
        return switch (ch) {
            case '(', ')', '.', '\'', '"', -1 -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    Element symbolOrInt(Bind bind) {
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.append((char) ch);
            get();
        }
        String word = sb.toString();
        if (INT_PAT.matcher(word).matches())
            return Int.of(Integer.parseInt(word));
        else if (bind == null)
            return Symbol.of(word);
        else
            return bind.get(Symbol.of(word), 0);
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
        get(); // skip '"'
        return Str.of(sb.toString());
    }

    public Element read(Bind bind) {
        spaces();
        switch (ch) {
            case -1:
                return null;
            case '(':
                return list(bind);
            case ')':
            case '.':
                throw error("unexpected '%c'", (char)ch);
            case '\'':
                return quote(bind);
            case '"':
                return str();
            default:
                return symbolOrInt(bind);
        }
    }
    
    public Element read() {
        return read(null);
    }
}
