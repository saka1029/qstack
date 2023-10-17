package saka1029.qstack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public interface List extends Element, Iterable<Element> {
    
    public static final List NIL = new List() {
        @Override
        public String toString() {
            return "()";
        }
    };

    public static List of(Element... es) {
        List result = NIL;
        for (int i = es.length - 1; i >= 0; --i)
            result = Cons.of(es[i], result);
        return result;
    }

    public static Element of(ArrayList<Element> elements, Element tail) {
        int size = elements.size();
        if (size == 0)
            if (tail == NIL)
                return NIL;
            else
                throw new IllegalArgumentException("Empty elements");
        int args = -1, returns = -1;
        if  (size >= 3
            && elements.get(0) instanceof Int a
            && elements.get(1) instanceof Int r
            && elements.get(2).equals(Symbol.of(":"))) {
            args = a.value;
            returns = r.value;
        }
        Element result = tail, first = elements.get(0);
        for (int i = size - 1; i > 0; --i)
            result = Cons.of(elements.get(i), result);
        if (args >= 0)
            result = Block.of(args, returns, first, result);
        else
            result =  Cons.of(first, result);
        return result;
    }
    
    default int length() {
        int length = 0;
        for (@SuppressWarnings("unused") Element e : this)
            ++length;
        return length;
    }

    public static List append(Element left, List right) {
        return left instanceof Cons c ? Cons.of(c.car, append(c.cdr, right)) : right;
    }

    @Override
    default void execute(Context c) {
        for (Element e : this)
            c.execute(e);
    }
    
    @Override
    default Iterator<Element> iterator() {
        return new Iterator<>() {
            
            Element next = List.this;

            @Override
            public boolean hasNext() {
                return next instanceof Cons;
            }

            @Override
            public Element next() {
                if (!(next instanceof Cons p))
                    throw new NoSuchElementException();
                Element result = p.car;
                next = p.cdr;
                return result;
            }
        };
    }
}
