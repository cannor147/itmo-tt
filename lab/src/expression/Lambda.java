package expression;

public class Lambda implements Expression {
    private Variable variable;
    private Expression expression;

    public Lambda(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(\\" + variable.toString() + "" + expression.toString() + ")";
    }
}
