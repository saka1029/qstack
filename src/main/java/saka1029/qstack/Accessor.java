package saka1029.qstack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A3 : Accessor.of(0, -3)
 * A2 : Accessor.of(0, -2)
 * A1 : Accessor.of(0, -1)
 * A0 : Accessor.of(0, 0) --> 旧fp
 * L0 : Accessor.of(0, 1) --> self
 * L1 : Accessor.of(0, 2)
 * L2 : Accessor.of(0, 3)
 * L3 : Accessor.of(0, 4)
 */
public class Accessor implements Traceable {
    
    static final Pattern PAT = Pattern.compile("([AL])([1-9])([0-9])?");
    final int nest, offset;
    
    Accessor(int nest, int offset) {
        this.nest = nest;
        this.offset = offset;
    }
    
    public static Accessor of(int nest, int offset) {
        return new Accessor(nest, offset);
    }
    
    public static Accessor of(String symbol) {
        Matcher m = PAT.matcher(symbol);
        if (!m.matches())
            return null;
        String ns = m.group(3);
        int nest = ns == null ? 0 : Integer.parseInt(ns);
        int n = Integer.parseInt(m.group(2));
        int offset = m.group(1).equals("A") ? -n : n + 1;
        return new Accessor(nest, offset);
    }

    public static String name(int nest, int offset) {
        return (offset < 0 ? "A" + -offset : "L" + (offset - 1))  + (nest == 0 ? "" : nest);
    }
    public static Symbol symbol(int nest, int offset) {
        return Symbol.of(name(nest, offset));
    }

    @Override
    public void execute(Context c) {
        c.load(nest, offset);
    }
    
    public void store(Context c) {
        c.store(nest, offset);
    }
    
    @Override
    public int hashCode() {
        return nest << 4 | offset;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Accessor a && a.nest == nest && a.offset == offset;
    }

    @Override
    public String toString() {
        return name(nest, offset);
    }
}
