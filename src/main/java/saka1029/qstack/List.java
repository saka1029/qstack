package saka1029.qstack;

import java.util.Iterator;
import java.util.ListIterator;
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

    public static List of(java.util.List<Element> elements, Element tail) {
        ListIterator<Element> i = elements.listIterator(elements.size());
        if (!i.hasPrevious())
            if (tail == NIL)
                return NIL;
            else
                throw new IllegalArgumentException("Empty elements");
        Cons result = Cons.of(i.previous(), tail);
        while (i.hasPrevious())
            result = Cons.of(i.previous(), result);
        return result;
    }
    
    default int length() {
        int length = 0;
        for (@SuppressWarnings("unused") Element e : this)
            ++length;
        return length;
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
