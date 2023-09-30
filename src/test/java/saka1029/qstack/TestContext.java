package saka1029.qstack;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestContext {

    @Test
    public void testPush() {
        Context c = Context.of(10);
        Int.ONE.execute(c);
        assertEquals(1, c.sp);
        Int.TWO.execute(c);
        assertEquals(2, c.sp);
        assertEquals(Int.TWO, c.pop());
        assertEquals(1, c.sp);
        assertEquals(Int.ONE, c.pop());
        assertEquals(0, c.sp);
    }
    
    @Test
    public void testList() {
        Context c = Context.of(10);
        List.of(Int.ONE, Int.TWO).execute(c);
        assertEquals(2, c.sp);
        assertEquals(Int.TWO, c.pop());
        assertEquals(Int.ONE, c.pop());
    }
    
    @Test
    public void testQuote() {
        Context c = Context.of(10);
        Quote.of(List.of(Int.ONE, Int.TWO)).execute(c);
        assertEquals(1, c.sp);
        assertEquals(List.of(Int.ONE, Int.TWO), c.pop());
    }

}
