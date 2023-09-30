package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Context {

    public final Element[] stack;
    public int sp = 0;
    public final Map<Symbol, Element> globals = new HashMap<>();

    Context(int stackSize) {
        this.stack = new Element[stackSize];
        standard();
    }

    public static Context of(int stackSize) {
        return new Context(stackSize);
    }

    public void execute(Element e) {
        e.execute(this);
    }
    
    public void execute(String name) {
        execute(globals.get(Symbol.of(name)));
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
    
    public void drop() {
        --sp;
    }
    
    @Override
    public String toString() {
        return IntStream.range(0, sp)
            .mapToObj(i -> stack[i].toString())
            .collect(Collectors.joining(" ", "[", "]"));
    }
    
    public Symbol add(String name, Element value) {
        Symbol key = Symbol.of(name);
        globals.put(key, value);
        return key;
    }

    void standard() {
        add("dup", c -> c.dup(0));
        add("@0", c -> c.dup(0));
        add("@1", c -> c.dup(1));
        add("@2", c -> c.dup(2));
        add("@3", c -> c.dup(3));
        add("drop", c -> c.drop());
        add("execute", c -> c.execute(c.pop()));
        add("+", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value + r.value)); });
        add("-", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value - r.value)); });
        add("*", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value * r.value)); });
        add("if", c -> {
            Element orElse = c.pop(), then = c.pop();
            execute(((Bool)c.pop()).value ? then : orElse);
        });
    }

}
