package saka1029.qstack;

import static org.junit.Assert.assertEquals;

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
        c.execute(Quote.of(Symbol.of("drop")));
        assertEquals(Symbol.of("drop"), c.pop());
    }
    
    @Test
    public void testDup() {
        Context c = Context.of(10);
        c.execute(Int.ONE);
        c.execute("dup");
        assertEquals(2, c.sp);
        assertEquals(Int.ONE, c.pop());
        assertEquals(Int.ONE, c.pop());
    }
    
    @Test
    public void testDrop() {
        Context c = Context.of(10);
        c.execute(Int.ONE);
        c.execute("drop");
        assertEquals(0, c.sp);
    }
    
    @Test
    public void testExecute() {
        Context c = Context.of(10);
        c.execute(Quote.of(List.of(Int.ONE, Int.TWO)));
        c.execute("execute");
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
        c.execute("if");
        assertEquals(1, c.sp);
        assertEquals(Int.ONE, c.pop());
        c.execute(Bool.FALSE);
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute("if");
        assertEquals(1, c.sp);
        assertEquals(Int.TWO, c.pop());
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Bool.TRUE);
        c.execute(Quote.of(Symbol.of("+")));
        c.execute(Quote.of(Symbol.of("-")));
        c.execute("if");
        assertEquals(1, c.sp);
        assertEquals(Int.THREE, c.pop());
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Bool.FALSE);
        c.execute(Quote.of(Symbol.of("+")));
        c.execute(Quote.of(Symbol.of("-")));
        c.execute("if");
        assertEquals(1, c.sp);
        assertEquals(Int.of(-1), c.pop());
    }
    
    @Test
    public void testEval() {
        Context c = Context.of(10);
        assertEquals(Int.ONE, c.eval(" 1 "));
        assertEquals(Int.of(8), c.eval(" 1 3 + 2 * "));
        assertEquals(Int.of(-1), c.eval(" (1  2 -) "));
        assertEquals(Int.of(-1), c.eval(" (1  (1 1 +) -) "));
        assertEquals(Symbol.of("abc"), c.eval(" 'abc "));
        assertEquals(List.of(Symbol.of("a"), Int.ONE), c.eval(" '(a 1) "));
    }
}
