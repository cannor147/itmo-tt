package expression;

public class Application implements Expression {
    private Expression firstExpression;
    private Expression secondExpression;

    public Application(Expression firstExpression, Expression secondExpression) {
        this.firstExpression = firstExpression;
        this.secondExpression = secondExpression;
    }

    public Expression getFirst() {
        return firstExpression;
    }

    public void setFirst(Expression firstExpression) {
        this.firstExpression = firstExpression;
    }

    public Expression getSecond() {
        return secondExpression;
    }

    public void setSecond(Expression secondExpression) {
        this.secondExpression = secondExpression;
    }

    @Override
    public String toString() {
        return "(" + firstExpression.toString() + " " + secondExpression.toString() + ")";
    }
}
