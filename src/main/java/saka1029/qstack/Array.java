package saka1029.qstack;

import java.util.Arrays;
import java.util.Iterator;

public class Array implements Value, Collection {

    public final Element[] array;
    
    Array(int size) {
        this.array = new Element[size];
        Arrays.fill(this.array, Bool.FALSE);
    }
    
    public static Array of(int size) {
        return new Array(size);
    }
    
    @Override
    public Element at(int index) {
        return array[index - 1];
    }
    
    @Override
    public void put(Element index, Element element) {
        array[((Int)index).value - 1] = element;
    }
    
    @Override
    public int size() {
        return array.length;
    }
    
    @Override
    public Iterator<Element> iterator() {
        return new Iterator<>() {
            
            int index = 0, size = size();

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Element next() {
                return array[index++];
            }
        };
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Array a && Arrays.deepEquals(a.array, array);
    }
    
    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
