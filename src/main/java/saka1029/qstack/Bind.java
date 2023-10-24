package saka1029.qstack;

import java.util.LinkedList;

public class Bind {
    
    final LinkedList<Symbol> names = new LinkedList<>();
    final Bind prev;
    
    Bind(Bind prev) {
        this.prev = prev;
    }
    
    public void add(Symbol name) {
        names.addFirst(name);
    }
    
    public Element load(Symbol name) {
        int nest = 0;
        for (Bind b = this; b != null; b = b.prev, ++nest) {
            int offset = b.names.indexOf(name);
            if (offset >= 0) {
                int n = nest;
                return c -> c.load(n, -(offset + 1));
            }
        }
        return null;
    }
    
    public Element store(Symbol name) {
        int nest = 0;
        for (Bind b = this; b != null; b = b.prev, ++nest) {
            int offset = b.names.indexOf(name);
            if (offset >= 0) {
                int n = nest;
                return c -> c.store(n, -(offset + 1));
            }
        }
        return null;
    }
}
