package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestParser {

    @Test
    public void testInt() {
        Parser p = Parser.of("  1 -23   43  ");
        assertEquals(Int.of(1), p.read());
        assertEquals(Int.of(-23), p.read());
        assertEquals(Int.of(43), p.read());
        assertNull(p.read());
    }

    @Test
    public void testSymbol() {
        Parser p = Parser.of("  a .a   ..a a. a.b ");
        assertEquals(Symbol.of("a"), p.read());
        assertEquals(Symbol.of(".a"), p.read());
        assertEquals(Symbol.of("..a"), p.read());
        assertEquals(Symbol.of("a."), p.read());
        assertEquals(Symbol.of("a.b"), p.read());
        assertNull(p.read());
    }

    @Test
    public void testList() {
        Parser p = Parser.of("  (a -3 b) () ");
        assertEquals(List.of(Symbol.of("a"), Int.of(-3), Symbol.of("b")), p.read());
        assertEquals(List.NIL, p.read());
        assertNull(p.read());
    }

    @Test
    public void testQuote() {
        Parser p = Parser.of("  '(a -3 b) '() ");
        assertEquals(Quote.of(List.of(Symbol.of("a"), Int.of(-3), Symbol.of("b"))), p.read());
        assertEquals(Quote.of(List.NIL), p.read());
        assertNull(p.read());
    }
    
    @Test
    public void testRP() {
        Parser p = Parser.of(" )  ");
        try {
            p.read();
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected ')'", e.getMessage());
        }
    }
    
    @Test
    public void testDot() {
        Parser p = Parser.of(" .  ");
        try {
            p.read();
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected '.'", e.getMessage());
        }
    }

}
