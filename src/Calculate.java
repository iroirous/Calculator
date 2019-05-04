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
        for(Term term : a){
            term.signInversion();
        }
        return a;
    }

    public static ArrayList<Term> addition(ArrayList<Term> a, Term b){
        if(a.size() == 0){
            a.add(b);
            return a;
        }

        for(int i=0; i<a.size(); i++){
            if(a.get(i).variableEquals(b)){
                a.get(i).setCoefficient(a.get(i).getCoefficient() + b.getCoefficient());
                break;
            }

            if(i == a.size()-1){
                a.add(b);
                break;
            }
        }
        return a;
    }

    // 加算
    public static ArrayList<Term> addition(ArrayList<Term> a, ArrayList<Term> b){
        for(Term term : b){
            a = Calculate.addition(a, term);
        }
        return a;
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
