package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import saka1029.Common;

public class Context {
    
    static final Logger logger = Common.logger(Context.class);

    public final Element[] stack;
    public int sp = 0, fp = 0, nest = 0;
    public final Map<Symbol, Element> globals = new HashMap<>();
    public Consumer<String> output = null, trace = null;

    Context(int stackSize) {
        this.stack = new Element[stackSize];
        standard();
    }
    
    public static Context of(int stackSize) {
        return new Context(stackSize);
    }
    
    public Context trace(Consumer<String> trace) {
        this.trace = trace;
        return this;
    }
    
    public void trace(String text) {
        if (trace != null)
            trace.accept(text);
    }
    
    public Context output(Consumer<String> output) {
        this.output = output;
        return this;
    }
    
    public void output(String text) {
        if (output != null)
            output.accept(text);
    }

    public void execute(Element e) {
        boolean t = e instanceof Traceable;
        if (t)
            trace("  ".repeat(nest++) + this + " " + e);
        e.execute(this);
        if (t)
            --nest;
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
    
    /**
     * rotの逆回転版
     * a b c rrot -> c a b
     */
    public void rrot() {
        Element temp = stack[sp - 1];
        stack[sp - 1] = stack[sp - 2];
        stack[sp - 2] = stack[sp - 3];
        stack[sp - 3] = temp;
    }
    
    public void swap() {
        Element temp = stack[sp - 1];
        stack[sp - 1] = stack[sp - 2];
        stack[sp - 2] = temp;
    }
    
    public void drop() {
        --sp;
    }
    
    public void drop(int count) {
        if (sp < count)
            throw new IllegalArgumentException("sp(" + sp + ") < count(" + count + ")");
        sp -= count;
    }
    
    /**
     * スタックトップを残して、n個の要素をドロップする。
     * [1 2 3 4] : exit(2) -> [1 4]
     */
    public void unwind(int n) {
        stack[sp - n - 1] = stack[sp - 1];
        sp -= n;
    }

    public void run(String source) {
        Reader reader = Reader.of(source);
        Element e;
        while ((e = reader.read()) != null)
            execute(e);
        trace("  ".repeat(nest) + this);
    }
    
    public Element eval(String source) {
        int baseSize = sp;
        run(source);
        int resultSize = sp - baseSize;
        assert resultSize == 1 : "%s result(s)".formatted(resultSize);
        Element result = pop();
        trace(result.toString());
        return result;
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
        add("^1", c -> c.unwind(1));
        add("^2", c -> c.unwind(2));
        add("^3", c -> c.unwind(3));
        add("^4", c -> c.unwind(4));
        add("drop", c -> c.drop());
        add("drop2", c -> c.drop(2));
        add("drop3", c -> c.drop(3));
        add("swap", c -> c.swap());
        add("rot", c -> c.rot());
        add("rrot", c -> c.rrot());
        add("execute", c -> c.execute(c.pop()));
        add("true", Bool.TRUE);
        add("false", Bool.FALSE);
        add("and", c -> c.push(Bool.of(((Bool)c.pop()).value & ((Bool)c.pop()).value)));
        add("or", c -> c.push(Bool.of(((Bool)c.pop()).value | ((Bool)c.pop()).value)));
        add("xor", c -> c.push(Bool.of(((Bool)c.pop()).value ^ ((Bool)c.pop()).value)));
        add("not", c -> c.push(Bool.of(!((Bool)c.pop()).value)));
        add("==", c -> c.push(Bool.of(c.pop().equals(c.pop()))));
        add("!=", c -> c.push(Bool.of(!c.pop().equals(c.pop()))));
        add("<", c -> c.push(Bool.of(((Ordered)c.pop()).compareTo((Ordered)c.pop()) > 0)));
        add("<=", c -> c.push(Bool.of(((Ordered)c.pop()).compareTo((Ordered)c.pop()) >= 0)));
        add(">", c -> c.push(Bool.of(((Ordered)c.pop()).compareTo((Ordered)c.pop()) < 0)));
        add(">=", c -> c.push(Bool.of(((Ordered)c.pop()).compareTo((Ordered)c.pop()) <= 0)));
        add("+", c -> c.push(Int.of(((Int)c.pop()).value + ((Int)c.pop()).value)));
        add("-", c -> c.push(Int.of(-((Int)c.pop()).value + ((Int)c.pop()).value)));
        add("*", c -> c.push(Int.of(((Int)c.pop()).value * ((Int)c.pop()).value)));
        add("/", c -> { Int r = (Int)c.pop(); c.push(Int.of(((Int)c.pop()).value / r.value)); });
        add("%", c -> { Int r = (Int)c.pop(); c.push(Int.of(((Int)c.pop()).value % r.value)); });
        add("car", c -> c.push(((Cons)c.pop()).car));
        add("cdr", c -> c.push(((Cons)c.pop()).cdr));
        add("cons", c -> { Element r = c.pop(), l = c.pop(); c.push(Cons.of(l, r)); });
        add("rcons", c -> { Element r = c.pop(), l = c.pop(); c.push(Cons.of(r, l)); });
        add("uncons", c -> { Cons e = (Cons)c.pop(); c.push(e.car); c.push(e.cdr); });
        add("quote", c -> c.push(Quote.of(c.pop())));
        add("null?", c -> c.push(Bool.of(c.pop().equals(List.NIL))));
        add("list?", c -> c.push(Bool.of(c.pop() instanceof List)));
        add("length", c -> c.push(Int.of(((List)c.pop()).length())));
        add("append", c -> { List right = (List)c.pop(); c.push(List.append(c.pop(), right)); });
        add("reverse", c -> {
            List list = (List)c.pop();
            Element result = List.NIL;
            for (Element e : list)
                result = Cons.of(e, result);
            c.push(result);
        });
        add("if", c -> {
            Element orElse = c.pop(), then = c.pop();
            execute(((Bool)c.pop()).value ? then : orElse);
        });
        add("define", c -> globals.put((Symbol)c.pop(), c.pop()));
        add("foreach", c -> {
            Element body = c.pop();
            for (Element e : (List)c.pop()) {
                c.push(e);
                c.execute(body);
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
        add("stack", c -> output(c.toString()));
        add("print", c -> output("" + c.pop()));
        add("println", c -> output(c.pop() + System.lineSeparator()));
        
        add("array", c -> c.push(Array.of(((Int)c.pop()).value)));
        add("size", c -> c.push(Int.of(((Array)c.pop()).size())));
        add("get", c -> { int i = ((Int)c.pop()).value; c.push(((Array)c.pop()).get(i)); });
        add("set", c -> { Element e = c.pop(); int i = ((Int)c.pop()).value; ((Array)c.pop()).set(i, e); });
        add("L-A", c -> {
            List list = (List)c.pop();
            int length = list.length();
            Array array = Array.of(length);
            int i = 1;
            for (Element e : list)
                array.set(i++, e);
            c.push(array);
        });
        add("A-L", c -> c.push(List.of(((Array)c.pop()).array)));
        // Frameの引数アクセス
        add("A1", c -> c.push(stack[c.fp - 1]));
        add("A2", c -> c.push(stack[c.fp - 2]));
        add("A3", c -> c.push(stack[c.fp - 3]));
        add("A4", c -> c.push(stack[c.fp - 4]));
        add("A5", c -> c.push(stack[c.fp - 5]));
        add("A6", c -> c.push(stack[c.fp - 6]));
        // Frameのローカル変数参照
        add("L1", c -> c.push(stack[c.fp + 2]));
        add("L2", c -> c.push(stack[c.fp + 3]));
        add("L3", c -> c.push(stack[c.fp + 4]));
        // Frameのローカル変数更新
        add("S1", c -> stack[c.fp + 2] = c.pop());
        add("S2", c -> stack[c.fp + 3] = c.pop());
        add("S3", c -> stack[c.fp + 4] = c.pop());
        add("self", c -> c.execute(stack[c.fp + 1]));
    }

}
