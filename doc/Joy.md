# Joy に関する非公式のチュートリアル
マンフレッド・フォン・トゥーン著
2003 年 2 月改訂
この改訂版には、John Cowan (2001) の拡張機能への参照が含まれています。

#要約:
Joy は、引数への関数の適用ではなく、関数の合成に基づいた関数型プログラミング言語です。
式のラムダ抽象化は使用せず、代わりに式の引用符を使用します。
デクォーテーションを実行するには多数のいわゆるコンビネータが使用され、
高次関数の効果があります。
それらのいくつかは、再帰的な定義を排除するために使用できます。
Joy のプログラムはコンパクトで、
多くの場合、後置記法のように見えます。
仮パラメータを実際のパラメータに置き換えることがないため、プログラムの作成とその推論が容易になります。

このチュートリアルでは、すべての実装で同じと考えられる Joy 言語の基本的な機能について説明します。

キーワード:関数型プログラミング、高階関数、関数の合成、コンビネータ、再帰定義の削除、変数自由表記

# 導入
ジョイの理論は興味深いものですが、このチュートリアルの説明では理論を可能な限り避けています。
この文書の残りの部分は次のように構成されています。 
この導入セクションでは、この言語のいくつかの際立った機能の非常に短い概要を続けます。
次の 2 つのセクションでは、基本的なデータ型とその操作について説明します。
その後のセクションは、Joy の中心的な機能であるプログラムの引用とコンビネータでのそれらの使用に戻ります。
定義に関する短いセクションの後、次のセクションではコンビネータ、
特に再帰的定義の必要性を排除できるコンビネータについての説明を再開します。
最後のセクションでは、いくつかの短いプログラムと 1 つの大きなプログラムを使用して、
Joy での集合体を使用したプログラミングを説明します。

2 つの整数、たとえば 2 と 3 を加算し、その合計を書き込むには、次のプログラムを入力します。

```
2 3 +
```

これは通常の後置記法であり、
1920 年代にポーランドの論理学者によって最初に使用された記法を逆にしたものです。
この利点は、複雑な式では括弧が必要ないことです。内部的には次のように動作します。
最初の数字により、整数 2 がスタックにプッシュされます。
2 番目の数字により、その上に整数 3 が追加されます。
次に、加算演算子は 2 つの整数をスタックからポップし、それらの合計 5 をプッシュします。
システムは上記のような入力を読み取り、次のように ピリオド"."で終了するとそれらを実行します。
  
```
        2 3 + .
```

デフォルト モードでは、明示的な出力命令は必要ないため、数値は5出力ファイル (通常は画面) に書き込まれます。
したがって、デフォルト モードでは、終了は"."スタックの最上位要素を書き込む命令であるとみなされます。
整数とは別に、John Cowan によって拡張された現在のバージョンの Joy には実数または「浮動小数点」が含まれています。
浮動小数点数の算術演算は、整数の算術演算とまったく同じです。次の例では 2 つの数値を乗算します

```
        2.34 5.67 *
```

そしてその積13.2678をスタックの一番上に残します。
(したがって、ターミナルで結果を確認するには、上記の行をピリオドで終了する必要があります。)
整数の 2 乗を計算するには、それ自体を乗算する必要があります。2 つの整数の合計の 2 乗を計算するには、合計をそれ自体で乗算する必要があります。できれば、合計を 2 回計算せずにこれを実行する必要があります。以下は、2 と 3 の和の 2 乗を計算するプログラムです。

```
        2 3 + dup *
```

2 と 3 の合計が計算された後、スタックには整数 5 だけが含まれます。
次に、dupオペレーターは 5 の別のコピーをスタックにプッシュします。
次に、乗算演算子は 2 つの整数をその積 (5 の 2 乗) に置き換えます。
その後、2 乗値は 25 として書き出されます。
演算子とは別に、スタックdup の先頭を再配置するための演算子がいくつかあります。
演算子popは最上位の要素を削除し、 swap演算子は、上の 2 つの要素を交換します。
スタックマニピュレータはスタックが存在する場合にのみ意味をなすため、
これは適切な後置表記とはまったく異なります。
このような表記法は、一部の電卓、Unix ユーティリティ dc、写植言語 Postscript、
および汎用言語 Forth でも使用されています。
Billy Tanksley は、これを連結記法と呼ぶことを提案しました。この表記法の理論はそれ自体がトピックですが、このチュートリアルでは扱いません。

整数のリストは角括弧内に記述されます。
整数を追加したり操作したりできるのと同じように、リストもさまざまな方法で操作できます。
以下のconcatは2 つのリストを連結します。

  
```
        [1 2 3] [4 5 6 7] concat
```

2 つのリストが最初にスタックにプッシュされます。
次に、 concatオペレーターはそれらをスタックからポップし、
リスト[1 2 3 4 5 6 7]をスタックにプッシュします。
そこでさらに操作されるか、出力ファイルに書き込まれる可能性があります。
リストの要素はすべて同じ型である必要はなく、要素自体がリストであってもかまいません。
以下では、1 つの整数、2 つの浮動小数点数、および 3 つの整数からなる 1 つのリストを含むリストを連結します。

```
        [ 3.14 42 [1 2 3] 0.003 ] dup concat
```

オペレーターdupはリストのコピーをスタックの最上部にプッシュし、
そこで 2 つのリストが 1 つに連結されます。
Joy はコンビネータ を多用しています。
これらは、スタックの最上位に特定のものを期待するという点で演算子に似ています。
ただし、演算子とは異なり、スタックの最上部で見つけたものを実行します。
これは角括弧で囲まれたプログラムの引用符である必要があります。
mapこれらの 1 つは、関数を介してあるリストの要素を別のリストに ping するためのコンビネータです。
以下のプログラムを検討してみましょう。
  
```
        [1 2 3 4] [dup *] map
```

まず整数のリストをプッシュし、次に引用符で囲まれたプログラムをスタックにプッシュします。
次に、コンビネータmapはリストと引用符を削除し、指定されたリストの各メンバーにプログラムを適用して別のリストを作成します。
結果`[1 4 9 16]`は、スタックの一番上に残るリストです。
新しい関数の定義 では仮パラメータは使用されないため、
仮パラメータを実際のパラメータで置き換えることはありません。
次の定義の後

  
```
        square == dup *
```

記号`square`は`dup *`の代わりに使用できます 。
定義は次のようなブロック内で行われます。

  
```
    DEFINE
        square == dup * ;
	    cube == dup dup * * .
```

例が示すように、定義モードは予約語DEFINEによって開始され、ピリオドまで続きます。
個々の定義はセミコロンで区切られます。
ライブラリでは、の代わりにLIBRAイニシエータがDEFINEの代わりに使用されます。
この文書の残りの部分では、イニシエーター、セパレーター、およびターミネーターは通常、これ以上示されません。
他のプログラミング言語と同様に、定義は再帰的であってもかまいません。
例えば階乗関数の定義です。
Joyでは定義を簡略化するための`primrec`再帰コンビネータがあります。
`primrec`コンビネータは二つの引用されたプログラムを引数に取ります。
整数のパラメータに対しては以下のように動作します。
パラメータがゼロの場合は最初の引用が返されます。
パラメータが正の場合は2番目の引用がひとつ前の結果と結合するために使用されます。
例えば階乗関数は以下のように定義されます。

```
        [1]  [*]  primrec
```

階乗を再帰的に計算します。定義など必要ありません。たとえば、次のプログラムは の階乗を計算します 5。

  
```
        5 [1] [*] primrec
```

まず番号をプッシュし5、次に引用符で囲まれた 2 つの短いプログラムをプッシュします。
この時点で、スタックには 3 つの要素が含まれています。
次に、primrecコンビネータが実行されます。
2 つの引用をスタックからポップし、別の場所に保存します。
次に、 primrecスタックの最上位要素 (最初は5) がゼロに等しいかどうかをテストします。
真の場合、それをポップして最初の引用を実行し、結果としてスタックに[1]を残します。
偽の場合は、最上位要素のデクリメントされたコピーをプッシュし、再帰します。
再帰から戻る途中で、もう1つの引用[*]を使用して、スタック上の階乗になっているものとスタック上の 2 番目の要素を乗算します。
すべてが完了すると、スタックには5の階乗である120が含まれます。
このように再帰的定義の分岐はコンビネータで構成できます。
primrecコンビネータは様々な関数で使用できます。
整数以外のデータ型に対しても使用できます。
Joyはユーザに再帰的な定義を強制することなく様々な計算が行えるようにするための多くのコンビネータを用意しています。
いくつかのコンビネータはprimrecのようにデータ型依存ですが、汎用型のものもあります。

## 整数、浮動小数点、文字、真理値
Joy のデータ型は、単純型と集計型に分類されます。
単純な型は、整数、浮動小数点 (または実数)、文字、および真理値で構成されます。
集計タイプは、セット、文字列、リストで構成されます。
任意の型のリテラルにより、その型の値がスタックにプッシュされます。
そこでは、dup、pop、swapおよびその他のいくつかの一般的なスタック操作によって操作することも、
その型に固有の演算子によって操作することもできます。
このセクションでは、単純型のリテラルと演算子を紹介します。
整数は単なる整数です。このタイプのリテラルは10進数表記で記述されます。
次の二項演算が提供されます。

  
```
+ - * / rem
```

最初の 4 つは従来の意味を持ち、最後のものは除算後の剰余の演算子です。演算子はオペランドの後に記述します。
二項演算子は、スタックの最上位から 2 つの値を削除し、結果で置き換えます。
たとえば、以下のプログラム
  
```
20 3 4 + * 6 - 100 rem
```

は 34 と評価され、この値はスタックの一番上に残ります。
absまた、絶対値を取得する演算子や、 パラメーターが負、ゼロ、正のいずれであるかに応じてsignum生成する演算子など、
整数に固有の単項演算子もいくつかあります。
正と負の整数または整数とは別に、Joy には浮動小数点数または「float」があります。
このタイプのリテラルは、小数点とその後に少なくとも 1 桁の数字を付けて記述されます。
オプションで、最後の数字の後に「E」または「e」を続け、その後に正または負の指数を続けることができます。
ここではいくつかの例を示します。

  
```
3.14 314.0 3.14E5 3.14e-5
```

最後の 2 つは、 314000.0 および 0.0000314 に相当します。整数に対するほとんどの演算子は、浮動小数点に対しても同じように機能します。John Cowan の拡張機能では、float 用の多数の関数も提供されていますが、これらはこのチュートリアルの範囲外です。
文字とは、文字、数字、句読点文字であり、実際には印刷可能な文字、またはいくつかの空白文字の 1 つです。文字型のリテラルは、一重引用符の後に文字自体が続くように記述されます。文字型の値は、小さな数値と非常によく似たものとして扱われます。これは、他の数字を追加できることを意味します。たとえば、文字を大文字から小文字に変更するには 32 を追加します。文字と整数に対して定義されている 2 つの単項演算子があります。pred先行演算子を取得し、succ後続演算子を取得します。例えば、

  
        'A 32 + 成功、成功
'c3 番目の小文字 である と評価されます。
真理値s の型は、一部の言語ではBooleanと呼ばれるものです 。以下に、2 つのリテラル、単項否定演算子、および論理積と論理和のための 2 つの二項演算子を示します。

  
        true false not and or
たとえば、プログラムは

  
        false true false not and not or
と評価されますfalse。
整数型と文字型の値は、次の関係演算子 を使用して比較できます。

  
        = < > != <= >=
演算子は、演算子が返す!=値の否定を返します=。その他は従来の意味を持ちます。すべての演算子と同様に、これらは後置表記で書かれます。結果は常に真理値です。例えば、
  
        'A 'E < 2 3 + 15 3 / = および
と評価されますtrue。
セット、文字列、リスト
集約タイプは、セットの順序なしタイプと、文字列およびリストの順序付きタイプです。集合体は、構築、結合、分解し、メンバーシップをテストすることができます。このセクションでは、集計タイプのリテラルと演算子を紹介します。
セットは、0 個以上の小さな整数の順序付けされていないコレクションです。型セットのリテラルは中括弧内に記述され、空のセットは空の中括弧のペアとして記述されます。セット リテラルの場合、要素の順序は無関係であり、重複は影響しません。論理積と論理和の演算子もセットで定義されます。たとえば、2 つの同等のプログラムは、

  
        {1 3 5 7} {2 4 6 8} または {} または {3 4 5 6 7 8 9 10} および
        {3 7 5 1} {2 4 6 8} または {} または {3 4 5 6 7 8 9 10 10} および
に評価します{3 4 5 6 7 8}。否定演算子は、 not表現可能な最大セットに対する補数を受け取ります。ほとんどの実装では、最大 32 個のメンバーが含まれます ( from から0) 31。
文字列は、0 個以上の文字の順序付けされたシーケンスです。この文字列型のリテラルは二重引用符で囲まれ、空の文字列は 2 つの隣接する二重引用符の中に何も含まれないように書かれます""。これは、空白だけを含む文字列とは異なることに注意してください: " "。2 つの文字列を連結したり、文字列を反転したりできます。例えば、

  
    「dooG」逆「朝」「」連結連結「世界」連結
と評価されます"Good morning world"。
For many operators an implementation can choose whether to make it a primitive or define it in a library. Apart from execution speed, to the user it makes no difference as to which choice has been made. In the current implementation the reverse operator is defined in a library.

A list is an ordered sequence of zero or more values of any type. Literals of type list are written inside square brackets, the empty list is written as an empty pair of brackets. Lists can contain lists as members, so the type of lists is a recursive data type.

Values of the aggregate types, namely sets, strings and lists can be constructed from existing ones by adding a new member with the cons operator. This is a binary operator for which the first parameter must be a possible new member and the second parameter must be an aggregate. For sets the new member is added if it is not already there, and for strings and lists the new member is added in front. Here are some examples. The programs on the left evaluate to the literals on the right.

  
        5  3 {2 1}  cons  cons  3  swap  cons                {1 2 3 5}
        'E  'C  "AB"  cons  cons  'C  swap  cons               "CECAB"
        5  [6]  [1 2]  cons  cons  'A  swap  cons       ['A 5 [6] 1 2]
例が示すように、このcons演算子は、集約の下のスタック上に既に存在する集約に要素を追加する場合に最も役立ちます。プッシュされたばかりの新しい要素を追加するには、新しい要素を集約に追加するswap前に、まず新しい要素と集約を ped する必要があります。consこれを容易にするために、Joy には別のオペレーター があり、 swons最初に を実行しswap、次に を 実行しますcons。
Whereas the cons and swons operators builds up aggregate values, the two unary operators first and rest take them apart. Both are defined only on non-empty aggregate values. For the two ordered aggregate types, strings and lists, the meaning is obvious: the first operator returns the first element and the rest operator returns the string or list without the first element:

  
        "CECAB"  first                                              'C
        "CECEB"  rest                                           "ECAB"
        ['A 5 [6] 1 2]  first                                       'A
        ['A 5 [6] 1 2]  rest                               [5 [6] 1 2]
しかし、セットには順序がないので、最初のメンバーをセットとして 話すのは意味がありません。ただし、それらのメンバーは整数であるため、整数の順序を使用して最初のメンバーが何であるかを決定できます。同様の考慮事項がrest 演算子にも適用されます。
  
        {5 2 3} 最初の 2
        {5 2 3} 休憩 {3 5}
For all three types of aggregates the members other than the first can be extracted by repeatedly taking the rest and finally the first of that. This can be cumbersome for extracting member deep inside. An alternative is to use the at operator to index into the aggregate, by extracting a member at a numerically specified position. For example, the following are two equivalent programs to extract the fifth member of any aggregate:

  
        rest  rest  rest  rest  first
        5  at
There is a unary operator which determines the size of any aggregate value. For sets this is the number of members, for strings its is the length, and for lists it is the length counting only top level members. The size operator yields zero for empty aggregates and a positive integer for others. There is also a unary null operator, a predicate which yields the truth value true for empty aggregates and false for others. Another predicate, the small operator, yields true just in case the size is 0 or 1.

Apart from the operators which only affect the stack, there are two for explicit input and output. The get operator reads an item from the input file and pushes it onto the stack. The put operator pops an item off the stack and writes it to the screen or whatever the output file is. The next program reads two pairs of integers and then compares the sum of the first pair with the sum of the second pair.

  
        get  get  +  get  get  +  >  put
2 人のgetオペレーターは 2 つの項目を読み取り、スタックにプッシュしようとします。そこではすぐに追加されるため、整数である必要があります。これを 2 番目のペアでも繰り返します。この時点で、スタックには 2 つの合計が含まれています。次に、比較演算子は 2 つの整数をポップし、最初の合計が 2 番目の合計より小さいかどうかに応じて、真理値、true またはに置き換えます。false演算子putはその真理値をポップして書き込みます。スタックはプログラム実行前の状態のままになり、入力ファイルと出力ファイルのみが変更されます。
別の例として、以下は愚かな小さな対話を実行します。


  
        "あなたの名前は何ですか？" put "Hello", get concat put
まず質問文字列がスタックにプッシュされ、次にポップされて画面に書き出されます。次に、「Hello, "」文字列がプッシュされます。次に、getオペレーターはキーボードから項目を読み取り、それをスタックにプッシュします。この項目はスタック上でその下にあるものと連結されるため、別の文字列である必要があります。結果の文字列が書き出されます。したがって、質問への答えとしてユーザーが'"Pat"'と入力すると、プログラムは最終的に'"Hello, Pat"'を書き出します 。
John Cowan の拡張機能は、複合データ型セット、文字列、リストに加えて、ファイル システムを操作するための多数の演算子 (ファイルを開く、閉じる、削除する、さまざまな入出力演算子) を提供します。これらはこのチュートリアルの範囲外です。

引用符と結合子
リストは実際には、引用符で囲まれたプログラム の特殊なケースにすぎません。リストにはさまざまなタイプの値のみが含まれますが、引用されたプログラムには、演算子や以下で説明するその他の要素などの他の要素が含まれる場合があります。引用はリストと同様に受動的なデータ構造として扱うことができます。例えば、
  
        [ + 20 * 10 4 - ]
size があり6、その 2 番目と 3 番目の要素は 20andであり*、逆にすることも、他の引用符と連結することもできます。ただし、受動的な引用は、 dequoteによってアクティブにすることもできます。
If the above quotation occurs in a program, then it results in the quotation being pushed onto the stack - just as a list would be pushed. There are many other ways in which that quotation could end up on top of the stack, by being concatenated from its parts, by extraction from a larger quotation, or by being read from the input. No matter how it got to be on top of the stack, it can now be treated in two ways: passively as a data structure, or actively as a program. The square brackets prevented it from being treated actively. Without them the program would have been executed: it would expect two integers which it would add, then multiply the result by 20, and finally push 6, the difference between 10 and 4.

Joy にはコンビネータと呼ばれる特定のデバイスがあり、スタックの最上部にある引用されたプログラムを実行します。このセクションでは、それらのうちのごく一部のみを説明します。

最も単純なものの 1 つはiコンビネータです。その結果、スタックの最上位で単一のプログラムが実行され、他には何も実行されません。構文的に言えば、その効果は引用角括弧を削除し、引用されたプログラムを実行用に公開することです。したがって、次の 2 つのプログラムは同等です。

  
        [ + 20 * 10 4 - ] i
          + 20 * 10 4 -
コンビネータiは主に理論的に重要ですが、時々使用されます。他の多くのコンビネータは、Joy でのプログラミングに不可欠です。
One of the most well-known combinators is for branching. The ifte combinator expects three quoted programs on the stack, an if-part, a then-part and an else-part, in that order, with the else-part on top. The ifte combinator removes and saves the three quotations and then performs the following on the remainder of the stack: It executes the if-part which should leave a truth value on top of the stack. That truth value is saved and the stack is restored to what it was before the execution of the if-part. Then, if the saved truth value was true, the ifte combinator executes the then-part, otherwise it executes the else-part.

In most cases the three parts would have been pushed in that order just before the ifte combinator is executed. But any or all of the three parts could have been constructed from other quotations.

In the following example the three parts are pushed just before the ifte combinator is executed. The program looks at a number on top of the stack, and if it is greater than 1000 it will halve it, otherwise it will triple it.

  
        [1000 >]  [2 /]  [3 *]  ifte
Some combinators require that the stack contains values of certain types. Many are analogues of higher order functions familiar from other programming languages: map, filter and fold. Others only make sense in Joy. For example, the step combinator can be used to access all elements of an aggregate in sequence. For strings and lists this means the order of their occurrence, for sets it means the underlying order. The following will step through the members of the second list and swons them into the initially empty first list. The effect is to reverse the non-empty list, yielding [5 6 3 8 2].

  
        []  [2 8 3 6 5]  [swons]  step
The map combinator expects an aggregate value on top of the stack, and it yields another aggregate of the same size. The elements of the new aggregate are computed by applying the quoted program to each element of the original aggregate. An example was already given in the introduction.

Another combinator that expects an aggregate is the filter combinator. The quoted program has to yield a truth value. The result is a new aggregate of the same type containing those elements of the original for which the quoted program yields true. For example, the quoted program ['Z >] will yield truth for characters whose numeric values is greater than that of Z. Hence it can be used to remove upper case letters and blanks from a string. So the following evaluates to "ohnmith":

  
        "John Smith"   ['Z >]   filter
Sometimes it is necessary to add or multiply or otherwise combine all elements of an aggregate value. The fold combinator can do just that. It requires three parameters: the aggregate to be folded, the quoted value to be returned when the aggregate is empty, and the quoted binary operation to be used to combine the elements. In some languages the combinator is called reduce (because it turns the aggregate into a single value), or insert (because it looks as though the binary operation has been inserted between any two members). The following two programs compute the sum of the members of a list and the sum of the squares of the members of a list. They evaluate to 10 and 38, respectively.

  
        [2 5 3]  0  [+]  fold
        [2 5 3]  0  [dup * +]  fold
To compute the average or arithmetic mean of the members of a set or a list, we have to divide the sum by the size. (Because of the integer arithmetic, the division will produce an inaccurate average.) The aggregate needs to be looked at twice: once for the sum and once for the size. So one way to compute the average is to duplicate the aggregate value first with the dup operator. Then take the sum of the top version. Then use the swap operator to interchange the position of the sum and the original aggregate, so that the original is now on top of the stack. Take the size of that. Now the stack contains the sum and the size, with the size on top. Apply the division operator to obtain the average value.

  
        dup  0  [+]  fold  swap  size  /
この小さなプログラムの優れた機能の 1 つは、設定値に対してもリスト値に対しても同様に機能することです。これは、構成要素 foldとsize機能が両方のタイプに対応しているためです。
But there are two aspects of this program which are unsatisfactory. One concerns the dup and swap operators which make the program hard to read. The other concerns the sequencing of operations: The program causes the computation of the sum to occur before the computation of the size. But it does not matter in which order they are computed, in fact on a machine with several processors the sum and the size could be computed in parallel. Joy has a combinator which addresses this problem: there is one data parameters, the aggregate, which is to be fed to two functions. From each of the functions a value is to be constructed, by calling both functions by means of a combinator cleaveこれにより、合計とサイズの 2 つの値が生成されます。平均のプログラムは次のようになります。

  
        [0 [+]折り] [サイズ] 折り目 /
定義
従来の言語では、1 つ以上の引数の関数を定義するには、これらを仮パラメータとして指定する必要がありますx。 yたとえば、二乗関数は、次のいずれかのバリエーションによって定義される可能性があります。
  
        平方(x) = x * x
        (でふん(四角×)(*××))
        平方 = ラムダ xx * x
Joy では、x上記のような仮パラメータは必要ありません。二乗関数の定義は単純です。
  
        正方形 == 二重 *
これは、Joy とラムダ計算に基づく言語との主な違いの 1 つです。後者には、Lisp、Scheme、ML、Haskell (の純粋に機能的なサブセット) が含まれます。これらはすべて、引数または実際のパラメータへの関数の適用に基づいています。
In definitions and abstractions of functions the formal parameters have to be named - x, y and so on, or something more informative. This is different in Joy. It is based on the composition of functions and not on the application of functions to arguments. In definitions and abstractions of functions the arguments do not need be named and as formal parameters indeed cannot be named. One consequence is that there are no environments of name-value pairs. Instead the work of environments is done by higher order functions called combinators.

Finally, the concrete syntax of the language is an integral part of the language and aids in reasoning about Joy programs in the metalanguage.

数値のリストをその立方体のリストに変換する必要があるとします。もちろん、単一の数値の 3 乗は次のように計算されます。

  
        ダップダップ * *
立方体関数の定義を導入することは可能です。しかし、それは別の名前、 を導入することになりますcube。cube 関数が数値リストの 3 乗を計算するために 1 回だけ使用される場合、関数の定義をまったく与えることは望ましくない可能性があります。Joy では、立方体のリストは以下の 1 行目で計算されますが、2 行目のように明示的に定義することも可能です。
  
        [ダップダップ * *] マップ
        cubelist == [dup dup * *] マップ
ラムダ計算に基づく言語では、両方とも、数値をx3 乗するための変数、たとえば を使用したラムダ抽象化が必要になります。そしてもちろん、2 行目には追加の仮パラメータ、つまりcubelist 関数が適用されるリスト lの変数を含むラムダ抽象化が必要になります。l
ここで、 数値のリストのリストをその立方体のリストのリストに変換する必要があるとします。定義を与えるかもしれない

  
        cubelistlist == [ [dup dup * *] マップ ] マップ
もちろん、その関数が 1 回だけ使用される場合は、わざわざ定義を与える必要はなく、右辺を直接使用することもできます。抽象化に基づく言語では、右辺だけで少なくとも 2 つの仮パラメータが必要であり、定義自体にはもう 1 つが必要です。たとえば、Scheme では定義は次のようになります。
  
        (定義 (cubelistlist ll)
                (マップ (ラムダ (l)
                     (マップ (ラムダ (n) (* n (* nn)))
                           l))
                 ll）
ここで、2 つの仮パラメータは、右側の n数値と 数値のリスト、および定義自体の数値のリストのリストです。 lll
他の言語と同様に、Joy では定義を再帰的に行うことができます。以下の最初の行は、従来の表記法の多くの変形のうちの 1 つでの階乗関数の再帰的定義です。2 行目は Joy での再帰的な定義です。

  
        階乗(x) = x = 0の場合は1、それ以外の場合はx *階乗(x - 1)
        階乗 == [0 =] [ポップ 1] [dup 1 - 階乗 *] ifte
繰り返しになりますが、Joy バージョンでは仮パラメータは使用されませんx。それは次のように動作します。定義ifteでは、if 部分、then 部分、else 部分がプッシュされた直後にコンビネータが使用されます。
The ifte combinator then does this: it executes the if-part, in this case [0 =], which tests whether the (anonymous) integer parameter is equal to zero. If it is, then the if-part is executed, in this case [pop 1], which pops the parameter off the stack and replaces it by one. Otherwise the else-part is executed, in this case [dup 1 - factorial *]. This uses dup to make another copy of the parameter and subtracts one from the copy. Then the factorial function is called recursively on that. Finally the original parameter and the just computed factorial are multiplied.

The definition could be shortened and made a little more efficient by using the inbuilt predicate null which tests for zero and the pred operator which takes the predecessor of a number. But these changes are insignificant.

For more complex functions of several arguments it is necessary to be able to access the arguments anywhere in the definition. Joy avoids formal parameters altogether, and hence in general arbitrary access has to be done by mechanisms more sophisticated than dup, swap and pop.

Here are some more definitions that one might have:


  
        sum   ==   0  [+]  fold
        product   ==   1  [*]  fold
        average   ==   [sum]  [size]  constr12  /
        concatenation   ==   ""  [concat]  fold
最後の定義は、文字列のリストを連結した単一の文字列を生成する演算子用です。
再帰的コンビネータ
与えられたリストの階乗のリストを計算したい場合、これは次のように行うことができます。
  
        [ 階乗 ] マップ
しかし、これは階乗の外部定義に依存します。この定義は再帰的であるため、明示的に指定する必要がありました。数値のリストの階乗を計算したいだけであれば、定義が再帰的であるという理由だけで階乗を明示的に定義することを強いられるのは少々面倒です。
A high proportion of recursively defined functions exhibit a very simple pattern: There is some test, the if-part, which determines whether the ground case obtains. If it does, then the non-recursive then-part is executed. Otherwise the recursive else-part has to be executed. In the else-part there is only one recursive call, and there can be something before the recursive call and something after the recursive call. It helps to think of the else-part to have two components, the else1-part before the recursive call, and the else2-part after the recursive call. This pattern is called linear recursion, and it occurs very frequently.

Joy にはコンビネータという便利なデバイスがあり、これを使用すると、線形再帰パターンを使用して再帰的に定義された可能性linrecのある匿名関数の計算が可能になります。コンビネータには引用符で囲まれたパラメータが 3 つ必要です が、コンビネータには if 部分、then 部分、else1 部分​​、else2 部分の 4 つが必要です。たとえば、階乗関数は次のように計算できます。 iftelinrec

  
        [null] [succ] [dup pred] [*] linrec
定義は必要なく、上記のプログラムをそのまま使用できます。
Very frequently the if-part of a linear recursion tests for a simple base condition which depends on the type of the parameter. For numbers that condition tends to be being zero, for sets, strings and lists that condition tends to be being empty. The else1-part frequently makes the parameter smaller in some way. For numbers it decrements them, for sets, strings and lists it takes the rest.

Joy has another useful combinator which has the appropriate if-part and else1-part built in. This is the primrec combinator, which only has to be supplied with two quotation parameters, the (modified) then-part and the else2-part of linear recursion. For the factorial function the two quotation parameters are very simple:

  
        [1]  [*]  primrec
階乗関数を計算します。したがって、与えられた数値リストの階乗リストを計算したい場合は、次のいずれかで実行できます。
  
        [ [null] [succ] [dup pred] [*] linrec ] マップ
        [ [1] [*] primrec ] マップ
数値の階乗は、実際のパラメータまでの連続する自然数の積です。以下は代わりに、それらの合計と二乗の合計を計算します。
  
        [0] [+] プリムレック
        [0] [dup * +] プリムレック
Joy コンビネータの多くは、全く異なるタイプのパラメータに適用できるという意味でポリモーフィックです。コンビネータは primrec数値だけでなくリストにも適用できます。たとえば、[1 2 3]プログラムを リストに適用すると、
  
        [[]] [[] 短所 短所] primrec
リストを生成します[1 [2 [3 []]]]。Lisp プログラマは、「点線ペア」との類似点を認識するでしょう。以下では、1 つ目は数値のセットをリストに変換し、2 つ目は数値のリストをセットに変換します。
  
        [[]] [短所] プリムレック
        [{}] [短所] プリムレック
実際、最初の方法はリストにも適用でき、2 つ目はセットにも適用できます。ただし、その場合は恒等式を計算するだけです。これらは数値に適用することもでき、パラメータから 1 までの数値のリストまたはセットを生成します。
In many recursive definitions there are two recursive calls of the function being defined. This is the pattern of binary recursion, and it is used in the usual definitions of quicksort and of the Fibonacci function. Joy has a facility that eliminates the need for a recursive definition, the binrec combinator.

The following will quicksort a list whose members can be a mixture of anything except lists. The program easily fits onto one line, but for reference it is here written over several numbered lines:

  
```
[small]
[]
[uncons [>] split]
[[swap] dip cons concat]
binrec
```

動作の仕組みは次のとおりです。 1 行目から 4 行目はそれぞれ、引用符で囲まれたプログラムをプッシュします。5 行目でbinrecコンビネータが呼び出され、引用符で囲まれた 4 つのプログラムとその下にあるソート対象のリストが使用されます。引用された 4 つのプログラムは別の場所に保存され、binrec コンビネータはプログラムを 1 行目から実行します。これにより、ソートされるリストが小さいかどうか、つまりメンバーが 1 つ以下かどうかがテストされます。本当に小さい場合は、すでにソートされています。
The binrec combinator now executes the program from line 2, which does nothing and hence leaves the small list as it is. On the other hand, if the list is not small, then the programs in lines 3 and 4 will be executed. The program in line 3 removes the first element from the list and uses it as a pivot to split the rest of the list into two sublists, by using the comparison function in [>] and the split combinator.

At this point the binrec combinator calls itself recursively on the two sublists and sorts them both. Finally the program in line 4 combines the two sorted versions and the original pivot into a single sorted list. The three items are not quite in the required order, so the [swap] dip part puts the pivot in between the two sorted lists.

Then cons puts the pivot in front of the topmost string or list, and finally concat combines everything into one single sorted list. Since all operations in the program also work on strings, the program itself can equally well be used to sort a string.

In fact, the program can be used on sets too, but this of course is pointless. The program is useful, it is part of the Joy system library under the name of qsort.

Many other functions are often defined by recursive definitions that use binary recursion. In Joy they can all be computed with the binrec combinator without the need for a definition. For example, the following computes the Fibonacci function; it implements the usual inefficient algorithm:

  
```
        [small]  []  [pred dup pred]  [+]  binrec
```

もちろん、システム ライブラリには、よく知られた効率的なアルゴリズムが含まれています。
2 次コンビネータは少数しかありませんが、パラメータとして 1 次コンビネータを必要とするものもあります。1 つは、Treetreerecを 再帰するためのものです。これらはリスト以外のもの、またはツリーのリストのいずれかです。たとえば、以下では パラメータとして指定されており、リストに遭遇したときにパラメータとして 指定されます。リスト内に深く埋め込まれている可能性がある数値に適用される関数は、二乗関数です 。

```
treerec[map][dup *]treerec[dup *]
```

以下に例を示します。


  
        [ 1 [2 3] [[[4]]] 5 ] [dup *] [map] ツリーレック
生成する

  
        [ 1 [2 9] [[[16]]] 25 ]
これらのコンビネータはすべて他の関数型言語でも定義できますが、そこではあまり役に立ちません。これは、パラメータが Joy のように引用符ではなく、変数を含む抽象化である必要があるためです。

集合体を使用したプログラミング
Joy の集合 タイプはリスト、セット、文字列です。パラメータとして集計を受け取り、値として部分集計のリストを生成する単項演算子がいくつかあります。そのうちの 1 つはオペレーターです powerlist。サイズNの集合体の場合、すべての2^N部分集合体のリストが生成されます。
以下に例を示します。


  
        [1 2 3] パワーリスト
結果として生成される
  
        [ [1 2 3] [1 2] [1 3] [1] [2 3] [2] [3] [] ]
順序が合わない場合は、結果リストをいつでも並べ替えることができます。たとえば、逆にすることもできます。別の例として、サイズに従ってリストを並べ替えることができます。コンビネータ mk_qsortはパラメータとして集計と引用符で囲まれた演算子を予期し、演算子を集計の各メンバーに適用して、ソートの基礎として使用します。
  
        [1 2 3] パワーリスト [サイズ] mk_qsort
結果として生み出す

  
        [ [] [1] [2] [3] [1 2] [1 3] [2 3] [1 2 3] ]
The powerlist operators can also be applied to a string. The result is a list of all substrings. In the following the result list is filtered to retain only those substrings whose size is greater than 3. This is achieved by the filter combinator which expects an aggregate and a quoted predicate. The first line is the program, the second line is the result:

  
        "abcde"  powerlist  [size 3 >]  filter
        [ "abcde" "abcd" "abce" "abde" "acde" "bcde" ]
The powerlist operators can also be applied to a set. In the program on the first line below the list of subsets is then filtered to retain only those of size 3; the result is the list of subsets in the second line:

  
        {1 2 3 4}  powerlist  [size 3 =]  filter
        [ {1 2 3} {1 2 4} {1 3 4} {2 3 4} ]
Suppose it is required to find the list, in ascending order, of all sums of any three distinct numbers taken from a given set of numbers. We already know how to get the list of all three-membered subsets. Each should be replaced by its sum, and that can be done with the map combinator applied to the whole list. The resulting list of sums then needs to be sorted. The example in the first line does just that, giving the result in the second line:

  
        {1 2 3 4 5}  powerlist  [size 3 =] filter  [sum] map  qsort
        [6 7 8 8 9 9 10 10 11 12]
In the remainder of this section a small program is to be constructed which takes one sequence as parameter and returns the list of all permutations of that sequence. Here is a first draft:

  
1 S にメンバーが 0 つまたは 1 つしかない場合
2 の場合、順列は 1 つだけなので、その単位リストを取得します。
3 他の場合は S の最初と残りを取得します。
                    再帰して残りの順列を構築する
4 すべての順列のすべての位置に最初のものを挿入します
再帰パターンは線形であるため、linrec コンビネータを使用して、この最初の不完全なプログラムに到達できます。

  
1[小]
2 [ユニットリスト]
3 [アンコン]
4 [ "すべての順列のすべての位置に最初のものを挿入する" ]
5 リンレック
Sステップ 3 と 4 の間の匿名再帰により、残りの順列のリストがスタックの一番上に 残ります。
Next, it is necessary to insert the original first of S into all positions into all these resulting permutations. This involves replacing each single permutation by a list of permuations with the original first inserted in all places.

This calls for the map combinator to apply a constructed program to each permutation. The original first is currently the second item on the stack. to make it available to the program to be constructed, it is swapped to the top. The required program consists of a constant part and a variable part.

The constant part now has to be pushed onto the stack. Then the first is consed into the required program. Then map will create a list of list of permutations. But this is a two-level list, and it should be one-level. So the two level list has to be flattened to a one-level list.

  
4.1             [ swap
4.2               [ "the constant part of the program" ]
4.3               cons map
4.4               "flatten the resulting list of lists of sequences" ]
The constant part of the constructed program has to be written next. The constructed program will be used to map all permutations of the rest, and in each case it will begin by pushing the original first on top of the current permutation being mapped. It then has to insert this first into all positions of the current permutation.

This again calls for a linear recursion with linrec. One way to do this is to give this anonymous recursive function just one parameter, the current permutation with the original first swons in as an initial element. So the task is now to insert this inital element into all positions in the remainder which is the current permutation.

  
4.2.2.1         If  the current sequence is small
4.2.2.2             then return just its unit list
4.2.2.3 それ以外の場合は 1. コピーを保持します
                                2. 2番目と
                                3. 2 番目のないシーケンス
                          3 を匿名で再帰します。
4.2.2.4 2番目を挿入するプログラムを構築するmap挿入を行うために
                          使用しますcons1 からのコピーを追加するために

                          使用します。
したがって、定数部分 4.2 は次のようになります。
  
4.2.1 [ スウォン
4.2.2.1 [小]
4.2.2.2 [ユニットリスト]
4.2.2.3 [dup unswons [uncons] dip swons ]
4.2.2.4 [ スワップ [スウォン] の短所 マップの短所 ]
4.2.2.5 リンレック]
The only other part that needs to be written is for flattening. This should be trivial by now: If the list is small, then take its unit list else take its first and its rest anonymously recurse on the rest, concatenate the saved first into the result.

Here is the required program:


  

```
             [ null ] [ ] [ uncons ] [ concat]  linrec
```
The entire program now is the following:


  
```
[ small ]
[ unitlist ]
[ uncons ]
[ swap
[ swons
[ small ]
[ unitlist ]
[ dup unswons [uncons] dip swons ]
[ swap [swons] cons map cons ]
linrec ]
cons map
[null] [] [uncons] [concat] linrec ]
linrec.
```

An essentially identical program is in the Joy library under the name permlist. It is considerably shorter than the one given here because it uses two subsidiary programs insertlist and flatten which are useful elsewhere. The program given above is an example of a non-trivial program which uses the linrec combinator three times and the map combinator twice, with constructed programs as parameters on both occasions.

Of course such a program can be written in lambda calculus languages such as Lisp, Scheme, ML or Haskell, but it would need many recursive definitions with attendant named formal parameters.

Miscellaneous
現在の実装には他にも多くの機能があり、それらについてはより専門的なドキュメントで説明するのが最適です。利用可能な内容の概要については、オンライン ヘルプコマンドの出力を参照してください 。これにより、すべてのライブラリがロードされたときに、プリミティブと定義された関数の名前のリストのみが得られます。現在のプリミティブの実際の説明については、オンライン マニュアルコマンドの出力を参照してください 。定義された関数の定義については、メイン ページのセクション 3 にあるさまざまなライブラリを参照してください。
プログラミング言語 Joy のメインページ に戻る
