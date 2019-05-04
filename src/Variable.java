public class Variable implements Cloneable{
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

    public boolean variableEquals(Variable v){
        return (var == v.getVariable());
    }

    @Override
    public Variable clone(){
        Variable ret = null;
        try {
            ret = (Variable) super.clone();
        } catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
