package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNamedFrame {

    @Test
    public void testRead() {
        Context c = Context.of(8);
        assertEquals(c.eval("'(1 1 : A1 A1 +)"), c.eval("'(F 1 n : n n +)"));
        assertEquals(c.eval("'(1 1 : (1 1 : A11 A1 +))"), c.eval("'(F 1 a : (F 1 n : a n +))"));
    }
    
    @Test
    public void testFact() {
        Context c = Context.of(80);
        c.run("'(F 1 n : n 0 <= 1 '(n 1 - self n *) if) 'fact define");
        assertEquals(Int.of(1), c.eval("0 fact"));
        assertEquals(Int.of(1), c.eval("1 fact"));
        assertEquals(Int.of(2), c.eval("2 fact"));
        assertEquals(Int.of(6), c.eval("3 fact"));
        assertEquals(Int.of(24), c.eval("4 fact"));
        assertEquals(Int.of(120), c.eval("5 fact"));
    }

}
