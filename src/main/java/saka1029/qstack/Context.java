package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;

public class Context {
    
    public final Element[] stack;
    public int sp = 0;
    public final Map<String, Element> globals = new HashMap<>();
    public final Map<String, Reference> references = new HashMap<>();
    
    public static final Element DUP = c -> c.push(c.peek(0));
    public static final Element DROP = c -> c.pop();
    public static final Element EXECUTE = c -> c.execute(c.pop());
    public static final Element IF = c -> {
    	Element otherwise = c.pop(), then = c.pop();
    	if (((Bool)c.pop()).value)
    		c.execute(then);
    	else
    		c.execute(otherwise);
    };
    public static final Element PLUS = c -> {
    	Int r = (Int)c.pop(), l = (Int)c.pop();
    	c.push(Int.of(l.value + r.value));
    };
    public static final Element MINUS = c -> {
    	Int r = (Int)c.pop(), l = (Int)c.pop();
    	c.push(Int.of(l.value - r.value));
    };
    public static final Element MULT = c -> {
    	Int r = (Int)c.pop(), l = (Int)c.pop();
    	c.push(Int.of(l.value * r.value));
    };
    
    Context(int stackSize) {
        this.stack = new Element[stackSize];
    }
    
    public static Context of(int stackSize) {
        return new Context(stackSize);
    }
    
    public void execute(Element e) {
        e.execute(this);
    }
    
    public void push(Element e) {
        stack[sp++] = e;
    }
    
    public Element peek(int index) {
        return stack[sp - index - 1];
    }
    
    public Element pop() {
        return stack[--sp];
    }
    
    public void dup(int index) {
        push(stack[sp - index - 1]);
    }

}
