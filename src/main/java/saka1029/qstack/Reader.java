package saka1029.qstack;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
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

    Element elements(Bind bind, java.util.List<Element> list) {
        spaces();
        Element tail = List.NIL;
        while (ch != -1 && ch != ')') {
            Element e = readMaybeDot(bind);
            if (e.equals(Symbol.of("."))) {
                tail = read(bind);
                break;
            }
            list.add(e);
            spaces();
        }
        if (ch != ')')
            error("')' expected");
        get(); // skip ')'
        return tail;
    }

    Element namedFrame(Bind bind) {
        spaces();
        Element er = read();
        if (!(er instanceof Int nr))
            throw error("int (number of returns) expected but %s", er);
        int returns = nr.value;
        spaces();
        if (ch != '(')
            throw error("'(' expected");
        get(); // skip '('
        java.util.List<Symbol> args = new ArrayList<>();
        spaces();
        while (ch != -1 && ch != ':' && ch != ')') {
            Element e = read();
            if (!(e instanceof Symbol s))
                throw error("symbol expected but %s", e);
            args.add(s);
            spaces();
        }
        Bind newBind = Bind.of(bind, args); // 引数をバインドする。
        if (ch == -1)
            throw error("':' or ')' expected but EOF found");
        java.util.List<Element> values = new ArrayList<>();
        java.util.List<Symbol> vars = new ArrayList<>();
        if (ch == ':') {
            get(); // skip ':'
            spaces();
            int offset = 2;
            while (ch != -1 && ch != ')') {
                Element value = read(newBind);
                spaces();
                if (ch == -1 || ch == ')')
                    throw error("local variable (symbol) expected");
                Element var = read(); // ローカル変数名はBindなしで読む。
                if (!(var instanceof Symbol s))
                    throw error("local variable (symbol) expected but %s", var);
                values.add(value);
                vars.add(s);
                newBind.add(s, offset++);
                spaces();
            }
        }
        if (ch != ')')
            throw error("')' expected");
        get(); // skip ')'
        Element eList = read(bind);
        if (!(eList instanceof List parms))
            throw error("argument list expected but %s", eList);
//        java.util.List<Symbol> args = new ArrayList<>();
        java.util.List<Element> localValues = new ArrayList<>();
        java.util.List<Symbol> localVariables = new ArrayList<>();
        boolean colonFound = false;
        for (Iterator<Element> it = parms.iterator(); it.hasNext();) {
            Element e = it.next();
            if (colonFound) {
                if (e.equals(Symbol.of(":")))
                    throw error("local value expected but %s", e);
                if (!it.hasNext())
                    throw error("local variable expected after %s", e);
                Element f = it.next();
                if (f instanceof Symbol localVariable && !localVariable.equals(Symbol.of(":"))) {
                    localValues.add(e);
                    localVariables.add(localVariable);
                } else
                    throw error("local variable expected but %s", f);
            } else if (e instanceof Symbol s) {
                if (s.equals(Symbol.of(":")))
                    colonFound = true;
                else
                    args.add(s);
            } else
                throw error("unexpected element %s", e);
        }
//        bind = Bind.of(bind, args, localVariables);
        java.util.List<Element> list = new ArrayList<>();
        for (Element e : localValues)
            list.add(e);
        spaces();
        Element tail = elements(bind, list);
        return Block.of(list, tail, args.size(), returns);
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
        if (ch != -1 && ch != ')') {
            Element first = read(bind);
            if (first.equals(Symbol.of("frame")))
                return namedFrame(bind);
            list.add(first);
            spaces();
        }
        Element tail = elements(bind, list);
        return frame(list, tail);
    }
    
    Quote quote(Bind bind) {
        get(); // skip '\''
        return Quote.of(read(bind));
    }

    static final Pattern INT_PAT = Pattern.compile("[+-]?\\d+");

    static boolean isWord(int ch) {
        return switch (ch) {
            case '(', ')', '\'', '"', -1 -> false;
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
        else {
            Symbol symbol = Symbol.of(word);
            return bind == null ? symbol : bind.get(symbol, 0);
        }
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

    public Element readMaybeDot(Bind bind) {
        spaces();
        switch (ch) {
            case -1:
                return null;
            case '(':
                return list(bind);
            case ')':
                throw error("unexpected '%c'", (char)ch);
            case '\'':
                return quote(bind);
            case '"':
                return str();
            default:
                return symbolOrInt(bind);
        }
    }

    public Element read(Bind bind) {
        Element e = readMaybeDot(bind);
        if (e != null && e.equals(Symbol.of(".")))
            throw error("invalid character '.'");
        return e;
    }
    
    public Element read() {
        return read(null);
    }
}
