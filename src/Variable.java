public class Variable implements Cloneable{
    private char var;
    private int exp;
    //public static long count = 0;

    Variable(char v, int e){
        var = v;
        exp = e;
        //count++;
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
        Variable ret = null;
        try {
            ret = (Variable) super.clone();
        } catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
