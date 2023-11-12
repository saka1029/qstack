package saka1029.qstack;

public interface Collection extends Iterable<Element> {
    Element at(int index);
    void put(int index, Element e);
    int size();
}
