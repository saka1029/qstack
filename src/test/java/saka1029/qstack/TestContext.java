package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestContext {

    @Test
    public void testPush() {
        Context c = Context.of(10);
        c.execute(Int.ONE);
        assertEquals(1, c.sp);
        c.execute(Int.TWO);
        assertEquals(2, c.sp);
        assertEquals(Int.TWO, c.pop());
        assertEquals(1, c.sp);
        assertEquals(Int.ONE, c.pop());
        assertEquals(0, c.sp);
    }
    
    @Test
    public void testList() {
        Context c = Context.of(10);
        c.execute(List.of(Int.ONE, Int.TWO));
        assertEquals(2, c.sp);
        assertEquals(Int.TWO, c.pop());
        assertEquals(Int.ONE, c.pop());
    }
    
    @Test
    public void testQuote() {
        Context c = Context.of(10);
        c.execute(Quote.of(List.of(Int.ONE, Int.TWO)));
        assertEquals(1, c.sp);
        assertEquals(List.of(Int.ONE, Int.TWO), c.pop());
        c.execute(Quote.of(Context.DROP));
        assertEquals(Context.DROP, c.pop());
    }
    
    @Test
    public void testDup() {
        Context c = Context.of(10);
        c.execute(Int.ONE);
        c.execute(Context.DUP);
        assertEquals(2, c.sp);
        assertEquals(Int.ONE, c.pop());
        assertEquals(Int.ONE, c.pop());
    }
    
    @Test
    public void testDrop() {
        Context c = Context.of(10);
        c.execute(Int.ONE);
        c.execute(Context.DROP);
        assertEquals(0, c.sp);
    }
    
    @Test
    public void testExecute() {
        Context c = Context.of(10);
        c.execute(Quote.of(List.of(Int.ONE, Int.TWO)));
        c.execute(Context.EXECUTE);
        assertEquals(2, c.sp);
        assertEquals(Int.TWO, c.pop());
        assertEquals(Int.ONE, c.pop());
    }

    @Test
    public void testIf() {
        Context c = Context.of(10);
        c.execute(Bool.TRUE);
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Context.IF);
        assertEquals(1, c.sp);
        assertEquals(Int.ONE, c.pop());
        c.execute(Bool.FALSE);
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Context.IF);
        assertEquals(1, c.sp);
        assertEquals(Int.TWO, c.pop());
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Bool.TRUE);
        c.execute(Quote.of(Context.PLUS));
        c.execute(Quote.of(Context.MINUS));
        c.execute(Context.IF);
        assertEquals(1, c.sp);
        assertEquals(Int.THREE, c.pop());
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Bool.FALSE);
        c.execute(Quote.of(Context.PLUS));
        c.execute(Quote.of(Context.MINUS));
        c.execute(Context.IF);
        assertEquals(1, c.sp);
        assertEquals(Int.of(-1), c.pop());
    }
}
