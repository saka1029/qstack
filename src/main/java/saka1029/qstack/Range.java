package saka1029.qstack;

import java.util.Iterator;

public class Range implements Value, Collection {
    
    final int start, end, step;
    
    Range(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }
    
    public static Range of(int start, int end, int step) {
        return new Range(start, end, step);
    }

    @Override
    public Iterator<Element> iterator() {
        return new Iterator<Element>() {
            
            int i = start;
            
            @Override
            public boolean hasNext() {
                return step > 0 ? i <= end : i >= end;
            }
            
            @Override
            public Element next() {
                int result = i;
                i += step;
                return Int.of(result);
            }
        };
    }

    @Override
    public Element at(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(Element index, Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Range(%d, %d, %d)".formatted(start, end, step);
    }
}
