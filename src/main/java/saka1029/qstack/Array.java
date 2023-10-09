package saka1029.qstack;

import java.util.Arrays;

public class Array implements Value {

    public final Element[] array;
    
    Array(int size) {
        this.array = new Element[size];
        Arrays.fill(this.array, Bool.FALSE);
    }
    
    public static Array of(int size) {
        return new Array(size);
    }
    
    public int size() {
        return array.length;
    }
    
    public Element get(int index) {
        return array[index - 1];
    }
    
    public void set(int index, Element element) {
        array[index - 1] = element;
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
