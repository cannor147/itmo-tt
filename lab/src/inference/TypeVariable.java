package inference;

public class TypeVariable implements Type {
    private String name;

    public TypeVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) obj;
            return this.getName().equals(typeVariable.getName());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
