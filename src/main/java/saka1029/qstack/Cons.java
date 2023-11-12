package saka1029.qstack;

import java.util.Objects;

public class Cons implements List {
    
    public final Element car;
    public final List cdr;
    
    Cons(Element car, List cdr) {
        this.car = car;
        this.cdr = cdr;
    }
    
    public static Cons of(Element car, List cdr) {
        return new Cons(car, cdr);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(car, cdr);
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Cons p && p.car.equals(car) && p.cdr.equals(cdr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(car);
        Element e = cdr;
        for (; e instanceof Cons p; e = p.cdr)
            sb.append(" ").append(p.car);
        return sb.append(")").toString();
    }
}
