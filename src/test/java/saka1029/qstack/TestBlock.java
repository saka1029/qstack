package saka1029.qstack;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class TestBlock {

    @Test
    public void testRead() {
        Context c = Context.of(10);
        Element e = c.eval("'(2 1 : A2 A1 +)");
        assertTrue(e instanceof Block);
        assertEquals("(2 1 : A2 A1 +)", e.toString());
        Block b = (Block)e;
        assertEquals(3, b.length());
        assertEquals(2, b.args);
        assertEquals(1, b.returns);
        Iterator<Element> it = b.iterator();
        assertEquals(Symbol.of("A2"), it.next());
        assertEquals(Symbol.of("A1"), it.next());
        assertEquals(Symbol.of("+"), it.next());
        assertFalse(it.hasNext());
    }
    
    @Test
    public void testStore() {
        StringBuilder sb = new StringBuilder();
        Context c = Context.of(10).output(s -> sb.append(s).append(System.lineSeparator()));
        assertEquals(Int.THREE, c.eval("2 (1 1 : 0 A1 1 + stack S1 stack L1)"));
        assertEquals(// A1=2, old fp= 0 self=(1 1 : ...) L1=0
            ("[2 0 (1 1 : 0 A1 1 + stack S1 stack L1) 0 3]%n"
            + "[2 0 (1 1 : 0 A1 1 + stack S1 stack L1) 3]%n").formatted(), sb.toString());
    }

    @Test
    public void testExecute() {
        Context c = Context.of(25);
        assertEquals(Int.THREE, c.eval("1 2 (2 1 : A2 A1 +)"));
        assertEquals(Int.THREE, c.eval("1 2 +"));
        c.run("'(1 1 : A1 0 <= 1 '(A1 1 - ! A1 *) if) '! define");
        assertEquals(Int.of(1), c.eval("0 !"));
        assertEquals(Int.of(1), c.eval("1 !"));
        assertEquals(Int.of(2), c.eval("2 !"));
        assertEquals(Int.of(6), c.eval("3 !"));
        assertEquals(Int.of(24), c.eval("4 !"));
        assertEquals(Int.of(120), c.eval("5 !"));
    }

    @Test
    public void testSelf() {
        Context c = Context.of(40);
        c.run("'(1 1 : A1 0 <= 1 '(A1 1 - self A1 *) if) '! define");
        assertEquals(Int.of(1), c.eval("0 !"));
        assertEquals(Int.of(1), c.eval("1 !"));
        assertEquals(Int.of(2), c.eval("2 !"));
        assertEquals(Int.of(6), c.eval("3 !"));
        assertEquals(Int.of(24), c.eval("4 !"));
        assertEquals(Int.of(120), c.eval("5 !"));
    }
    
    @Test
    public void testImmediatelyExecuteRecursiveFunction() {
        Context c = Context.of(40);
        assertEquals(Int.of(120), c.eval("5 (1 1 : A1 0 <= 1 '(A1 A1 1 - self *) if)"));
    }

}
