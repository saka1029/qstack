package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import saka1029.Common;

public class Context {
    
    static final Logger logger = Common.logger(Context.class);

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
    
    /**
     * a b c rot -> b c a
     */
    public void rot() {
        Element temp = stack[sp - 3];
        stack[sp - 3] = stack[sp - 2];
        stack[sp - 2] = stack[sp - 1];
        stack[sp - 1] = temp;
    }
    
    public void swap() {
        Element temp = stack[sp - 1];
        stack[sp - 1] = stack[sp - 2];
        stack[sp - 2] = temp;
    }
    
    public void drop() {
        --sp;
    }
    
    /**
     * スタックトップを残して、n個の要素をドロップする。
     * [1 2 3 4] : exit(2) -> [1 4]
     */
    public void exit(int n) {
        stack[sp - n - 1] = stack[sp - 1];
        sp -= n;
    }

    public void run(String source) {
        Reader reader = Reader.of(source);
        Element e;
        while ((e = reader.read()) != null)
            execute(e);
    }
    
    public Element eval(String source) {
        int baseSize = sp;
        run(source);
        int resultSize = sp - baseSize;
        assert resultSize == 1 : "%s result(s)".formatted(resultSize);
        return pop();
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
//        add("dup", c -> c.dup(0));
        add("@0", c -> c.dup(0));
        add("@1", c -> c.dup(1));
        add("@2", c -> c.dup(2));
        add("@3", c -> c.dup(3));
        add("@4", c -> c.dup(4));
        add("^1", c -> c.exit(1));
        add("^2", c -> c.exit(2));
        add("^3", c -> c.exit(3));
        add("^4", c -> c.exit(4));
        add("drop", c -> c.drop());
        add("swap", c -> c.swap());
        add("rot", c -> c.rot());
        add("execute", c -> c.execute(c.pop()));
        add("true", Bool.TRUE);
        add("false", Bool.FALSE);
        add("==", c -> { Element r = c.pop(), l = c.pop(); c.push(Bool.of(l.equals(r))); });
        add("!=", c -> { Element r = c.pop(), l = c.pop(); c.push(Bool.of(!l.equals(r))); });
        add("<", c -> { Ordered r = (Ordered)c.pop(), l = (Ordered)c.pop(); c.push(Bool.of(l.compareTo(r) < 0)); });
        add("<=", c -> { Ordered r = (Ordered)c.pop(), l = (Ordered)c.pop(); c.push(Bool.of(l.compareTo(r) <= 0)); });
        add(">", c -> { Ordered r = (Ordered)c.pop(), l = (Ordered)c.pop(); c.push(Bool.of(l.compareTo(r) > 0)); });
        add(">=", c -> { Ordered r = (Ordered)c.pop(), l = (Ordered)c.pop(); c.push(Bool.of(l.compareTo(r) >= 0)); });
        add("+", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value + r.value)); });
        add("-", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value - r.value)); });
        add("*", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value * r.value)); });
        add("/", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value / r.value)); });
        add("%", c -> { Int r = (Int)c.pop(), l = (Int)c.pop(); c.push(Int.of(l.value % r.value)); });
        add("car", c -> c.push(((Cons)c.pop()).car));
        add("cdr", c -> c.push(((Cons)c.pop()).cdr));
        add("cons", c -> { Element r = c.pop(), l = c.pop(); c.push(Cons.of(l, r)); });
        add("uncons", c -> { Cons e = (Cons)c.pop(); c.push(e.car); c.push(e.cdr); });
        add("null?", c -> c.push(Bool.of(c.pop().equals(List.NIL))));
        add("if", c -> {
            Element orElse = c.pop(), then = c.pop();
            execute(((Bool)c.pop()).value ? then : orElse);
        });
        add("define", c -> {
            Symbol name = (Symbol)c.pop();
            Element value = c.pop();
            globals.put(name, value);
        });
        add("foreach", c -> {
            Element clause = c.pop();
            List list = (List)c.pop();
            for (Element e : list) {
                c.push(e);
                c.execute(clause);
            }
        });
        add("for", c -> {
            Element closure = c.pop();
            int step = ((Int)c.pop()).value, end = ((Int)c.pop()).value, start = ((Int)c.pop()).value;
            if (step == 0)
                throw new RuntimeException("step == 0");
            else if (step > 0)
                for (int i = start; i <= end; i += step) {
                    c.push(Int.of(i));
                    c.execute(closure);
                }
            else
                for (int i = start; i >= end; i += step) {
                    c.push(Int.of(i));
                    c.execute(closure);
                }
        });
    }

}
