# Calculator

Java製の数式計算機です。中学2年生程度の多項式の計算ができます。

# 使い方
Manipulation.calculateメソッドの第二引数は省略可能で、trueを与えると入力数式、トークン列、逆ポーランド記法をデバッグ情報として出力します。

```Java
Manipulation mnp = new Manipulation();
System.out.println(mnp.calculate("10x(25x+3y)", true));
```