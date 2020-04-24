package inference;

public class TypeConnection implements Type {
    private Type left;
    private Type right;

    public TypeConnection(Type left, Type right) {
        this.left = left;
        this.right = right;
    }

    public Type getLeft() {
        return left;
    }

    public void setLeft(Type left) {
        this.left = left;
    }

    public Type getRight() {
        return right;
    }

    public void setRight(Type right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeConnection) {
            TypeConnection typeConnection = (TypeConnection) obj;
            return this.getLeft().equals(typeConnection.getLeft()) && this.getRight().equals(typeConnection.getRight());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " -> " + right.toString() + ")";
    }
}
