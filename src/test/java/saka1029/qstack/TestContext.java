package saka1029.qstack;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Test;

import saka1029.Common;

public class TestContext {
    
    static final Logger logger = Common.logger(TestContext.class);

    @Test
    public void testEQ() {
        Context c = Context.of(10);
        assertEquals(List.NIL, c.eval("'()"));
        assertEquals(c.eval("'()"), c.eval("'()"));
    }

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
        c.execute("@0");
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
    public void testRot() {
        Context c = Context.of(10);
        c.execute(Int.ONE);
        c.execute(Int.TWO);
        c.execute(Int.THREE);
        c.execute("rot");
        assertEquals(3, c.sp);
        assertEquals(Int.ONE, c.pop());
        assertEquals(Int.THREE, c.pop());
        assertEquals(Int.TWO, c.pop());
    }
    
    @Test
    public void testExit() {
        Context c = Context.of(10);
        c.run("1 2 3 4");
        c.execute("^3");
        assertEquals(1, c.sp);
        assertEquals(Int.of(4), c.pop());
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
        assertEquals(Int.of(1), c.eval("true 1  2 if"));
        assertEquals(Int.of(2), c.eval("false 1  2 if"));
        assertEquals(Int.of(1), c.eval("true '1  '2 if"));
        assertEquals(Int.of(1), c.eval("1 2 < '(0 1 +) '(1 2 +) if"));
        assertEquals(Int.of(3), c.eval("2 1 < '(0 1 +) '(1 2 +) if"));
    }
    
    @Test
    public void testDefine() {
        Context c = Context.of(10);
        c.run("3 'みっつ define");
        assertEquals(Int.THREE, c.eval("みっつ"));
        c.run("'(1 +) 'inc define");
        assertEquals(Int.of(4), c.eval("3 inc"));
    }
    
    @Test
    public void testPrint() {
        Context c = Context.of(10).output(System.out::print).trace(logger::info);
        c.run("1 2 3 '𩸽 stack println print stack");
    }
    
    @Test
    public void testArray() {
        Context c = Context.of(5).output(logger::info);
        c.run("5 array 0 4 1 '(@1 swap @0 set) for");
        assertEquals(c.eval("5"), c.eval("@0 size"));
        assertEquals(c.eval("0"), c.eval("@0 0 get"));
        assertEquals(c.eval("1"), c.eval("@0 1 get"));
        assertEquals(c.eval("2"), c.eval("@0 2 get"));
        assertEquals(c.eval("3"), c.eval("@0 3 get"));
        assertEquals(c.eval("4"), c.eval("@0 4 get"));
    }
}
