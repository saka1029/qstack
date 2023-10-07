package saka1029.qstack;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Test;

import saka1029.Common;

public class TestQstack {

    static final Logger logger = Common.logger(TestQstack.class);
        
    @Test
    public void testFactRecursive() {
        Context c = Context.of(10);
        c.run("'(@0 0 <= '(drop 1) '(@0 1 - ! *) if) '! define");
        assertEquals(c.eval("1"), c.eval("0 !"));
        assertEquals(c.eval("1"), c.eval("1 !"));
        assertEquals(c.eval("2"), c.eval("2 !"));
        assertEquals(c.eval("6"), c.eval("3 !"));
        assertEquals(c.eval("24"), c.eval("4 !"));
        assertEquals(c.eval("120"), c.eval("5 !"));
    }

    @Test
    public void testFactByFor() {
        Context c = Context.of(10);
        c.run("'(1 swap 1 swap 1 '* for) '! define");
        assertEquals(c.eval("1"), c.eval("0 !"));
        assertEquals(c.eval("1"), c.eval("1 !"));
        assertEquals(c.eval("2"), c.eval("2 !"));
        assertEquals(c.eval("6"), c.eval("3 !"));
        assertEquals(c.eval("24"), c.eval("4 !"));
        assertEquals(c.eval("120"), c.eval("5 !"));
    }
    
    /**
     * '(1 2) '(3 4) append
     * (1 2) (3 4) : swap
     * (3 4) (1 2) : uncons
     * (3 4) 1 (2) : rot
     * 1 (2) (3 4) : append
     * 1 (2 3 4) : cons
     * (1 2 3 4)
     */
    @Test
    public void testAppend() {
        Context c = Context.of(10);
        c.run("'(swap @0 null? '(drop) '(uncons rot append cons) if) 'append define");
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'() '(1 2 3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1) '(2 3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2) '(3 4) append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2 3) '(4) append"));
    }
    
    @Test
    public void testReverseByForeach() {
        Context c = Context.of(10);
        c.run("'('() swap '(swap cons) foreach) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }
    
    /**
     * () (1 2 3) reverse2
     * () (1 2 3) : uncons
     * () 1 (2 3) : rrot
     * (2 3) () 1 : swap
     * (2 3) 1 () : cons
     * (2 3) (1) : swap
     * (1) (2 3) : reverse2
     * (1 2 3)
     */
    @Test
    public void testReverseRecursive() {
        Context c = Context.of(10);
        c.run("'(@0 null? '(drop) '(uncons rrot swap cons swap reverse2) if)  'reverse2 define");
        c.run("'('() swap reverse2) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }

    /**
     * (1 2 3) reverse
     * (1 2 3) : uncons
     * 1 (2 3) : reverse
     * 1 (3 2) : swap
     * (3 2) 1 : '()
     * (3 2) 1 () : cons
     * (3 2) (1) : append
     * (3 2 1)
     * 
     */
    @Test
    public void testReverseByAppend() {
        Context c = Context.of(10);
        c.run("'(swap @0 null? '(drop) '(uncons rot append cons) if) 'append define");
        c.run("'(@0 null? '() '(uncons reverse swap '() cons append) if) 'reverse define");
        assertEquals(c.eval("'()"), c.eval("'() reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) reverse"));
    }
    
    /**
     * (0 1 2) (1 +) map
     * (0 1 2) (1 +) : swap
     * (1 +) (0 1 2)
     * 
     * (1 +) (0 1 2) : uncons
     * (1 +) 0 (1 2) : swap
     * (1 +) (1 2) 0 : @2
     * (1 +) (1 2) 0 (1 +) : execute
     * (1 +) (1 2) 1 : swap
     * (1 +) 1 (1 2) : @2
     * (1 +) 1 (1 2) (1 +) : map
     * (1 +) 1 (2 3) : cons
     * (1 +) (1 2 3) : ^1
     * (1 2 3)
     * 
     */
    @Test
    public void testMapRecursive() {
        Context c = Context.of(20);
        c.run("'(swap @0 null? '() '(uncons swap @2 execute swap @2 map cons) if ^1) 'map define");
        assertEquals(c.eval("'()"), c.eval("'() '(1 +) map"));
        assertEquals(c.eval("'(1)"), c.eval("'(0) '(1 +) map"));
        assertEquals(c.eval("'(1 2 3)"), c.eval("'(0 1 2) '(1 +) map"));
        assertEquals(c.eval("'(1 2 3 4 5)"), c.eval("'(0 1 2 3 4) '(1 +) map"));
    }
    
    /**
     * cdr部分の再起を先に実行する。リストの後ろからフィルターする。
     */
    @Test
    public void testFilterRecursiveFromLast() {
        Context c = Context.of(20);
        c.run("'(swap @0 null? '() '(uncons @2 filter swap @0 @3 execute '(swap cons) '(drop) if) if ^1) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    /**
     * リストの先頭からフィルターする。
     */
    @Test
    public void testFilterRecursiveFromFirst() {
        Context c = Context.of(20);
        c.run("'(swap @0 null? '() '(uncons swap @0 @3 execute rot @3 filter swap '(cons) '(swap drop) if) if ^1) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    @Test
    public void testFilterByForeachAndReverse() {
        Context c = Context.of(20);
        c.run("'('() swap '(swap cons) foreach) 'reverse define");
        c.run("'(swap '() swap '(@0 @3 execute '(swap cons) '(drop) if) foreach ^1 reverse) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    /**
     * filter-predicateは述語(E->B)をフィルター用の述語に変換する高階関数。
     * pred filter-predicate -> (@0 pred execute '(swap cons) '(drop) if)
     * (0 1 2 3) filter-predicate : '()
     * (0 1 2 3) filter-predicate '() : rot
     * filter-predicate '() (0 1 2 3) : rot
     * '() (0 1 2 3) filter-predicate : filter
     * 
     * Lispのようにバッククォートを使って
     * `(a ,b c d e)
     * をReaderが
     * 'a b '(c d e) cons cons
     * のように展開する方法も考えられる。
     * ただし、b(unquote)を評価するときのスタックの状態があいまいになってしまう。
     * この例の場合[prev1 prev0 a]のようになっており、bの前に何個要素がpushされているかわからない。
     */
    @Test
    public void testFilterByCompound() {
        Context c = Context.of(20); //.trace(logger::info);
        c.run("'('() swap '(swap cons) foreach) 'reverse define");
        c.run("'('(execute '(swap cons) '(drop) if) cons '@0 swap cons) 'filter-predicate define");
        c.run("'(filter-predicate '() rrot foreach reverse) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    @Test
    public void testCompound() {
        Context c = Context.of(20);
        assertEquals(c.eval("9"), c.eval("3 '@0 '* '() cons cons execute"));
        assertEquals(c.eval("9"), c.eval("3 '(@0) '(*) cons execute"));
        assertEquals(c.eval("9"), c.eval("3 '@0 '(*) cons execute"));
        c.run("'@0 '* '() cons cons 'square define");
        assertEquals(c.eval("9"), c.eval("3 square"));
        c.run("'(@0 list? 'cons '('() cons cons) if) 'compound define");
        assertEquals(c.eval("9"), c.eval("3 '@0 '* compound stack execute"));
        assertEquals(c.eval("9"), c.eval("2 '(1 +) '@0 '* compound compound execute"));
        c.run("'(1 +) 'inc define");
        assertEquals(c.eval("9"), c.eval("2 'inc '@0 '* compound compound execute"));
        assertEquals(c.eval("9"), c.eval("2 'inc '@0 '* compound compound compound execute"));
        c.run("'inc '@0 '* compound compound '1+^2 stack define");
        assertEquals(c.eval("9"), c.eval("2 1+^2 execute"));
    }
    
    /**
     * Joy言語のprimrec
     * 以下は5の階乗120を計算する。
     * 5 1 '* primrec
     */
    @Test
    public void testPrimrec() {
        Context c = Context.of(100); // .trace(logger::info);
        c.run("'(@2 0 <= '(@1 ^3) '(@2 1 - @2 @2 primrec swap rot drop execute) if) 'primrec define");
        assertEquals(c.eval("120"), c.eval("5 1 '* primrec"));
    }
    
    @Test
    public void testIota() {
        Context c = Context.of(10);
        c.run("'('() swap 1 -1 '(swap cons) for) 'iota define");
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("4 iota"));
        c.run("'(1 swap iota '* foreach) '! define");
       assertEquals(c.eval("1"), c.eval("0 !")); 
       assertEquals(c.eval("1"), c.eval("1 !")); 
       assertEquals(c.eval("6"), c.eval("3 !")); 
       assertEquals(c.eval("120"), c.eval("5 !")); 
    }
    
    @Test
    public void testFibonacciByFor() {
        Context c = Context.of(10);
        c.run("'(0 swap 1 swap 1 swap 1 '(drop @1 @1 + rot drop) for drop) 'fibonacci define");
        assertEquals(c.eval("0"), c.eval("0 fibonacci"));
        assertEquals(c.eval("1"), c.eval("1 fibonacci"));
        assertEquals(c.eval("1"), c.eval("2 fibonacci"));
        assertEquals(c.eval("2"), c.eval("3 fibonacci"));
        assertEquals(c.eval("3"), c.eval("4 fibonacci"));
        assertEquals(c.eval("5"), c.eval("5 fibonacci"));
        assertEquals(c.eval("8"), c.eval("6 fibonacci"));
        assertEquals(c.eval("13"), c.eval("7 fibonacci"));
    }
    
    @Test
    public void testFibonacciRecursive() {
        Context c = Context.of(12);
        c.run("'(@0 1 <= '() '(@0 2 - fibonacci swap 1 - fibonacci +) if) 'fibonacci define");
        assertEquals(c.eval("0"), c.eval("0 fibonacci"));
        assertEquals(c.eval("1"), c.eval("1 fibonacci"));
        assertEquals(c.eval("1"), c.eval("2 fibonacci"));
        assertEquals(c.eval("2"), c.eval("3 fibonacci"));
        assertEquals(c.eval("3"), c.eval("4 fibonacci"));
        assertEquals(c.eval("5"), c.eval("5 fibonacci"));
        assertEquals(c.eval("8"), c.eval("6 fibonacci"));
        assertEquals(c.eval("13"), c.eval("7 fibonacci"));
        assertEquals(c.eval("21"), c.eval("8 fibonacci"));
    }
    
    @Test
    public void testAndOrNot() {
        Context c = Context.of(10);
        c.run("'('() '(drop false) if) '& define");
        assertEquals(Bool.TRUE, c.eval("true true &"));
        assertEquals(Bool.FALSE, c.eval("true false &"));
        assertEquals(Bool.FALSE, c.eval("false true &"));
        assertEquals(Bool.FALSE, c.eval("false false &"));
        c.run("'('(drop true) '() if) '| define");
        assertEquals(Bool.TRUE, c.eval("true true |"));
        assertEquals(Bool.TRUE, c.eval("true false |"));
        assertEquals(Bool.TRUE, c.eval("false true |"));
        assertEquals(Bool.FALSE, c.eval("false false |"));
        c.run("'('false 'true if) '! define");
        assertEquals(Bool.FALSE, c.eval("true !"));
        assertEquals(Bool.TRUE, c.eval("false !"));
    }
    
    @Test
    public void testCandCorCnot() {
        Context c = Context.of(10);
        c.run("'(swap execute 'execute '(drop false) if) '&& define");
        assertEquals(Bool.TRUE, c.eval("'(0 0 ==) '(0 0 ==) &&"));
        assertEquals(Bool.FALSE, c.eval("'(0 0 ==) '(0 1 ==) &&"));
        assertEquals(Bool.FALSE, c.eval("'(0 1 ==) '(0 0 ==) &&"));
        assertEquals(Bool.FALSE, c.eval("'(0 1 ==) '(0 1 ==) &&"));
        c.run("'(swap execute '(drop true) 'execute if) '|| define");
        assertEquals(Bool.TRUE, c.eval("'(0 0 ==) '(0 0 ==) ||"));
        assertEquals(Bool.TRUE, c.eval("'(0 0 ==) '(0 1 ==) ||"));
        assertEquals(Bool.TRUE, c.eval("'(0 1 ==) '(0 0 ==) ||"));
        assertEquals(Bool.FALSE, c.eval("'(0 1 ==) '(0 1 ==) ||"));
        c.run("'(execute 'false 'true if) '!! define");
        assertEquals(Bool.FALSE, c.eval("'(0 0 ==) !!"));
        assertEquals(Bool.TRUE, c.eval("'(0 1 ==) !!"));
    }
}
