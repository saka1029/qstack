package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Symbol implements Element {
	
    static final Map<String, Symbol> symbols = new HashMap<>();
	public final String name;
	
	private Symbol(String name) {
		this.name = name;
	}
	
	public static Symbol of(String name) {
	    return symbols.computeIfAbsent(name, n -> new Symbol(n));
	}
	
	@Override
	public void execute(Context c) {
	    Element value = c.globals.get(this);
	    Objects.requireNonNull(value, "symbol '%s' undefined".formatted(this));
		c.execute(value);
	}

	@Override
	public String toString() {
		return name;
	}
}
