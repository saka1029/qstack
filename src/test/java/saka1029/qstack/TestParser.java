package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestParser {

    @Test
    public void testInt() {
        Parser p = Parser.of("  1 -23   43  ");
        assertEquals(Int.of(1), p.read(null));
        assertEquals(Int.of(-23), p.read(null));
        assertEquals(Int.of(43), p.read(null));
        assertNull(p.read(null));
    }

    @Test
    public void testSymbol() {
        Parser p = Parser.of("  a .a   ..a a. a.b ");
        assertEquals(Symbol.of("a"), p.read(null));
        assertEquals(Symbol.of(".a"), p.read(null));
        assertEquals(Symbol.of("..a"), p.read(null));
        assertEquals(Symbol.of("a."), p.read(null));
        assertEquals(Symbol.of("a.b"), p.read(null));
        assertNull(p.read(null));
    }

    @Test
    public void testList() {
        Parser p = Parser.of("  (a -3 b) () ");
        assertEquals(List.of(Symbol.of("a"), Int.of(-3), Symbol.of("b")), p.read(null));
        assertEquals(List.NIL, p.read(null));
        assertNull(p.read(null));
    }

    @Test
    public void testListDot() {
        Parser p = Parser.of("  (a -3 . c) (2 .(a))");
        assertEquals(List.of(java.util.List.of(Symbol.of("a"), Int.of(-3)), Symbol.of("c")), p.read(null));
        assertEquals(List.of(Int.of(2), Symbol.of("a")), p.read(null));
        assertNull(p.read(null));
    }

    @Test
    public void testQuote() {
        Parser p = Parser.of("  '(a -3 b) '() ");
        assertEquals(Quote.of(List.of(Symbol.of("a"), Int.of(-3), Symbol.of("b"))), p.read(null));
        assertEquals(Quote.of(List.NIL), p.read(null));
        assertNull(p.read(null));
    }
    
    @Test
    public void testRP() {
        Parser p = Parser.of(" )  ");
        try {
            p.read(null);
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected ')'", e.getMessage());
        }
    }
    
    @Test
    public void testDot() {
        Parser p = Parser.of(" .  ");
        try {
            p.read(null);
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected '.'", e.getMessage());
        }
    }
    
    @Test
    public void testListDotIllegal() {
        Parser p = Parser.of(" (a . b c)  ");
        try {
            p.read(null);
            fail();
        } catch (RuntimeException e) {
            assertEquals("')' expected", e.getMessage());
        }
    }
    
    @Test
    public void testCast() {
        Element e = Int.of(0);
        if (e instanceof Int i) {
            e = Int.of(2);
            assertEquals(Int.of(0), i);
        }
    }

}
