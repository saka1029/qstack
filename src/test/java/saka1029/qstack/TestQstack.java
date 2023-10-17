package saka1029.qstack;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.junit.Test;

import saka1029.Common;

public class TestQstack {

    static final Logger logger = Common.logger(TestQstack.class);
        
    @Test
    public void testFactRecursive() {
        Context c = Context.of(9);
        c.run("'(@0 0 <= '(1 ^1) '(@0 1 - ! *) if) '! define");
        assertEquals(c.eval("1"), c.eval("0 !"));
        assertEquals(c.eval("1"), c.eval("1 !"));
        assertEquals(c.eval("2"), c.eval("2 !"));
        assertEquals(c.eval("6"), c.eval("3 !"));
        assertEquals(c.eval("24"), c.eval("4 !"));
        assertEquals(c.eval("120"), c.eval("5 !"));
    }

    @Test
    public void testFactFrame() {
        Context c = Context.of(15);//.trace(logger::info);
        c.run("'(1 1 : A1 0 <= 1 '(A1 1 - ! A1 *) if) '! define");
        assertEquals(Int.of(1), c.eval("0 !"));
        assertEquals(Int.of(1), c.eval("1 !"));
        assertEquals(Int.of(2), c.eval("2 !"));
        assertEquals(Int.of(6), c.eval("3 !"));
        assertEquals(Int.of(24), c.eval("4 !"));
        assertEquals(Int.of(120), c.eval("5 !"));
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

    @Test
    public void testFactFrameByFor() {
        Context c = Context.of(10);//.trace(logger::info);
        c.run("'(1 1 : 1 1 A1 1 '* for) '! define");
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
        c.run("'(swap @0 null? 'drop '(uncons rot append cons) if) 'my-append define");
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'() '(1 2 3 4) my-append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1) '(2 3 4) my-append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2) '(3 4) my-append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2 3) '(4) my-append"));
    }
    
    @Test
    public void testAppendFrame() {
        Context c = Context.of(10);
        c.run("'(2 1 : A2 null? 'A1 '(A2 uncons A1 append cons) if) 'my-append define");
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'() '(1 2 3 4) my-append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1) '(2 3 4) my-append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2) '(3 4) my-append"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(1 2 3) '(4) my-append"));
    }
    
    @Test
    public void testReverseByForeach() {
        Context c = Context.of(10);
        c.run("'('() swap 'rcons foreach) 'my-reverse define");
        assertEquals(c.eval("'()"), c.eval("'() my-reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) my-reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) my-reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) my-reverse"));
    }
    
    @Test
    public void testReverseFrameByForeach() {
        Context c = Context.of(10);
        c.run("'(1 1 : '() A1 'rcons foreach) 'my-reverse define");
        assertEquals(c.eval("'()"), c.eval("'() my-reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) my-reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) my-reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) my-reverse"));
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
        c.run("'(@0 null? 'drop '(uncons rrot rcons swap reverse2) if)  'reverse2 define");
        c.run("'('() swap reverse2) 'my-reverse define");
        assertEquals(c.eval("'()"), c.eval("'() my-reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) my-reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) my-reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) my-reverse"));
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
//        c.run("'(swap @0 null? '(drop) '(uncons rot append cons) if) 'append define");
        c.run("'(@0 null? '() '(uncons reverse swap '() cons append) if) 'my-reverse define");
        assertEquals(c.eval("'()"), c.eval("'() my-reverse"));
        assertEquals(c.eval("'(1)"), c.eval("'(1) my-reverse"));
        assertEquals(c.eval("'(2 1)"), c.eval("'(1 2) my-reverse"));
        assertEquals(c.eval("'(4 3 2 1)"), c.eval("'(1 2 3 4) my-reverse"));
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
        c.run("'(swap @0 null? '() '(uncons @2 filter swap @0 @3 execute 'rcons 'drop if) if ^1) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    /**
     * リストの先頭からフィルターする。
     * '(0 1 2 3) '(2 % 0 ==) filter
     * (0 1 2 3) (2 % 0 ==) : swap
     * 
     * (2 % 0 ==) (0 1 2 3) : uncons
     * (2 % 0 ==) 0 (1 2 3) : swap
     * (2 % 0 ==) (1 2 3) 0 : @0
     * (2 % 0 ==) (1 2 3) 0 0 : @3
     * (2 % 0 ==) (1 2 3) 0 0 (2 % 0 ==) : execute
     * (2 % 0 ==) (1 2 3) 0 true : rot
     * (2 % 0 ==) 0 true (1 2 3) : @3
     * (2 % 0 ==) 0 true (1 2 3) (2 % 0 ==) : filter
     * (2 % 0 ==) 0 true (2) : swap
     * (2 % 0 ==) 0 (2) true : 'cons '@1 if
     * (2 % 0 ==) (0 2) : ^1
     * (0 2) : ^1
     */
    @Test
    public void testFilterRecursiveFromFirst() {
        Context c = Context.of(20);
        c.run("'(swap @0 null? '() '(uncons swap @0 @3 execute rot @3 filter swap 'cons '^1 if) if ^1) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    @Test
    public void testFilterByForeachAndReverse() {
        Context c = Context.of(20);
        c.run("'(swap '() swap '(@0 @3 execute 'rcons 'drop if) foreach ^1 reverse) 'filter define");
        assertEquals(c.eval("'(0 2)"), c.eval("'(0 1 2 3) '(2 % 0 ==) filter"));
        assertEquals(c.eval("'(1 3)"), c.eval("'(0 1 2 3) '(2 % 0 !=) filter"));
    }
    
    /**
     * filter-predicateは述語(E->B)をフィルター用の述語に変換する高階関数。
     * pred filter-predicate -> (@0 pred execute 'rcons 'drop if)
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
//        c.run("'('() swap 'rcons foreach) 'reverse define");
        c.run("'('(execute 'rcons 'drop if) cons '@0 rcons) 'filter-predicate define");
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
        c.run("'('() swap 1 -1 'rcons for) 'iota define");
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
    
    @Test
    public void testQuickSort() {
        Context c = Context.of(32);
        c.run("'(swap @0 null?"
            + " '()"
            + " '(uncons swap @0 @3 execute rot @3 filter swap 'cons '^1 if)"
            + " if ^1) 'filter define");
        c.run("'(@0 null?"
            + " '()"
            + " '(uncons"
            + "   @0 @2 '(<=) cons filter qsort"
            + "   swap @2 '(>) cons filter qsort"
            + "   @2 rcons append ^1) if) 'qsort define");
        assertEquals(c.eval("'()"), c.eval("'() qsort"));
        assertEquals(c.eval("'(1 2 3 4)"), c.eval("'(3 2 4 1) qsort"));
        assertEquals(c.eval("'(1 2 3 4 5 6 7 8 9)"), c.eval("'(6 3 9 5 2 4 7 8 1) qsort"));
    }
    
    @Test
    public void testSieveOfEratosthenes() {
        Context c = Context.of(6).output(logger::info);
        c.run("'(@0 2 * @2 size rot '(@1 swap true set) for) 'sieve-of-eratosthenes define");
        c.run("'('() 2 @2 size 1 '(@0 @3 swap get 'drop 'rcons if) for reverse ^1) 'array-to-list define");
        c.run("'(@0 array swap 2 swap 1 '(sieve-of-eratosthenes) for array-to-list) 'primes define");
        assertEquals(c.eval("'(2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97)"),
            c.eval("100 primes"));
    }
    
    @Test
    public void testHelloWorld() {
        StringBuilder sb = new StringBuilder();
        Context c = Context.of(6).output(sb::append);
        c.run("\"Hello World\\r\\n\" print");
        assertEquals("Hello World\r\n", sb.toString());
    }
    
    /**
     * <pre>
     * Scheme:
     * (define (remove x ls)
     *   (cond
     *    ((null? ls) '())
     *    ((equal? (car ls) x)
     *     (remove x (cdr ls)))
     *    (else
     *     (cons (car ls) (remove x (cdr ls))))))
     * </pre>
     */
    @Test
    public void testRemove() {
        Context c = Context.of(20).output(logger::info);
        // schemeでは(remove x (cdr ls))の呼び出しが2か所にあるが、
        // これはひとつにまとめることができる。
        // (1) cdrをremoveする。
        // (2) carとxが等しい場合はcarを捨てる(^1)。等しくない場合はconsする。
        c.run("'(@0 null? '^1 '(uncons @2 swap remove rot @2 == '^1 'cons if) if) 'remove define");
        assertEquals(c.eval("'()"), c.eval("1 '() remove"));
        assertEquals(c.eval("'()"), c.eval("1 '(1) remove"));
        assertEquals(c.eval("'(2)"), c.eval("1 '(1 2) remove"));
        assertEquals(c.eval("'(2 3)"), c.eval("1 '(1 2 3) remove"));
        assertEquals(c.eval("'(1 3)"), c.eval("2 '(1 2 3) remove"));
        assertEquals(c.eval("'(1 2)"), c.eval("3 '(1 2 3) remove"));
        assertEquals(c.eval("'(1 2 3)"), c.eval("4 '(1 2 3) remove"));
    }
    
    /**
     * <pre>
     * scheme:
     * (define (permutations func ls)
     *   (define (perm ls a)
     *     (if (null? ls)
     *         (func (reverse a))
     *         (for-each
     *           (lambda (n)
     *             (perm (remove n ls) (cons n a)))
     *           ls)))
     *   (perm ls '()))
     * </pre>
     * (1 2) () perm
     * (1 2) () : @1
     * (1 2) () (1 2) : (...) foreach
     * (1 2) () 1 : @0
     * (1 2) () 1 1 : @3
     * (1 2) () 1 1 (1 2) : remove
     * (1 2) () 1 (2) : swap
     * (1 2) () (2) 1 : @2
     * (1 2) () (2) 1 () : cons
     * (1 2) () (2) (1) : perm
     */
    @Test
    public void testPermutations() {
        StringBuilder sb = new StringBuilder();
        Context c = Context.of(50).output(sb::append); // .trace(logger::info);
        c.run("'(@0 null? '^1 '(uncons @2 swap remove rot @2 == '^1 'cons if) if) 'remove define");
        c.run("'(@1 null? '(@0 reverse print) '(@1 '(@0 @3 remove swap @2 cons perm) foreach) if drop2) 'perm define");
        c.run("'('() perm) 'permutations define");
        sb.setLength(0);
        c.run("'() permutations");
        assertEquals("()", sb.toString());
        sb.setLength(0);
        c.run("'(1) permutations");
        assertEquals("(1)", sb.toString());
        sb.setLength(0);
        c.run("'(1 2 3) permutations");
        assertEquals("(1 2 3)(1 3 2)(2 1 3)(2 3 1)(3 1 2)(3 2 1)", sb.toString());
        sb.setLength(0);
        c.run("'(1 2 3 4) permutations");
        assertEquals("(1 2 3 4)(1 2 4 3)(1 3 2 4)(1 3 4 2)(1 4 2 3)(1 4 3 2)"
                   + "(2 1 3 4)(2 1 4 3)(2 3 1 4)(2 3 4 1)(2 4 1 3)(2 4 3 1)"
                   + "(3 1 2 4)(3 1 4 2)(3 2 1 4)(3 2 4 1)(3 4 1 2)(3 4 2 1)"
                   + "(4 1 2 3)(4 1 3 2)(4 2 1 3)(4 2 3 1)(4 3 1 2)(4 3 2 1)", sb.toString());
    }
}
