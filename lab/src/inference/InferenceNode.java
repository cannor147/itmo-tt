package inference;

import expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class InferenceNode {
    private final Expression expression;
    private final InferenceNode[] children;
    private final List<InferenceNode> hiddenChildren;
    private Type type;

    public InferenceNode(Expression expression, Type type, InferenceNode... children) {
        this.expression = expression;
        this.children = children;
        this.hiddenChildren = new ArrayList<>();
        this.type = type;
    }

    public Expression getExpression() {
        return expression;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public InferenceNode[] getChildren() {
        return children;
    }

    public void addHiddenChild(InferenceNode inferenceNode) {
        hiddenChildren.add(inferenceNode);
    }

    public List<InferenceNode> getHiddenChildren() {
        return hiddenChildren;
    }
}
