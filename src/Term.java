import java.util.ArrayList;

public class Term implements Cloneable{
    private int denominator;    // 分母
    private int numerator;      // 分子
    private ArrayList<Object[]> variable = new ArrayList<>();   // 変数を格納する二次元配列

    Term(int nume, int denom){
        numerator = nume;
        denominator = denom;
    }

    Term(int nume){
        numerator = nume;
        denominator = 1;
    }

    Term(int nume, int denom, ArrayList<Object[]> var){
        numerator = nume;
        denominator = denom;
        variable = var;
    }

    Term(String term){
        StringBuilder tmp = new StringBuilder();
        String var = "";
        char c;

        // 1文字ずつ取り出す
        for(int i=0; i<term.length(); i++){
            c = term.charAt(i);

            if(Character.isDigit(c)){
                // 数字ならばtmpに移動
                tmp.append(c);
            } else if(Character.isAlphabetic(c)) {
                if(var.equals("")) {
                    // 係数をnumeratorに代入する
                    if (tmp.length() > 0) {
                        numerator = Integer.valueOf(tmp.toString());
                        tmp.setLength(0);
                    } else {
                        // 係数が明示されてなければ係数1とする
                        numerator = 1;
                    }
                } else {
                    // 既にチェック中の変数があれば、それを追加する
                    if(tmp.length() == 0){
                        variable.add(new Object[]{var, 1});
                    } else {
                        variable.add(new Object[]{var, Integer.valueOf(tmp.toString())});
                    }
                    tmp.setLength(0);
                }

                // 現在チェック中の変数名として格納
                var = String.valueOf(c);
            }
        }

        // 一時変数に入っている中身を処理
        if(var.equals("")){
            numerator = Integer.valueOf(tmp.toString());
        } else {
            // 既にチェック中の変数があれば、それを追加する
            if(tmp.length() == 0){
                variable.add(new Object[]{var, 1});
            } else {
                variable.add(new Object[]{var, Integer.valueOf(tmp.toString())});
            }
        }

        denominator = 1;
    }

    // 分子の符号を反転させる
    public Term inverse(){
        this.numerator = -this.numerator;
        return this;
    }

    public void setDenominator(int i){
        denominator = i;
    }

    public void setNumerator(int i){
        numerator = i;
    }

    public void setVariable(ArrayList<Object[]> var){
        variable = var;
    }

    // 0乗の変数を削除する
    private void shortify() {
        // 0乗の変数を削除する
        ArrayList<Object[]> shortified = new ArrayList<>();
        for (Object[] obj : variable) {
            if ((int) obj[1] != 0)
                shortified.add(obj);
        }
        variable = shortified;
    }

    // 変数を並び替える
    private void sortVariables(){
        // コムソートによりソートする。計算時間はほぼO(nlogn)
        if(variable.size() > 1) {
            int h = variable.size() * 10 / 13;
            String a, b;
            while (true) {
                for (int i = 0; i + h < variable.size(); i++) {
                    a = (String)variable.get(i)[0];
                    b = (String)variable.get(i+h)[0];

                    if (a.charAt(0) > b.charAt(0)) {
                        Object[] tmp = variable.get(i + h);
                        variable.set(i + h, variable.get(i));
                        variable.set(i, tmp);
                    }
                }
                if (h == 1) {
                    break;
                } else {
                    h = h * 10 / 13;
                }
            }
        }
    }

    // 約分を行う
    private void reduction(){
        int gcd = gcd(denominator, numerator);
        numerator = numerator / gcd;
        denominator = denominator / gcd;

        if(denominator < 0){
            denominator = -denominator;
            numerator = -numerator;
        }

    }

    // Shortify, sortVariables, reductionを全て行う
    public void beautify(){
        if(numerator != 0)
            reduction();

        sortVariables();
        shortify();
    }

    // 変数の数を返す
    public int variableSize(){
        return variable.size();
    }

    // 指定番目の変数を取り出す
    public Object[] getVariable(int num){
        return variable.get(num);
    }

    // 最大公約数を求める
    private static int gcd(int m, int n){
        if(m % n == 0){
            return n;
        } else {
            return gcd(n, m%n);
        }
    }

    // 最大公倍数を求める
    private static int lcm(int m, int n){
        return m * n / gcd(m,n);
    }

    // 変数を持っているかどうか
    public boolean hasVariable(){
        return (variable.size() != 0);
    }

    // 同じ変数を持っているかどうか
    public boolean variableEquals(Term a){
        if(variable.size() == a.variableSize()){
            outer : for(Object[] var : variable){
                for(Object[] avar : a.variable){
                    // 両方の項が同じ変数を持っている場合
                    if(var[0].equals(avar[0]) && var[1].equals(avar[1])){
                        continue outer;
                    }
                }
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    // この項が整数かどうか
    public boolean isInteger(){
        return (denominator == 1);
    }

    // 分子が正数かどうか
    public boolean isPositive(){
        return (numerator > 0);
    }

    // この項が負数かどうか
    public boolean isNegative(){
        return (numerator < 0);
    }

    // 分子がゼロかどうか
    public boolean isZero(){
        return (numerator == 0);
    }

    // 複製する
    @Override
    public Term clone(){
        Term tmp = null;
        try {
            tmp = (Term)super.clone();

            // ArrayListのcloneメソッドはシャローコピーしかしてくれないので手作業でディープコピー
            tmp.variable = new ArrayList<>();
            for(Object[] obj : this.variable){
                tmp.variable.add(obj.clone());
            }
        } catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return tmp;
    }

    // 文字列化
    @Override
    public String toString(){
        StringBuilder tmp = new StringBuilder();
        boolean flg = false;    // マイナスｎ乗の変数があるか

        if(variable.size() == 0){
            tmp.append(numerator);
        } else {
            switch(numerator) {
                case -1:
                    // -1xは-xと表示する
                    tmp.append("-");
                    break;

                case 1:
                    // 1のときは何もしない
                    break;

                case 0:
                    // 0だったら変数の中身を出力せずにreturnする
                    return tmp.toString();

                default:
                    tmp.append(numerator);
                    break;
            }

            // この項が持つ指数が正の数の変数を順に出力する
            for(Object[] var : variable) {
                if ((int) var[1] < 0) {
                    // マイナスｎ乗の変数があったら分母に出力するためフラグを立てる
                    flg = true;
                } else if ((int) var[1] != 1) {
                    // 1乗以外の変数があったら分子に出力する
                    tmp.append(var[0]);
                    tmp.append("^");
                    tmp.append(var[1]);
                } else {
                    tmp.append(var[0]);
                }

            }

        }

        // 分母を出力する
        if(denominator != 1 || flg) {
            tmp.append("/");
            if (denominator != 1) {
                tmp.append(denominator);
            }

            if(flg) {
                for (Object[] var : variable) {
                    if ((int) var[1] < -1) {
                        // マイナスｎ乗の変数を出力
                        tmp.append(var[0]);
                        tmp.append("^");
                        tmp.append(-(int)var[1]);
                    } else if ((int) var[1] == -1) {
                        // マイナス1乗の変数は単に変数のみ出力
                        tmp.append(var[0]);
                    }
                }
            }
        }

        return tmp.toString();
    }

    public int getDenominator(){
        return denominator;
    }

    public int getNumerator(){
        return numerator;
    }

    public ArrayList<Object[]> getVariable(){
        // ArrayListのcloneメソッドはシャローコピーなので手作業でディープコピー
        ArrayList<Object[]> tmp = new ArrayList<>();
        for(Object[] obj : variable){
            tmp.add(obj.clone());
        }
        return tmp;
    }
}
