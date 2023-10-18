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
    
    /**
     * Blockをexecuteする場合は以下の処理を行います。
     * (1) fpをセーブする。
     * (2) Block内の要素を順次executeする。
     * (3) spを引数(args個ある)を除いた位置までもどす。
     * (4) スタックトップにあったreturns個の要素をスタックにプッシュする。
     */
    @Override
    public void execute(Context context) {
        context.push(Int.of(context.fp));   // save fp
        context.fp = context.sp - 1;        // set new fp
        context.push(this);                 // push self
        for (Element e : this)
            context.execute(e);
        int end = context.sp, start = end - returns; // 戻り値の先頭と末尾
        context.sp = context.fp - args; // restore frame (unwind)
        for (int i = start; i < end; ++i)
            context.push(context.stack[i]); // push return values
        context.fp = ((Int)context.stack[context.fp]).value; // restore fp
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(args).append(" ").append(returns).append(" : ");
        sb.append(car);
        Element e = cdr;
        for (; e instanceof Cons p; e = p.cdr)
            sb.append(" ").append(p.car);
        if (e != NIL)
            sb.append(" . ").append(e);
        return sb.append(")").toString();
    }
}
