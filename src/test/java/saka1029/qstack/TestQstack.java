package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestQstack {

    @Test
    public void testFactRecursive() {
        Context c = Context.of(10);
        c.run("'(@0 0 <= '(drop 1) '(@0 1 - ! *) if) '! define");
        assertEquals(c.eval("1"), c.eval("0 !"));
        assertEquals(c.eval("1"), c.eval("1 !"));
        assertEquals(c.eval("2"), c.eval("2 !"));
        assertEquals(c.eval("6"), c.eval("3 !"));
        assertEquals(c.eval("24"), c.eval("4 !"));
        assertEquals(c.eval("120"), c.eval("5 !"));
    }

    @Test
    public void testFactByFor() {
        Context c = Context.of(10);
        c.run("'(1 swap 1 swap 1 '* for) '! define");
        assertEquals(c.eval("1"), c.eval("0 !"));
        assertEquals(c.eval("1"), c.eval("1 !"));
        assertEquals(c.eval("2"), c.eval("2 !"));
        assertEquals(c.eval("6"), c.eval("3 !"));
        assertEquals(c.eval("24"), c.eval("4 !"));
        assertEquals(c.eval("120"), c.eval("5 !"));
    }
    
    /**
     * '(1 2) '(3 4) append
     * (1 2) (3 4) : swap
     * (3 4) (1 2) : uncons
     * (3 4) 1 (2) : rot
     * 1 (2) (3 4) : append
     * 1 (2 3 4) : cons
     * (1 2 3 4)
     */
    @Test
    public void testAppend() {
        Context c = Context.of(10);
        c.run("'(swap @0 '() == '(drop) '(uncons rot append cons) if) 'append define");
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'() '(1 2 3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1) '(2 3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2) '(3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2 3) '(4) append"));
    }
    
    @Test
    public void testReverseByForeach() {
        Context c = Context.of(10);
        c.run("'('() swap '(swap cons) foreach) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }
    
    /**
     * () (1 2 3) reverse2
     * () (1 2 3) : uncons
     * () 1 (2 3) : rot
     * 1 (2 3) () : rot
     * (2 3) () 1 : swap
     * (2 3) 1 () : cons
     * (2 3) (1) : swap
     * (1) (2 3) : reverse2
     * (1 2 3)
     */
    @Test
    public void testReverseRecursive() {
        Context c = Context.of(10);
        c.run("'(@0 '() == '(drop) '(uncons rot rot swap cons swap reverse2) if)  'reverse2 define");
        c.run("'('() swap reverse2) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }

}
