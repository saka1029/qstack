package saka1029.qstack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface List extends Traceable, Iterable<Element> {
    
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

    public static Element of(java.util.List<Element> elements) {
        List result = NIL;
        for (int i = elements.size() - 1; i >= 0; --i)
            result = Cons.of(elements.get(i), result);
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
