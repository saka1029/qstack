package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;

public class Bind {
    
    final Bind previous;
    final Map<Symbol, Integer> bind = new HashMap<>();
    
    Bind(Bind previous, java.util.List<Symbol> args) {
        this.previous = previous;
        int offset = -args.size();
        for (Symbol s : args)
            bind.put(s, offset++);
    }
    
    public static Bind of(Bind previous, java.util.List<Symbol> args) {
        return new Bind(previous, args);
    }
    
    public void add(Symbol symbol, int offset) {
        bind.put(symbol, offset);
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
