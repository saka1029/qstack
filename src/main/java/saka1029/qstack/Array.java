package saka1029.qstack;

import java.util.Arrays;

public class Array implements Value {

    final Element[] array;
    
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
        return array[index];
    }
    
    public void set(int index, Element element) {
        array[index] = element;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
