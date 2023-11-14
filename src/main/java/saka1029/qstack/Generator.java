package saka1029.qstack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Generator implements Value, Collection {

    final Collection codes;
    final Context context;
    
    Generator(Context context, Collection codes) {
        this.context = context;
        this.codes = codes;
    }
    public static Generator of(Context context, Collection codes) {
        return new Generator(context, codes);
    }

    class Iter implements Iterator<Element> {

        Iterator<Element> codes = Generator.this.codes.iterator();
        Element e = advance();
        
        Element advance() {
            while (codes.hasNext()) {
                context.execute(codes.next());
                if (context.sp >= 2 && context.peek(0) == Context.YIELD) {
                    context.drop(); // drop YIELD
                    return context.pop();
                }
            }
            return null;
        }
        
        @Override
        public boolean hasNext() {
            return e != null;
        }

        @Override
        public Element next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Element result = e;
            e = advance();
            return result;
        }
    }

    @Override
    public Iterator<Element> iterator() {
        return new Iter();
    }

    @Override
    public Element at(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(Element index, Element e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return "Generator%s".formatted(codes);
    }

}
