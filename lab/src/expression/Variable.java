package expression;

public class Variable implements Expression {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(Variable v) {
        return name.equals(v.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
