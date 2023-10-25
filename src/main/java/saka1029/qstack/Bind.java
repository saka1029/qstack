package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;

public class Bind {
    
    final Bind previous;
    final Map<Symbol, Integer> bind = new HashMap<>();
    
    Bind(Bind previous, java.util.List<Symbol> names) {
        this.previous = previous;
        int offset = -names.size();
        for (Symbol s : names)
            bind.put(s, offset++);
    }
    
    public static Bind of(Bind previous, java.util.List<Symbol> names) {
        return new Bind(previous, names);
    }
    
    public Accessor get(Symbol name) {
        return get(name);
    }

    public Accessor get(Symbol name, int nest) {
        Integer offset = bind.get(name);
        if (offset == null)
            return previous == null ? null : previous.get(name, nest + 1);
        return Accessor.of(nest, offset);
    }
    
    @Override
    public String toString() {
        return "%s -> %s".formatted(bind, previous);
    }
}
