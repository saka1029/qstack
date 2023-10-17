package saka1029.qstack;

public class Block extends Cons {
    
    final int args, returns;

    Block(int args, int returns, Element car, Element cdr) {
        super(car, cdr);
        this.args = args;
        this.returns = returns;
    }
    
    public static Block of(int args, int returns, Element car, Element cdr) {
        return new Block(args, returns, car, cdr);
    }
    
    @Override
    public void execute(Context context) {
        context.push(Int.of(context.fp));   // save fp
        context.fp = context.sp - 1;        // set new fp
        for (Element e : this)
            context.execute(e);
        int end = context.sp, start = end - returns; // 戻り値の先頭と末尾
        context.sp = context.fp - args; // restore frame (unwind)
        for (int i = start; i < end; ++i)
            context.push(context.stack[i]); // push return values
        context.fp = ((Int)context.stack[context.fp]).value; // restore fp
    }
}
