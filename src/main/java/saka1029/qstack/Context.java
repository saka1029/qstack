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

    public final Element[] stack;
    public int sp = 0, fp = 0, nest = 0;
    public final Map<Symbol, Element> globals = new HashMap<>();
    public Consumer<String> output = System.out::println, trace = null;

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
    
    public int fp(int nest) {
        int index = fp;
        while (nest-- > 0)
            index = ((Int)stack[index]).value;
        return index;
    }
    
    public void load(int nest, int offset) {
        push(stack[fp(nest) + offset]);
    }
    
    public void store(int nest, int offset) {
        stack[fp(nest) + offset] = pop();
    }

    static final Pattern CLASS_CAST_EXCEPTION = Pattern.compile(
        "class \\S*\\.(\\S+) cannot be cast to class \\S*\\.(\\S+).*");
    
    static RuntimeException error(ClassCastException e) {
        Matcher m = CLASS_CAST_EXCEPTION.matcher(e.getMessage());
        if (m.find())
            return new RuntimeException("Cast error "
                + m.group(1) + " to " + m.group(2), e);
        else
            return new RuntimeException(e);
    }

    public void run(String source) {
        Reader reader = Reader.of(source);
        Element e;
        while ((e = reader.read()) != null)
            try {
                execute(e);
            } catch (ClassCastException ex) {
                throw error(ex);
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
//        add("nil", c -> c.push(List.NIL));
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
        add("set", c -> ((Accessor)c.globals.get(c.pop())).store(c));
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
        add("at", c -> { int i = ((Int)c.pop()).value; c.push(((Array)c.pop()).get(i)); });
        add("put", c -> { Element e = c.pop(); int i = ((Int)c.pop()).value; ((Array)c.pop()).set(i, e); });
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
        add("A1", Accessor.of(0, -1));
        add("A2", Accessor.of(0, -2));
        add("A3", Accessor.of(0, -3));
        add("A4", Accessor.of(0, -4));
        add("A5", Accessor.of(0, -5));
        add("A6", Accessor.of(0, -6));
        // Frameのnest引数アクセス
        add("A11", Accessor.of(1, -1));
        add("A21", Accessor.of(1, -2));
        add("A31", Accessor.of(1, -3));
        add("A41", Accessor.of(1, -4));
        add("A51", Accessor.of(1, -5));
        add("A61", Accessor.of(1, -6));
        // Frameのローカル変数参照
        add("L1", Accessor.of(0, 2));
        add("L2", Accessor.of(0, 3));
        add("L3", Accessor.of(0, 4));
//        // Frameのローカル変数更新 -> S1の代わりに'L1 setを使う。
//        add("S1", c -> c.store(0, 2));
//        add("S2", c -> c.store(0, 3));
//        add("S3", c -> c.store(0, 4));
        // Frameのnest1ローカル変数参照
        add("L11", Accessor.of(1, 2));
        add("L21", Accessor.of(1, 3));
        add("L31", Accessor.of(1, 4));
//        // Frameのnest1ローカル変数更新
//        // Frameのローカル変数更新 -> S11の代わりに'L11 setを使う。
//        add("S11", c -> c.store(1, 2));
//        add("S21", c -> c.store(1, 3));
//        add("S31", c -> c.store(1, 4));
        // Frameのローカル手続き実行
        // 'X1はローカル手続きをスタックにpushするが、
        // その手続きはfp相対で定義されたものであり、
        // executeされる場所によっては正しく動作しない。
        // ローカル手続きをスタックにpushするのであれば'X1ではなくL1を使用すべきである。
        // 紛らわしいのでX1をやめてL1 executeを使用すべき。
//        add("X1", c -> c.execute(stack[c.fp + 2]));
//        add("X2", c -> c.execute(stack[c.fp + 3]));
//        add("X3", c -> c.execute(stack[c.fp + 4]));
        add("self", c -> c.execute(stack[c.fp + 1]));
    }

}
