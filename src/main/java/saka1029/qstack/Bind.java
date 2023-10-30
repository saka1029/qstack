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
    
    public Element get(Symbol symbol, int nest) {
        Integer offset = bind.get(symbol);
        if (offset == null)
            return previous == null ? symbol : previous.get(symbol, nest + 1);
        return Accessor.symbol(nest, offset);
    }
    
    @Override
    public String toString() {
        return "%s -> %s".formatted(bind, previous);
    }
}
