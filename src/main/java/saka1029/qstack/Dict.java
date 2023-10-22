package saka1029.qstack;

import java.util.LinkedList;

public class Dict {
    
    final LinkedList<Symbol> names = new LinkedList<>();
    final Dict prev;
    
    Dict(Dict prev) {
        this.prev = prev;
    }
    
    public void add(Symbol name) {
        names.addFirst(name);
    }
}
