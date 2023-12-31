package saka1029.qstack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import saka1029.Common;

public class Context {
    
    static final Logger logger = Common.logger(Context.class);
    static final Value YIELD = new Value() {
        @Override
        public String toString() {
            return "yield";
        }
    };

    public final Element[] stack;
    public int sp = 0, nest = 0;
    public final Map<Symbol, Element> globals;
    public Consumer<String> output, trace;

    Context(Element[] stack, Map<Symbol, Element> globals, Consumer<String> output, Consumer<String> trace) {
        this.stack = stack;
        this.globals = globals;
        this.output = output;
        this.trace = trace;
    }
    Context(int stackSize) {
        this(new Element[stackSize], new HashMap<>(), System.out::println, null);
        standard();
    }
    
    public static Context of(int stackSize) {
        return new Context(stackSize);
    }
    
    public Context child() {
        return new Context(new Element[stack.length], globals, output, trace);
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
    
    static final Pattern CLASS_CAST_EXCEPTION = Pattern.compile(
        "class \\S*\\.(\\S+) cannot be cast to class \\S*\\.(\\S+).*");
    
    static RuntimeException error(ClassCastException ex, Element e) {
        Matcher m = CLASS_CAST_EXCEPTION.matcher(ex.getMessage());
        if (m.find())
            return new RuntimeException("Cast error %s to %s at '%s'"
                .formatted(m.group(1), m.group(2), e), ex);
        else
            return new RuntimeException(ex);
    }

    public void run(String source) {
        Reader reader = Reader.of(source);
        Element e;
        while ((e = reader.read()) != null)
            try {
                execute(e);
            } catch (ClassCastException ex) {
                throw error(ex, e);
            }
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
            .mapToObj(i -> "" + stack[i])
            .collect(Collectors.joining(" ", "[", "]"));
    }
    
    public Symbol add(String name, Element value) {
        Symbol key = Symbol.of(name);
        globals.put(key, value);
        return key;
    }

    static int i(Element e) {
        return ((Int)e).value;
    }
    
    static Int i(int i) {
        return Int.of(i);
    }
    
    static boolean b(Element e) {
        return ((Bool)e).value;
    }
    
    static Bool b(boolean b) {
        return Bool.of(b);
    }
    
    static Ordered o(Element e) {
        return (Ordered)e;
    }

    void standard() {
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
        add("and", c -> c.push(b(b(c.pop()) & b(c.pop()))));
        add("or", c -> c.push(b(b(c.pop()) | b(c.pop()))));
        add("xor", c -> c.push(b(b(c.pop()) ^ b(c.pop()))));
        add("not", c -> c.push(b(!b(c.pop()))));
        add("==", c -> c.push(b(c.pop().equals(c.pop()))));
        add("!=", c -> c.push(b(!c.pop().equals(c.pop()))));
        add("<", c -> c.push(b(o(c.pop()).compareTo(o(c.pop())) > 0)));
        add("<=", c -> c.push(b(o(c.pop()).compareTo(o(c.pop())) >= 0)));
        add(">", c -> c.push(b(o(c.pop()).compareTo(o(c.pop())) < 0)));
        add(">=", c -> c.push(b(o(c.pop()).compareTo(o(c.pop())) <= 0)));
        add("+", c -> c.push(i(i(c.pop()) + i(c.pop()))));
        add("-", c -> c.push(i(-i(c.pop()) + i(c.pop()))));
        add("*", c -> c.push(i(i(c.pop()) * i(c.pop()))));
        add("/", c -> { int r = i(c.pop()); c.push(i(i(c.pop()) / r)); });
        add("%", c -> { int r = i(c.pop()); c.push(i(i(c.pop()) % r)); });
        add("car", c -> c.push(((Cons)c.pop()).car));
        add("cdr", c -> c.push(((Cons)c.pop()).cdr));
        add("cons", c -> { Element r = c.pop(), l = c.pop(); c.push(Cons.of(l, (List)r)); });
        add("rcons", c -> { Element r = c.pop(), l = c.pop(); c.push(Cons.of(r, (List)l)); });
        add("uncons", c -> { Cons e = (Cons)c.pop(); c.push(e.car); c.push(e.cdr); });
//        add("nil", c -> c.push(List.NIL));
        add("quote", c -> c.push(Quote.of(c.pop())));
        add("null?", c -> c.push(b(c.pop().equals(List.NIL))));
        add("list?", c -> c.push(b(c.pop() instanceof List)));
        add("append", c -> { List right = (List)c.pop(); c.push(List.append(c.pop(), right)); });
        add("reverse", c -> {
            List list = (List)c.pop();
            List result = List.NIL;
            for (Element e : list)
                result = Cons.of(e, result);
            c.push(result);
        });
        add("if", c -> {
            Element orElse = c.pop(), then = c.pop();
            execute(((Bool)c.pop()).value ? then : orElse);
        });
        add("define", c -> globals.put((Symbol)c.pop(), c.pop()));
        add("for", c -> {
            Element body = c.pop();
            for (Element e : (Collection)c.pop()) {
                c.push(e);
                c.execute(body);
            }
        });
        add("stack", c -> output(c.toString()));
        add("print", c -> output("" + c.pop()));
        add("println", c -> output(c.pop() + System.lineSeparator()));
        
        add("array", c -> c.push(Array.of(((Int)c.pop()).value)));
        add("at", c -> { Collection x = (Collection)c.pop(); int i = ((Int)c.pop()).value; c.push(x.at(i)); });
        add("put", c -> { Collection x = (Collection)c.pop(); Element e = c.pop(), i = c.pop(); x.put(i, e); });
        add("size", c -> { Collection x = (Collection)c.pop(); c.push(i(x.size())); });
        add("L-A", c -> {
            List list = (List)c.pop();
            int length = list.size();
            Array array = Array.of(length);
            int i = 1;
            for (Element e : list)
                array.put(i(i++), e);
            c.push(array);
        });
        add("A-L", c -> c.push(List.of(((Array)c.pop()).array)));
        add("generator", c -> c.push(Generator.of(c.child(), (Collection)c.pop())));
        add("yield", YIELD);
        add("range", c -> {
            int step = i(c.pop()), end = i(c.pop()), start = i(c.pop());
            c.push(Range.of(start, end, step));
        });
    }

}
