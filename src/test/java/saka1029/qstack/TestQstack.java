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
//        c.run("/append (swap dup () == (drop) (unpair rot append pair) if) define");
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'() '(1 2 3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1) '(2 3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2) '(3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2 3) '(4) append"));
        
    }

}
