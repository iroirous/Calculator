import java.util.ArrayList;

public class Polynomial implements Cloneable{
    private ArrayList<Term> numerator = new ArrayList<>();
    private ArrayList<Term> denominator = new ArrayList<>();

    Polynomial(String nume){
        numerator.add(new Term(nume));
        denominator.add(new Term(1));
    }

    Polynomial(ArrayList<Term> nume, ArrayList<Term> denom){
        numerator = nume;
        denominator = denom;
    }

    public Polynomial beautify(){
        reduction();
        numerator = termSort(numerator);
        denominator = termSort(denominator);

        // 分母の最初の項が負の値のとき、全ての項の符号を反転させる
        if(denominator.get(0).isNegative()){
            for(Term term : numerator){
                term.signInversion();
            }
            for(Term term : denominator){
                term.signInversion();
            }
        }
        return this;
    }

    // 項を降べきの順に並べ替える
    private ArrayList<Term> termSort(ArrayList<Term> terms){
        // コムソートにより並べ替える。計算時間はほぼO(nlogn)
        int i;
        if(terms.size() > 1){
            int h = terms.size() * 10 / 13;
            Term a, b, tmp;
            char aVar, bVar;
            int aExp, bExp;
            while(true){
                for(i = 0; i + h < terms.size(); i++) {
                    a = terms.get(i);
                    b = terms.get(i + h);

                    // 先頭の変数を調べる
                    if(a.hasVariable()){
                        aVar = a.getVariable(0).getVariable();
                    } else {
                        aVar = 127;   // 定数項のときはDEL文字を代入
                    }
                    if(b.hasVariable()){
                        bVar = b.getVariable(0).getVariable();
                    } else {
                        bVar = 127;
                    }

                    if (aVar > bVar) {
                        tmp = b;
                        terms.set(i + h, a);
                        terms.set(i, tmp);
                    } else if(aVar == bVar) {
                        // 同じ変数の場合、指数の大きな順に並べる（降べきの順）
                        aExp = a.getVariable(0).getExponent();
                        bExp = b.getVariable(0).getExponent();

                        if(aExp < bExp){
                            tmp = b;
                            terms.set(i + h, a);
                            terms.set(i, tmp);
                        }
                    }
                }
                if(h == 1){
                    break;
                } else {
                    h = h * 10 / 13;
                }
            }
        }

        // 係数が0の項を削除する
        for(i = terms.size()-1; i>=0; i--){
            if(terms.get(i).getCoefficient() == 0){
                terms.remove(i);
            } else {
                break;
            }
        }

        return terms;
    }

    // 各項で共通している変数とその指数を求める
    private ArrayList<Variable> getCommonVariables(ArrayList<Term> terms, ArrayList<Variable> now, boolean isNumerator){
        ArrayList<Variable> vars = now;
        Term t;
        Variable varsv, v;
        for(int i=0; i<terms.size(); i++){
            t = terms.get(i);
            if(i == 0 && isNumerator){
                for(Variable tv : t.getVariable()){
                    vars.add(tv.clone());
                }
            } else {
                // 全ての項に共通していない変数を削除していく
                // 変数を持たない項であれば
                if(t.getVariable().size() == 0){
                    // 全ての変数を削除
                    vars.clear();
                    return vars;
                }

                outer : for(int j=0; j<vars.size(); j++) {
                    varsv = vars.get(j);
                    // 変数が共通ならば指数を見る
                    for (int k = 0; k < t.getVariable().size(); k++) {
                        v = t.getVariable(k);
                        if (v.variableEquals(varsv)) {
                            if (v.getExponent() <= varsv.getExponent()) {
                                varsv.setExponent(v.getExponent());
                            }
                            continue outer;
                        }

                    }
                    // 共通していなかったら削除
                    vars.remove(j);
                    j--;
                }
            }
        }
        return vars;
    }

    // 約分する
    private void reduction(){
        // 変数を約分する
        ArrayList<Variable> vars = getCommonVariables(numerator, new ArrayList<>(), true);
        vars = getCommonVariables(denominator, vars, false);

        // 分子と分母の係数を配列に
        ArrayList<Integer> nums = new ArrayList<>();
        for(Term t : numerator){
            if(t.getCoefficient() != 0)
                nums.add(t.getCoefficient());
        }
        for(Term t : denominator){
            if(t.getCoefficient() != 0)
                nums.add(t.getCoefficient());
        }
        int gcd = Calculate.gcd(nums);
        // 新しい係数を設定しつつ、全ての項に共通する変数を取り除く
        char c;
        for(Term t : numerator){
            t.setCoefficient(t.getCoefficient() / gcd);
            for(Variable v : vars) {
                c = v.getVariable();
                t.setVariableExponent(c, t.getVariable(c).getExponent() - v.getExponent());
            }
            t.beautify();   // 項をきれいにする
        }
        for(Term t : denominator){
            t.setCoefficient(t.getCoefficient() / gcd);
            for(Variable v : vars) {
                c = v.getVariable();
                t.setVariableExponent(c, t.getVariable(c).getExponent() - v.getExponent());
            }
            t.beautify();   // 項をきれいにする
        }
    }

    private ArrayList<Term> getNumerator(){
        return numerator;
    }

    private ArrayList<Term> getDenominator(){
        return denominator;
    }

    // 分母が1かどうか
    private boolean isDenominatorOne(){
        return (denominator.size() == 1 && denominator.get(0).isInteger() && denominator.get(0).getCoefficient() == 1);
    }

    // 分子が整数かどうか
    private boolean isNumeratorInteger(){
        return (numerator.size() == 1 && numerator.get(0).isInteger());
    }

    // 分子が正数かどうか
    private boolean isNumeratorPositive(){
        return (isNumeratorInteger() && numerator.get(0).getCoefficient() > 0);
    }

    // 符号を反転する
    public Polynomial signInversion(){
        for(Term t : numerator){
            t.signInversion();
        }
        return this;
    }

    // 加算
    public Polynomial addition(Polynomial right){
        // 新たな分母を求める
        this.numerator = Calculate.addition(
                Calculate.multiplication(numerator, right.getDenominator()),
                Calculate.multiplication(right.getNumerator(), denominator)
        );
        this.denominator = Calculate.multiplication(getDenominator(), right.getDenominator());
        return this;
    }

    // 減算
    public Polynomial subtraction(Polynomial right){
        // 新たな分母を求める
        this.numerator = Calculate.subtraction(
                Calculate.multiplication(numerator, right.getDenominator()),
                Calculate.multiplication(right.getNumerator(), denominator)
        );
        this.denominator = Calculate.multiplication(getDenominator(), right.getDenominator());
        return this;
    }


    public Polynomial multiplication(Polynomial a){
        this.numerator = Calculate.multiplication(numerator,  a.getNumerator());
        this.denominator = Calculate.multiplication(denominator, a.getDenominator());
        return this;
    }

    // 除算
    public Polynomial division(Polynomial a){
        this.numerator = Calculate.multiplication(numerator, a.denominator);
        this.denominator = Calculate.multiplication(denominator, a.numerator);
        return this;
    }

    // 階乗を求める
    public Polynomial factorial(){
        if(numerator.size() != 1 || !numerator.get(0).isInteger() || numerator.get(0).hasVariable()){
            throw new UnsupportedOperationException("正の整数以外の数値の階乗計算には対応していません");
        } else {
            numerator.set(0, Calculate.factorial(numerator.get(0)));
            return this;
        }
    }

    // 累乗を求める
    public Polynomial power(Polynomial a){
        // 現状ではaは唯一つの整数項を持つとし、異なる場合は例外をスロー
        if(a.isDenominatorOne() && a.isNumeratorInteger() && a.isNumeratorPositive()){
            Polynomial tmp = this.clone();

            // aの全ての項に対してb乗を適用する
            // 負数のべき乗はとりあえず仮定しない
            for(int i=a.getNumerator().get(0).getCoefficient(); i>1; i--){
                tmp = tmp.multiplication(this);
            }
            return tmp;
        } else {
            throw new UnsupportedOperationException("現時点ではべき乗演算の右側の多項式は唯1つの整数項を持っていなければなりません");
        }
    }

    // 文字列化
    private StringBuilder printTerms(ArrayList<Term> terms, boolean isThisNumerator){
        StringBuilder tmp = new StringBuilder();

        // カッコを出力する
        if(!isThisNumerator) {
            // 分母が1のときは何も出力しない。1以外のとき除算記号を出力
            if (terms.get(0).isInteger() && terms.get(0).getCoefficient() == 1) {
                return tmp;
            } else {
                tmp.append('/');
            }
        }
        if(terms.size() > 1){
            if(isThisNumerator){
                // 分母が1以外かつ分子が複数項のときカッコを出力
                if(!this.isDenominatorOne()) {
                    tmp.append('(');
                }
            } else {
                tmp.append('(');
            }
        } else if(terms.isEmpty()) {
            // 項がなければ0を出力
            tmp.append("0");
            return tmp;
        }
        // 項を出力していく
        Term t;
        for(int i=0; i < terms.size(); i++){
            t = terms.get(i);
            // 係数が0で変数を持つならば表示しない
            if(t.getCoefficient() == 0 && t.hasVariable()){
                continue;
            }

            // 負数でなければ＋を表示
            if(!t.isNegative() && i > 0){
                tmp.append('+');
            }
            tmp.append(t.toString());
        }
        // 閉じカッコを出力する
        if(terms.size() > 1){
            if(isThisNumerator){
                if(!this.isDenominatorOne()) {
                    tmp.append(')');
                }
            } else if(terms.size() > 1) {
                tmp.append(')');
            }
        }

        return tmp;
    }

    @Override
    public String toString(){
        StringBuilder tmp = new StringBuilder();
        tmp.append(printTerms(numerator, true));
        tmp.append(printTerms(denominator, false));
        return tmp.toString();
    }

    // 複製する
    @Override
    public Polynomial clone(){
        Polynomial tmp = null;
        try {
            tmp = (Polynomial)super.clone();

            // ArrayListのcloneメソッドはシャローコピーしかしてくれないので手作業でディープコピー
            tmp.numerator = new ArrayList<>();
            tmp.denominator = new ArrayList<>();
            for(Term term : this.numerator){
                tmp.numerator.add(term.clone());
            }
            for(Term term : this.denominator){
                tmp.denominator.add(term.clone());
            }
        } catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return tmp;
    }

}
