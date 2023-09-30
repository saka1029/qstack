package saka1029.qstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.junit.Test;

public class TestReader {
    
    @Test
    public void testRead() {
        Context c = Context.of(10);
        Reader reader = Reader.of(c, new StringReader("  1 2 "));
        assertEquals(Int.ONE, reader.read());
        assertEquals(Int.TWO, reader.read());
        assertNull(reader.read());
    }

}
