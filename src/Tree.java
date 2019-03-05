public class Tree {
    private Tree left, right = null;
    private String item;

    Tree(String s){
        item = s;
    }

    Tree(Tree l, String s){
        left = l;
        item = s;
    }

    Tree(Tree l, Tree r, String s){
        left = l;
        right = r;
        item = s;
    }

    public String getItem(){
        return item;
    }

    public Tree getLeft(){
        return left;
    }

    public Tree getRight(){
        return right;
    }

    public boolean hasChildren(){
        if(left != null && right != null){
            return true;
        } else {
            return false;
        }
    }

    public boolean hasLeft(){
        if(left != null){
            return true;
        } else {
            return false;
        }
    }
}
