package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestNamedFrame {

    /**
     * f(n) {
     *     return n + n
     * }
     */
    @Test
    public void testNamedFrame() {
        Context c = Context.of(20);
        assertEquals(c.eval("'(1 1 : A1 A1 +)"), c.eval("'(frame 1 (n) n n +)"));
    }
    
    /**
     * f(a) {
     *     g(n) {
     *         return a + n
     *     }
     *     return g(4)
     * }
     */
    @Test
    public void testNestedFunction() {
        Context c = Context.of(20);
        assertEquals(c.eval("'(1 1 : '(1 1 : A11 A1 +))"), c.eval("'(frame 1 (a) '(frame 1 (n) a n +))"));
        assertEquals(c.eval("'(1 1 : '(1 1 : a A1 +) 4 L2 execute)"), c.eval("'(frame 1 (a : '(frame 1 (n) a n +) g) 4 g execute)"));
        c.run("'(frame 1 (a : '(frame 1 (n) a n +) g) 4 g execute) 'nested-function define");
        assertEquals(Int.of(7), c.eval("3 nested-function"));
        assertEquals(Int.of(8), c.eval("4 nested-function"));
    }
    
    @Test
    public void testFact() {
        Context c = Context.of(80);
        c.run("'(frame 1 (n) n 0 <= 1 '(n 1 - self n *) if) 'fact define");
        assertEquals(Int.of(1), c.eval("0 fact"));
        assertEquals(Int.of(1), c.eval("1 fact"));
        assertEquals(Int.of(2), c.eval("2 fact"));
        assertEquals(Int.of(6), c.eval("3 fact"));
        assertEquals(Int.of(24), c.eval("4 fact"));
        assertEquals(Int.of(120), c.eval("5 fact"));
    }

}
