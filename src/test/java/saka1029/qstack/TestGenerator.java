package saka1029.qstack;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import saka1029.Common;

public class TestGenerator {

    static final Logger logger = Common.logger(TestGenerator.class);

    @Test
    public void testGenerator() {
        Context c = Context.of(10);
        assertEquals(Int.of(6), c.eval("0 '(1 yield 2 yield 3 yield) generator '+ for"));
    }

    @Test
    public void testGeneratorFact() {
        Context c = Context.of(10).trace(logger::info);
        c.run("'(1 1 5 1 range '(* @0 yield) for) generator 'print for");
    }
}
