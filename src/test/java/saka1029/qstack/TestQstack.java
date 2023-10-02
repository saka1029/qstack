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
        c.run("'(swap @0 null? '(drop) '(uncons rot append cons) if) 'append define");
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
        c.run("'(@0 null? '(drop) '(uncons rot rot swap cons swap reverse2) if)  'reverse2 define");
        c.run("'('() swap reverse2) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }

    /**
     * (1 2 3) reverse
     * (1 2 3) : uncons
     * 1 (2 3) : reverse
     * 1 (3 2) : swap
     * (3 2) 1 : '()
     * (3 2) 1 () : cons
     * (3 2) (1) : append
     * (3 2 1)
     * 
     */
    @Test
    public void testReverseByAppend() {
        Context c = Context.of(10);
        c.run("'(swap @0 null? '(drop) '(uncons rot append cons) if) 'append define");
        c.run("'(@0 null? '() '(uncons reverse swap '() cons append) if) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }
    
    /**
     * (0 1 2) (1 +) map
     * (0 1 2) (1 +) : swap
     * (1 +) (0 1 2)
     * 
     * (1 +) (0 1 2) : uncons
     * (1 +) 0 (1 2) : swap
     * (1 +) (1 2) 0 : @2
     * (1 +) (1 2) 0 (1 +) : execute
     * (1 +) (1 2) 1 : swap
     * (1 +) 1 (1 2) : @2
     * (1 +) 1 (1 2) (1 +) : map
     * (1 +) 1 (2 3) : cons
     * (1 +) (1 2 3) : ^1
     * (1 2 3)
     * 
     */
    @Test
    public void testMapRecursive() {
        Context c = Context.of(20);
        c.run("'(swap @0 null? '() '(uncons swap @2 execute swap @2 map cons) if ^1) 'map define");
        assertEquals(c.eval("'()"), c.eval("'() '(1 +) map"));
        assertEquals(c.eval("'(1)"), c.eval("'(0) '(1 +) map"));
        assertEquals(c.eval("'(1 2 3)"), c.eval("'(0 1 2) '(1 +) map"));
        assertEquals(c.eval("'(1 2 3 4 5)"), c.eval("'(0 1 2 3 4) '(1 +) map"));
    }

}
