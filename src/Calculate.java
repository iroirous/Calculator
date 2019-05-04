import java.util.ArrayList;

public class Calculate {
    // 最大公約数を求める
    public static int gcd(int m, int n){
        if(m % n == 0){
            return n;
        } else {
            return gcd(n, m%n);
        }
    }

    public static int gcd(ArrayList<Integer> arg){
        int result = arg.get(0);
        if(arg.size() > 1){
            for(int i=1; i<arg.size(); i++){
                result = gcd(result, arg.get(i));
            }
        }
        return result;
    }

    private static ArrayList<Term> signInversion(ArrayList<Term> a){
        ArrayList<Term> result = new ArrayList<>();
        for(Term term : a){
            result.add(term.signInversion());
        }
        return result;
    }

    public static ArrayList<Term> addition(ArrayList<Term> a, Term b){
        ArrayList<Term> result = new ArrayList<>();

        // aをresultにコピー
        for(Term term : a){
            result.add(term.clone());
        }

        // resultが空ならbを追加して終わる
        if(result.size() == 0){
            result.add(b.clone());
            return result;
        }

        // 同じ変数をもつtermが存在すれば加算して、なければ新たに追加する
        for(int i=0; i<result.size(); i++){
            if(result.get(i).variableEquals(b)){
                result.get(i).setCoefficient(result.get(i).getCoefficient() + b.getCoefficient());
                break;
            }

            if(i == a.size()-1){
                result.add(b.clone());
                break;
            }
        }

        return result;
    }

    // 加算
    public static ArrayList<Term> addition(ArrayList<Term> a, ArrayList<Term> b){
        ArrayList<Term> result = new ArrayList<>();

        // thisのnumeratorをコピー
        for(Term term : a){
            result.add(term.clone());
        }

        // 加算する
        outer : for(Term termB : b) {
            for (Term term : result) {
                // 同じ変数ならば係数を変更
                if (termB.variableEquals(term)) {
                    term.setCoefficient(term.getCoefficient() + termB.getCoefficient());
                    continue outer;
                }
            }

            result.add(termB);
        }

        return result;
    }

    // 減算
    public static ArrayList<Term> subtraction(ArrayList<Term> a, ArrayList<Term> b){
        return addition(a, signInversion(b));
    }

    // 乗算
    public static ArrayList<Term> multiplication(ArrayList<Term> a, ArrayList<Term> b){
        ArrayList<Term> result = new ArrayList<>();
        for(Term aTerm : a){
            for(Term bTerm : b) {
                result.add(aTerm.multiplication(bTerm));
            }
        }
        return result;
    }

    // 階乗計算
    public static Term factorial(Term a){
        if(!a.isInteger() || a.isNegative() || a.hasVariable()){
            throw new UnsupportedOperationException("正の整数以外の数値の階乗計算には対応していません");
        } else {
            if(a.getCoefficient() == 0){ return new Term(1); }

            int fact = a.getCoefficient();
            for(int i=a.getCoefficient()-1; i>1; i--){
                fact *= i;
            }
            return new Term(fact);
        }
    }
}
