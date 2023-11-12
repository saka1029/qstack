package saka1029.qstack;

public interface Collection extends Iterable<Element> {
    Element at(int index);
    void put(Element index, Element e);
    int size();
}
