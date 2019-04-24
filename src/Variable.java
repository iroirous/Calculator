public class Variable {
    private char var;
    private int exp;

    Variable(char v, int e){
        var = v;
        exp = e;
    }

    public char getVariable(){
        return var;
    }

    public int getExponent(){
        return exp;
    }

    public void setExponent(int num){
        exp = num;
    }

    @Override
    public Variable clone(){
        return new Variable(var, exp);
    }
}
