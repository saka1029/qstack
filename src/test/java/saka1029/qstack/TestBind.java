package saka1029.qstack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestBind {

    @Test
    public void testLoad() {
        Bind a = new Bind(null);
        a.add(Symbol.of("a"));
        a.add(Symbol.of("b"));
        a.add(Symbol.of("c"));
        Bind b = new Bind(a);
        b.add(Symbol.of("x"));
        b.add(Symbol.of("y"));
        b.add(Symbol.of("z"));
        Context c = Context.of(10);
        c.push(Int.of(0));
        c.push(Int.of(1));
        c.push(Int.of(2));
        c.push(Int.of(0));
        c.push(Int.of(3));
        c.push(Int.of(4));
        c.push(Int.of(5));
        c.push(Int.of(3));
        c.fp = c.sp - 1;
        c.execute(b.load(Symbol.of("x"))); assertEquals(Int.of(3), c.pop());
        c.execute(b.load(Symbol.of("y"))); assertEquals(Int.of(4), c.pop());
        c.execute(b.load(Symbol.of("z"))); assertEquals(Int.of(5), c.pop());
        c.execute(b.load(Symbol.of("a"))); assertEquals(Int.of(0), c.pop());
        c.execute(b.load(Symbol.of("b"))); assertEquals(Int.of(1), c.pop());
        c.execute(b.load(Symbol.of("c"))); assertEquals(Int.of(2), c.pop());
    }

    @Test
    public void testStore() {
        Bind a = new Bind(null);
        a.add(Symbol.of("a"));
        a.add(Symbol.of("b"));
        a.add(Symbol.of("c"));
        Bind b = new Bind(a);
        b.add(Symbol.of("x"));
        b.add(Symbol.of("y"));
        b.add(Symbol.of("z"));
        Context c = Context.of(10);
        c.push(Int.of(0));
        c.push(Int.of(1));
        c.push(Int.of(2));
        c.push(Int.of(0));
        c.push(Int.of(3));
        c.push(Int.of(4));
        c.push(Int.of(5));
        c.push(Int.of(3));
        c.fp = c.sp - 1;
        Int.of(30).execute(c);c.execute(b.store(Symbol.of("x"))); assertEquals(Int.of(30), c.stack[4]);
        Int.of(40).execute(c);c.execute(b.store(Symbol.of("y"))); assertEquals(Int.of(40), c.stack[5]);
        Int.of(50).execute(c);c.execute(b.store(Symbol.of("z"))); assertEquals(Int.of(50), c.stack[6]);
        Int.of(11).execute(c);c.execute(b.store(Symbol.of("a"))); assertEquals(Int.of(11), c.stack[0]);
        Int.of(10).execute(c);c.execute(b.store(Symbol.of("b"))); assertEquals(Int.of(10), c.stack[1]);
        Int.of(20).execute(c);c.execute(b.store(Symbol.of("c"))); assertEquals(Int.of(20), c.stack[2]);
    }

}
