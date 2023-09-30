package saka1029.qstack;

import java.io.IOException;
import java.util.regex.Pattern;

public class Reader {

    final java.io.Reader reader;
    final Context context;
    int ch;

    Reader(Context context, java.io.Reader reader) {
        this.context = context;
        this.reader = reader;
        get();
    }

    public static Reader of(Context context, java.io.Reader reader) {
        return new Reader(context, reader);
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
            case '(', ')', '\'', -1 -> false;
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
            return Reference.of(word);
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
            default:
                return element();
        }
    }

}
