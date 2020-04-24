package inference;

import java.util.HashMap;

public class Equation {
    private Type left;
    private Type right;

    public Equation(Type left, Type right) {
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
        if (obj instanceof Equation) {
            Equation equation = (Equation) obj;
            return this.getLeft().equals(equation.getLeft()) && this.getRight().equals(equation.getRight());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + getLeft().toString() + " = " + getRight().toString() + "]";
    }
}
