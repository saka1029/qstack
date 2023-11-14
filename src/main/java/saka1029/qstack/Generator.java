package saka1029.qstack;

import java.util.Iterator;

public class Generator implements Value, Collection {

    final Collection codes;
    final Context context;
    
    Generator(Context context, Collection codes) {
        this.context = context;
        this.codes = codes;
    }

    @Override
    public Iterator<Element> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Element at(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(Element index, Element e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

}
