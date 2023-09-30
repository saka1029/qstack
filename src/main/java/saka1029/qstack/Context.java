package saka1029.qstack;

public class Context {
    
    public final Element[] stack;
    public int sp = 0;
    
    Context(int stackSize) {
        this.stack = new Element[stackSize];
    }
    
    public static Context of(int stackSize) {
        return new Context(stackSize);
    }
    
    public void execute(Element e) {
        e.execute(this);
    }
    
    public void push(Element e) {
        stack[sp++] = e;
    }
    
    public Element pop() {
        return stack[--sp];
    }
    
    public void dup(int index) {
        push(stack[sp - index - 1]);
    }

}
