package saka1029.qstack;

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
            result = Pair.of(es[i], result);
        return result;
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
                return next instanceof Pair;
            }

            @Override
            public Element next() {
                if (!(next instanceof Pair p))
                    throw new NoSuchElementException();
                Element result = p.head;
                next = p.tail;
                return result;
            }
        };
    }
}
