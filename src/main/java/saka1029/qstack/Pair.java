package saka1029.qstack;

import java.util.Objects;

public class Pair implements List {
    
    public final Element head, tail;
    
    Pair(Element head, Element tail) {
        this.head = head;
        this.tail = tail;
    }
    
    public static Pair of(Element head, Element tail) {
        return new Pair(head, tail);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair p && p.head.equals(head) && p.tail.equals(tail);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(head);
        Element e = tail;
        for (; e instanceof Pair p; e = p.tail)
            sb.append(" ").append(p.head);
        if (e != NIL)
            sb.append(" . ").append(e);
        return sb.append(")").toString();
    }
}
