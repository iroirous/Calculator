import java.util.ArrayList;

public class Term implements Cloneable{
    private int coefficient;
    private ArrayList<Variable> variables = new ArrayList<>();

    Term(int coe){
        coefficient = coe;
    }

    Term(int coe, ArrayList<Variable> var){
        coefficient = coe;
        variables = var;
    }

    Term(String term){
        StringBuilder tmp = new StringBuilder();
        char var = 0;
        char c;

        // 一文字ずつ取り出す
        for(int i=0; i<term.length(); i++){
            c = term.charAt(i);

            if(Character.isDigit(c)){
                // 数字ならばtmpに移動
                tmp.append(c);
            } else if(Character.isAlphabetic(c)){
                if(var == 0){
                    // 係数をcoefficientに代入
                    if(tmp.length() > 0){
                        coefficient = Integer.valueOf(tmp.toString());
                        tmp.setLength(0);
                    } else {
                        // 係数が明示されていなければ1とする
                        coefficient = 1;
                    }
                } else {
                    // 既にチェック済みの変数があれば、それを追加する
                    if(tmp.length() == 0){
                        variables.add(new Variable(var, 1));
                    } else {
                        variables.add(new Variable(var, Integer.valueOf(tmp.toString())));
                    }
                    tmp.setLength(0);
                }

                // 現在チェック中の変数名として格納
                var = c;
            }
        }

        // 一時変数に入っている中身を処理
        if(var == 0){
            coefficient = Integer.valueOf(tmp.toString());
        } else {
            // 既にチェック済みの変数があれば、それを追加する
            if(tmp.length() == 0){
                variables.add(new Variable(var, 1));
            } else {
                variables.add(new Variable(var, Integer.valueOf(tmp.toString())));
            }
        }
    }

    // 符号反転
    public Term signInversion(){
        coefficient = -coefficient;
        return this;
    }

    // 係数取得
    public int getCoefficient(){
        return coefficient;
    }

    // 係数設定
    public void setCoefficient(int num){
        coefficient = num;
    }

    // 乗算
    public Term multiplication(Term a){
        int coe = coefficient * a.getCoefficient();
        ArrayList<Variable> newvar = new ArrayList<>();

        // thisの変数をコピーする
        for(Variable var : this.variables){
            newvar.add(var.clone());
        }

        // aの変数を乗算する
        outer : for(Variable vara : a.variables){
            for(Variable varnew : newvar){
                if(vara.variableEquals(varnew)){
                    varnew.setExponent(vara.getExponent() + varnew.getExponent());
                    continue outer;
                }
            }

            newvar.add(vara);
        }

        return new Term(coe, newvar);
    }

    // 符号を反転させる
    public Term inverse(){
        // this.numerator = -this.numerator;
        this.coefficient = -this.coefficient;
        return this;
    }

    // 0乗の変数を削除する
    private void shortify() {
        // 0乗の変数を削除する
        ArrayList<Variable> shortified = new ArrayList<>();
        for (Variable obj : variables) {
            if (obj.getExponent() != 0)
                shortified.add(obj);
        }
        variables = shortified;
    }

    // 変数を並び替える
    private void sortVariables(){
        // コムソートによりソートする。計算時間はほぼO(nlogn)
        if(variables.size() > 1) {
            int h = variables.size() * 10 / 13;
            char a, b;
            while (true) {
                for (int i = 0; i + h < variables.size(); i++) {
                    a = variables.get(i).getVariable();
                    b = variables.get(i+h).getVariable();

                    if (a > b) {
                        Variable tmp = variables.get(i + h);
                        variables.set(i + h, variables.get(i));
                        variables.set(i, tmp);
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

    // Shortify, sortVariablesを全て行う
    public void beautify(){
        sortVariables();
        shortify();
    }

    // 変数の数を返す
    private int variableSize(){
        return variables.size();
    }

    // 指定番目の変数を取り出す
    public Variable getVariable(int num){
        return variables.get(num);
    }

    public Variable getVariable(char c){
        for(Variable v : variables){
            if(v.getVariable() == c){
                return v;
            }
        }
        throw new Error("引数に指定された変数が存在しないため、取得できません。");
    }

    // 指定した変数の指数を書き換える
    public void setVariableExponent(char c, int exp){
        for(Variable v : variables){
            if(v.getVariable() == c){
                v.setExponent(exp);
            }
        }
    }

    // 変数を持っているかどうか
    public boolean hasVariable(){
        return (variables.size() != 0);
    }

    // 同じ変数を持っているかどうか
    public boolean variableEquals(Term a){
        if(variables.size() == a.variableSize()){
            outer : for(Variable var : variables){
                for(Variable avar : a.variables){
                    // 両方の項が同じ変数を持っている場合
                    if((var.getVariable() == avar.getVariable()) && (var.getExponent() == avar.getExponent())){
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
        // return (denominator == 1);
        return (variableSize() == 0);
    }

    // この項が負数かどうか
    public boolean isNegative(){
        return (coefficient < 0);
    }

    // 複製する
    @Override
    public Term clone(){
        Term tmp = null;
        try {
            tmp = (Term)super.clone();

            // ArrayListのcloneメソッドはシャローコピーしかしてくれないので手作業でディープコピー
            tmp.variables = new ArrayList<>();
            for(Variable obj : this.variables){
                tmp.variables.add(obj.clone());
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

        if(variables.size() == 0){
            tmp.append(coefficient);
        } else {
            switch(coefficient) {
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
                    tmp.append(coefficient);
                    break;
            }

            // この項が持つ指数が正の数の変数を順に出力する
            for(Variable var : variables) {
                if (var.getExponent() < 0) {
                    // マイナスｎ乗の変数があったら分母に出力するためフラグを立てる
                    flg = true;
                } else if (var.getExponent() != 1) {
                    // 1乗以外の変数があったら分子に出力する
                    tmp.append(var.getVariable());
                    tmp.append("^");
                    tmp.append(var.getExponent());
                } else {
                    tmp.append(var.getVariable());
                }

            }

        }

        return tmp.toString();
    }

    public ArrayList<Variable> getVariable(){
        // ArrayListのcloneメソッドはシャローコピーなので手作業でディープコピー
        ArrayList<Variable> tmp = new ArrayList<>();
        for(Variable obj : variables){
            tmp.add(obj.clone());
        }
        return tmp;
    }
}
