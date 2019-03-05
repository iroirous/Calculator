import java.util.ArrayList;

public class Polynomial implements Cloneable{
    private ArrayList<Term> terms = new ArrayList<>();

    Polynomial(){

    }

    Polynomial(String str){
        terms.add(new Term(str));
    }

    Polynomial(Term term){
        terms.add(term);
    }

    Polynomial(Polynomial poly){
        terms = poly.get();
    }

    // termsの内容を複製して返す
    public ArrayList<Term> get(){
        return terms;
        //return (ArrayList<Term>)terms.clone();
    }

    // termsの内容のうち指定番目を複製して返す
    public Term get(int num){
        return terms.get(num);
    }

    // termsの内容のうち指定番目を置き換える
    public void set(int num, Term replace){
        terms.set(num, replace);
    }

    // termsにTermを追加する
    public void add(Term term){
        terms.add(term);
    }

    // 多項式の項の総数を返す
    public int size(){
        return terms.size();
    }

    // 項をきれいにする
    public Polynomial beautify(){
        for(Term term : terms){
            term.beautify();
        }
        return this;
    }

    // 文字列化
    @Override
    public String toString(){
        StringBuilder tmp = new StringBuilder();

        for(int i=0; i<terms.size(); i++){
            if(tmp.length() > 0){
                if(terms.get(i).isPositive()){
                    tmp.append("+");
                }
            }

            if(!terms.get(i).isZero())
                tmp.append(terms.get(i).toString());
        }

        // まだtmpが空なら0を出力する
        if(tmp.length() == 0)
            tmp.append("0");

        return tmp.toString();
    }

    // 複製する
    @Override
    public Polynomial clone(){
        // return new Term(numerator, denominator);
        Polynomial tmp = null;
        try {
            tmp = (Polynomial)super.clone();

            // ArrayListのcloneメソッドはシャローコピーしかしてくれないので手作業でディープコピー
            tmp.terms = new ArrayList<>();
            for(Term term : this.terms){
                tmp.terms.add(term.clone());
            }
        } catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return tmp;
    }
}
