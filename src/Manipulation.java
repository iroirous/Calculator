import java.util.*;

public class Manipulation {
    private static HashMap<String, Integer> priority = new HashMap<>();
    private static HashMap<String, String> associativity = new HashMap<>();

    Manipulation(){
        // 各演算子の優先順位をロードする
        priority.put("+", 10);
        priority.put("-", 10);
        priority.put("*", 20);
        priority.put("/", 20);
        priority.put("minus", 30);  // 負数を表す
        priority.put("^", 40);
        priority.put("!", 50);
        priority.put("var*", 25);  // 係数と変数を繋ぐ記号

        // 各演算子の結合性をロードする
        associativity.put("(", "special");
        associativity.put(")", "special");
        associativity.put("+", "left");
        associativity.put("-", "left");
        associativity.put("*", "left");
        associativity.put("/", "left");
        associativity.put("var*", "left");
        associativity.put("^", "right");
        associativity.put("!", "leftonly");
        associativity.put("minus", "leftonly"); // 負数を表す
    }

    // 与えた文字列を計算する（第二引数省略版）
    public String calculate(String str){
        return calculate(str, false);
    }

    // 与えた文字列を計算する
    public String calculate(String str, boolean showDebug){
        if(showDebug) {
            System.out.print("入力数式：");
            System.out.println(str);
        }
        ArrayList<String> token = generateToken(str);

        if(showDebug) {
            System.out.print("トークン：");
            printArray(token);
        }
        ArrayList<String> rpn = shuntingYard(token);

        if(showDebug) {
            System.out.print("逆ポーランド記法：");
            printArray(rpn);
        }
        Polynomial poly = calculate(rpn).beautify();

        return poly.toString();
    }

    // 逆ポーランド記法の配列を走査して計算する（スタック版）
    private Polynomial calculate(ArrayList<String> rpn){
        ArrayDeque<Polynomial> stack = new ArrayDeque<>();
        String associativeValue;
        Polynomial left, right;

        for(String token : rpn){
            if(priority.containsKey(token)){
                // 演算子ならば、スタックから取り出して計算する
                associativeValue = associativity.get(token);
                if(associativeValue.equals("leftonly")){
                    // 左の項のみに働く演算子のとき
                    // スタックから1つ取り出して演算
                    left = stack.pop();
                    switch(token){
                        case "!":
                            stack.push(Calculate.factorial(left));
                            break;

                        case "minus":
                            stack.push(Calculate.signInversion(left));
                            break;
                    }
                } else {
                    right = stack.pop();
                    left = stack.pop();
                    switch(token){
                        case "+":
                            stack.push(Calculate.addition(left, right));
                            break;

                        case "-":
                            stack.push(Calculate.subtraction(left, right));
                            break;

                        case "*":
                            stack.push(Calculate.multiplication(left, right));
                            break;

                        case "/":
                            stack.push(Calculate.division(left, right));
                            break;

                        case "^":
                            stack.push(Calculate.power(left, right));
                            break;

                        case "var*":
                            stack.push(Calculate.multiplication(left, right));
                            break;
                    }
                }
            } else {
                // 演算子でなければ（＝数値ならば）スタックに積む
                stack.push(new Polynomial(token));
            }
        }

        return stack.pop();
    }

    // 構文木を走査して計算する
    private Polynomial calculate(Tree tree){
        // Pre-orderで走査しながら再帰的に計算する
        if(tree.hasChildren()){
            // 子を持っている場合
            String operator = tree.getItem();
            Polynomial left = calculate(tree.getLeft());
            Polynomial right = calculate(tree.getRight());

            // 演算子によって動作を変える
            if(operator == null){
                throw new NullPointerException();
            } else switch(operator){
                case "+":
                    return Calculate.addition(left, right);

                case "-":
                    return Calculate.subtraction(left, right);

                case "*":
                    return Calculate.multiplication(left, right);

                case "/":
                    return Calculate.division(left, right);

                case "^":
                    return Calculate.power(left, right);

                case "var*":
                    return Calculate.multiplication(left, right);
            }
        } else if(tree.hasLeft()){
            // 左の要素しか持っていない木
            String operator = tree.getItem();
            if(operator == null){
                throw new NullPointerException();
            } else switch(operator){
                case "minus":
                    return Calculate.signInversion(calculate(tree.getLeft()));

                case "!":
                    return Calculate.factorial(calculate(tree.getLeft()));
            }
        } else {
            // 子を持っていない場合
            return new Polynomial(tree.getItem());
        }

        throw new Error("木の構造が正しくないか認識できない演算子が含まれている可能性があります");
    }

    // 数式をトークンに分解する
    private ArrayList<String> generateToken(String formula){
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder tmp = new StringBuilder();


        // 数式を一文字ずつ取り出す
        char c; // 現在見ている文字
        for(int i=0; i<formula.length(); i++){
            // 現在見ている文字
            c = formula.charAt(i);

            if(Character.isDigit(c)){
                // 文字が数字の場合
                tmp.append(c);
            } else if(Character.isAlphabetic(c)) {
                // 文字がアルファベットの場合

                // tmpが空のときは係数1とみなす
                if(tmp.length() != 0){
                    // tmpの中身を配列へ
                    tokens.add(tmp.toString());
                    tmp.setLength(0);

                    // 係数と文字をかけるようにする
                    tokens.add("var*");
                }

                tmp.append(c);
            } else {
                // tmpに何かあったら配列に追加する
                if (tmp.length() > 0) {
                    tokens.add(tmp.toString());
                    tmp.setLength(0);
                }

                // その他の文字列は演算子とみなして配列へ
                if (c == '-') {
                    // 負数と減算を区別する
                    if((i > 0 && formula.charAt(i-1) == '(') || i == 0){
                        tokens.add("minus");
                    } else {
                        tokens.add(String.valueOf(c));
                    }
                } else {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        // tmpに文字が残っていれば配列に追加
        if(tmp.length() > 0){
            tokens.add(tmp.toString());
        }

        return tokens;
    }

    // 操車場アルゴリズムを用いてトークンを逆ポーランド記法へ変換
    private ArrayList<String> shuntingYard(ArrayList<String> token){
        ArrayList<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        String stackFirst;  // スタックの最上段を見る
        int stackFirstPriority, sPriority;  // 演算子を見るときに使う整数型変数
        char c; // 現在見ているトークンの先頭の文字
        String s;

        for(int i=0; i<token.size(); i++){
            // トークンを1つずつ読み込んで処理を行う
            s = token.get(i);
            c = s.charAt(0);

            if(priority.containsKey(s)) {
                sPriority = priority.get(s);

                // sが演算子の場合の処理
                if(!stack.isEmpty()) {
                    // スタックの最上段を見る
                    stackFirst = stack.getFirst();

                    // スタックの最上段にあるトークンが演算子の場合のループ
                    if (priority.containsKey(stackFirst)) {
                        do {
                            stackFirstPriority = priority.get(stackFirst);
                            // 左結合の演算子であり、トークンsの優先順位 <= スタックの最上段のトークンoの優先順位を満たすとき、または
                            // その他の結合性を持つ演算子であり、トークンsの優先順位 < スタックの最上段のトークンoの優先順位を満たすとき
                            if(sPriority < stackFirstPriority || (sPriority <= stackFirstPriority && associativity.get(s).equals("left"))){
                                output.add(stack.pop());
                            } else {
                                break;
                            }

                            if (stack.isEmpty()) {
                                break;
                            }

                            stackFirst = stack.getFirst();
                        } while (priority.containsKey(stackFirst));
                    }
                }

                // sをスタックに追加
                stack.push(s);
            } else if((Character.isDigit(c) || Character.isAlphabetic(c))){
                // 数字の場合は出力キューに追加する
                output.add(s);
            } else if(s.equals("(")){
                // トークンが左括弧の場合
                // 1つ前が演算子以外だったら「＊」をスタックに追加

                // 左括弧の前が演算子以外だったら「＊」をスタックに追加（乗算とみなす）
                if(i>0 && !associativity.containsKey(token.get(i-1))){
                    stack.push("*");
                }


                stack.push(s);
            } else if(s.equals(")")){
                // トークンが右括弧の場合
                // 左括弧までスタックの中身をキューに追加し続ける
                String tmp = stack.pop();

                while (!tmp.equals("(")) {
                    output.add(tmp);
                    tmp = stack.pop();
                }

                // 右括弧の次が演算子以外もしくは左括弧だったら「＊」をスタックに追加（乗算とみなす）
                if(i<token.size()-1 && (token.get(i+1).equals("(") || !associativity.containsKey(token.get(i+1)))){
                    stack.push("*");
                }

                // 左括弧は捨てる
            }
        }

        // 読み込むトークンが無くなった場合
        while(!stack.isEmpty()){
            output.add(stack.pop());
        }

        return output;
    }

    // 構文木を生成する
    private Tree generateTree(ArrayList<String> rpn) {
        Deque<Tree> stack = new ArrayDeque<>();

        Tree a, b;
        for(String s : rpn){
            // cが演算子であるか
            if(priority.containsKey(s) && !associativity.get(s).equals("leftonly")){
                // 演算子の場合、スタックから2つ取り出し、それを左右に持つTreeインスタンスを生成
                a = stack.pop();
                b = stack.pop();
                stack.push(new Tree(b, a, s));
            } else if(associativity.containsKey(s)){
                // 階乗など左側の項のみに対して働く演算子の場合、スタックから一個取り出して計算する
                a = stack.pop();
                stack.push(new Tree(a, s));
            } else {
                // 数値の場合、Treeインスタンスを作成しスタックに積む
                stack.push(new Tree(s));
            }
        }

        return stack.pop();
    }



    // 配列の内容を先頭から順に出力
    private static void printArray(ArrayList<String> array){
        for(String s : array){
            System.out.print(s + " ");
        }
        System.out.println();
    }
}
