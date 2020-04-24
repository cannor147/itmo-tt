package expression;

public class Wrapper implements Expression {
    private Expression expression;

    public Wrapper(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
//        return "[" + expression.toString() + "]";
        return expression.toString();
    }
}
