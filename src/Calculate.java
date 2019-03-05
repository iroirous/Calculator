import java.util.ArrayList;

public class Calculate {
    // 項を加算
    private static Term addition(Term a, Term b) {
        int orgdenom = a.getDenominator();
        int bdenom = b.getDenominator();
        a.setDenominator(lcm(orgdenom, bdenom));
        int adenom = a.getDenominator();
        a.setNumerator((a.getNumerator() * (adenom / orgdenom))
                + (b.getNumerator() * (adenom / bdenom)));
        return a;
    }

    private static Polynomial addition(Polynomial a, Term b){
        ArrayList<Term> tmp = a.get();
        for(int i=0; i<tmp.size(); i++) {
            if(tmp.get(i).variableEquals(b)){
                // 同じ変数を持つ項があれば加算
                tmp.set(i, addition(tmp.get(i), b));
                return a;
            }
        }

        // 同じ変数を持つ項が無ければaに追加
        a.add(b);
        return a;
    }

    // 多項式を加算する
    public static Polynomial addition(Polynomial a, Polynomial b){
        // 多項式aと多項式bの中に同じ変数を持つ項があればそれらを足し合わせる
        // 多項式aに同じ変数を持つ項が無ければ、tmpにその項を追加する
        Term atmp, btmp;

        outer : for(int bcount = 0; bcount<b.size(); bcount++){
            btmp = b.get(bcount);
            for(int tmpcount = 0; tmpcount<a.size(); tmpcount++){
                atmp = a.get(tmpcount);
                // 同じ変数を持つ項ならば足し合わせる
                if(atmp.variableEquals(btmp)){
                    a.set(tmpcount, addition(atmp, btmp));
                    continue outer;
                }
            }

            // 同じ変数を持つ項が無ければ、tmpにその項を追加する
            a.add(btmp);
        }

        return a;
    }

    // 減算
    private static Term subtraction(Term a, Term b) {
        int orgdenom = a.getDenominator();
        int bdenom = b.getDenominator();
        a.setDenominator(lcm(orgdenom, bdenom));
        int adenom = a.getDenominator();
        a.setNumerator((a.getNumerator() * (adenom / orgdenom))
                - (b.getNumerator() * (adenom / bdenom)));
        return a;
    }

    // 多項式を減算する
    public static Polynomial subtraction(Polynomial a, Polynomial b){
        // 多項式aと多項式bの中に同じ変数を持つ項があればそれらを足し合わせる
        // 多項式aに同じ変数を持つ項が無ければ、tmpにその項を追加する
        Term atmp, btmp;

        outer : for(int bcount = 0; bcount<b.size(); bcount++){
            btmp = b.get(bcount);
            for(int tmpcount = 0; tmpcount<a.size(); tmpcount++){
                atmp = a.get(tmpcount);
                // 同じ変数を持つ項ならば足し合わせる
                if(atmp.variableEquals(btmp)){
                    a.set(tmpcount, subtraction(atmp, btmp));
                    continue outer;
                }
            }

            // 同じ変数を持つ項が無ければ、tmpにその項を追加する
            a.add(btmp.inverse());
        }

        return a;
    }

    // 乗算
    private static Term multiplication(Term a, Term b){
        int denominator = a.getDenominator() * b.getDenominator();
        int numerator = a.getNumerator() * b.getNumerator();
        ArrayList<Object[]> avar = a.getVariable();

        // 文字の指数を変更する
        outer : for(int i=0; i<b.variableSize(); i++){
            Object[] btmp = b.getVariable(i);
            for(int j=0; j<avar.size(); j++){
                // 同じ変数が見つかれば指数を加算する
                if(btmp[0].equals(avar.get(j)[0])){
                    avar.get(j)[1] = (int)btmp[1] + (int)avar.get(j)[1];
                    continue outer;
                }
            }

            // 同じ変数が見つからなければ変数を追加する
            avar.add(btmp.clone());
        }

        return new Term(numerator, denominator, avar);
    }

    public static Polynomial multiplication(Polynomial a, Polynomial b){
        // bが持つ項を順に見ていき、aが持つ項全てに掛ける
        Polynomial tmp = new Polynomial();
        Term mul;

        for(Term aTerm : a.get()){
            for(Term bTerm : b.get()){
                mul = multiplication(aTerm, bTerm);
                tmp = Calculate.addition(tmp, mul);
            }
        }

        return tmp;
    }

    // 除算
    private static Term division(Term a, Term b){
        int denominator = a.getDenominator() * b.getNumerator();
        int numerator = a.getNumerator() * b.getDenominator();
        ArrayList<Object[]> avar = a.getVariable();
        Object[] btmp;

        outer : for(int i=0; i<b.variableSize(); i++){
            btmp = b.getVariable(i);
            for(int j=0; j<avar.size(); j++){
                // 同じ変数が見つかれば指数を減算する
                if(btmp[0].equals(avar.get(j)[0])){
                    avar.get(j)[1] = (int)avar.get(j)[1] - (int)btmp[1];
                    continue outer;
                }
            }

            // 同じ変数が見つからなければ変数を追加する
            btmp = btmp.clone();
            btmp[1] = -(int)btmp[1];
            avar.add(btmp);
        }

        return new Term(numerator, denominator, avar);
    }

    public static Polynomial division(Polynomial a, Polynomial b){
        // 現状では割る多項式bは唯一つの項を持つとし、異なる場合は例外をスロー
        if(b.size() == 1){
            Polynomial tmp = new Polynomial();

            // aの全ての項に対して1/bを掛ける
            // 負数のべき乗はとりあえず仮定しない
            Term div;
            for(Term aTerm : a.get()){
                div = division(aTerm, b.get(0));
                tmp = Calculate.addition(tmp, div);
            }

            return tmp;
        } else {
            throw new UnsupportedOperationException("現時点では除算演算子の右側の多項式は唯1つの整数項を持っていなければなりません");
        }
    }

    // べき乗計算
    public static Term power(Term a, Term b){
        if(!b.isInteger()){
            throw new UnsupportedOperationException("べき乗演算子の右側の項に利用できるのは正の整数のみです");
        } else {
            int count = b.getNumerator();
            Term tmp = a.clone();
            while(count > 1){
                tmp = Calculate.multiplication(tmp, a);
                count--;
            }
            return tmp;
        }
    }

    public static Polynomial power(Polynomial a, Polynomial b){
        // 現状ではbは唯1つの整数項を持つとし、異なる場合は例外をスロー
        if(b.size() == 1 && !b.get(0).hasVariable() && b.get(0).isInteger()){
            Polynomial tmp = a.clone();

            // aの全ての項に対してb乗を適用する
            // 負数のべき乗はとりあえず仮定しない
            for(int i=b.get(0).getNumerator(); i>1; i--){
                tmp = Calculate.multiplication(tmp, a);
            }

            return tmp;
        } else {
            throw new UnsupportedOperationException("現時点ではべき乗演算の右側の多項式は唯1つの整数項を持っていなければなりません");
        }
    }

    // 項の符号反転
    private static Term signInversion(Term a){
        a.setNumerator(-a.getNumerator());
        return a;
    }

    // 多項式の符号反転
    public static Polynomial signInversion(Polynomial a){
        for(Term term : a.get()){
            signInversion(term);
        }
        return a;
    }

    // 階乗計算
    private static Term factorial(Term a){
        if(!a.isInteger() || a.isNegative() || a.hasVariable()){
            throw new UnsupportedOperationException("正の整数以外の数値の階乗計算には対応していません");
        } else {
            if(a.getNumerator() == 0){ return new Term(1); }

            int fact = a.getNumerator();
            for(int i=a.getNumerator()-1; i>1; i--){
                fact *= i;
            }
            return new Term(fact);
        }
    }

    public static Polynomial factorial(Polynomial a){
        if(a.size() != 1 || !a.get(0).isInteger() || a.get(0).hasVariable()){
            throw new UnsupportedOperationException("正の整数以外の数値の階乗計算には対応していません");
        } else {
            Term tmp = factorial(a.get(0));
            return new Polynomial(tmp);
        }
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
}
