package saka1029.qstack;

public class Block extends Cons {
    
    final int args, returns;

    Block(int args, int returns, Element car, Element cdr) {
        super(car, cdr);
        this.args = args;
        this.returns = returns;
    }

}
