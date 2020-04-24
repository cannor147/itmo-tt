package inference;

import expression.*;

import java.util.*;
import java.util.stream.Collectors;

public class InferenceUtils {
    private static int MEOW = 0;

    public static InferenceNode infer(Expression expression) {
        try {
            List<Equation> equations = new ArrayList<>();
            InferenceNode inferenceNode = createTree(expression, equations, new HashMap<>(), new HashMap<>());
            equations = solve(new ArrayList<>(equations));

            Map<String, Type> mapping = equations.stream().collect(Collectors.toMap(equation -> ((TypeVariable) equation.getLeft()).getName(), Equation::getRight));

            List<InferenceNode> inferenceNodes = new ArrayList<>();
            Queue<InferenceNode> queue = new ArrayDeque<>();
            queue.add(inferenceNode);
            while (!queue.isEmpty()) {
                InferenceNode node = queue.poll();
                inferenceNodes.add(node);
                queue.addAll(Arrays.asList(node.getChildren()));
                queue.addAll(node.getHiddenChildren());
            }

            for (InferenceNode node : inferenceNodes) {
                substituteTypes(mapping, node);
            }
            return inferenceNode;
        } catch (MyException e) {
            return null;
        }
    }

    public static Map<String, Type> generateContext(InferenceNode node, Set<String> freeVariables) {
        Map<String, Type> mapping = new HashMap<>();
        Queue<InferenceNode> queue = new ArrayDeque<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            InferenceNode current = queue.poll();
            Expression expression = current.getExpression();
            if (expression instanceof Variable) {
                String name = ((Variable) expression).getName();
                if (freeVariables.contains(name)) {
                    mapping.put(name, current.getType());
                }
            } else if (expression instanceof Lambda) {
                queue.addAll(Arrays.asList(current.getChildren()));
                queue.addAll(current.getHiddenChildren());
            } else if (expression instanceof Application) {
                queue.addAll(Arrays.asList(current.getChildren()));
            } else {
                throw new RuntimeException("Lol kek");
            }
        }
        return mapping;
    }

    private static void substituteTypes(Map<String, Type> mapping, InferenceNode node) {
        Type type = node.getType();
        if (type instanceof TypeVariable) {
            if (mapping.containsKey(((TypeVariable) type).getName())) {
                node.setType(cloneType(mapping.get(((TypeVariable) type).getName())));
            }
            return;
        }

        Queue<Type> queue = new ArrayDeque<>();
        queue.add(type);
        while (!queue.isEmpty()) {
            Type current = queue.poll();
            if (current instanceof TypeConnection) {
                Type left = ((TypeConnection) current).getLeft();
                if (left instanceof TypeVariable) {
                    if (mapping.containsKey(((TypeVariable) left).getName())) {
                        ((TypeConnection) current).setLeft(cloneType(mapping.get(((TypeVariable) left).getName())));
                    }
                } else {
                    queue.add(left);
                }

                Type right = ((TypeConnection) current).getRight();
                if (right instanceof TypeVariable) {
                    if (mapping.containsKey(((TypeVariable) right).getName())) {
                        ((TypeConnection) current).setRight(cloneType(mapping.get(((TypeVariable) right).getName())));
                    }
                } else {
                    queue.add(right);
                }
            } else {
                throw new RuntimeException("Lol kek");
            }
        }
    }

    private static InferenceNode createTree(Expression expression, List<Equation> equations, Map<String, Type> local, Map<String, Type> global) {
        if (expression instanceof Variable) {
            Variable variable = (Variable) expression;
            Type type;
            if (local.containsKey(variable.getName())) {
                type = cloneType(local.get(variable.getName()));
            } else if (global.containsKey(variable.getName())) {
                type = cloneType(global.get(variable.getName()));
            } else {
                type = generateType();
                global.put(variable.getName(), type);
            }
            return new InferenceNode(expression, type);
        } else if (expression instanceof Lambda) {
            Lambda lambda = (Lambda) expression;
            Type variableType = generateType();
            local.put(lambda.getVariable().getName(), variableType);
            InferenceNode variableNode = new InferenceNode(lambda.getVariable(), variableType);
            InferenceNode expressionNode = createTree(lambda.getExpression(), equations, local, global);
            local.remove(lambda.getVariable().getName());

            Type type = new TypeConnection(variableNode.getType(), expressionNode.getType());
            InferenceNode node = new InferenceNode(expression, type, expressionNode);
            node.addHiddenChild(variableNode);
            return node;
        } else if (expression instanceof Application) {
            Application application = (Application) expression;
            InferenceNode firstNode = createTree(application.getFirst(), equations, local, global);
            InferenceNode secondNode = createTree(application.getSecond(), equations, local, global);

            Type type = generateType();
            equations.add(new Equation(firstNode.getType(), new TypeConnection(secondNode.getType(), type)));
            return new InferenceNode(expression, type, firstNode, secondNode);
        } else {
            throw new RuntimeException("Lol kek");
        }
    }

    private static List<Equation> solve(List<Equation> equations) throws MyException {
        Map<String, List<Type>> substitutions = new HashMap<>();
        boolean changes = true;
        while (changes) {
            TypeVariable substitutionVariable = null;
            Type substitutionType = null;
            changes = false;
            List<Equation> nextGeneration = new ArrayList<>();
            for (Equation equation : equations) {
                Type left = equation.getLeft();
                Type right = equation.getRight();
                if (left instanceof TypeVariable) {
                    if (right instanceof TypeVariable && ((TypeVariable) left).getName().equals(((TypeVariable) right).getName())) {
                        continue;
                    }
                    if (substitutionVariable == null) {
                        List<Type> types = substitutions.computeIfAbsent(((TypeVariable) left).getName(), k -> new ArrayList<>());
                        if (types.stream().noneMatch(type -> type == right)) {
                            substitutionVariable = (TypeVariable) cloneType(left);
                            substitutionType = right;
                            types.add(substitutionType);
                            changes = true;
                        }
                    }

                    checkRecursive((TypeVariable) left, right);
                    nextGeneration.add(equation);
                } else if (right instanceof TypeVariable) {
                    nextGeneration.add(new Equation(right, left));
                    changes = true;
                } else {
                    Type leftLeft = ((TypeConnection) left).getLeft();
                    Type leftRight = ((TypeConnection) left).getRight();
                    Type rightLeft = ((TypeConnection) right).getLeft();
                    Type rightRight = ((TypeConnection) right).getRight();
                    nextGeneration.add(new Equation(leftLeft, rightLeft));
                    nextGeneration.add(new Equation(leftRight, rightRight));
                    changes = true;
                }
            }

            for (Equation equation : nextGeneration) {
                Type equationLeft = equation.getLeft();
                Type equationRight = equation.getRight();
                Deque<Type> stack = new ArrayDeque<>();

                if (equationLeft instanceof TypeVariable) {
                    if (substitutionVariable != null && substitutionVariable.equals(equationLeft)) {
                        if (substitutionType != equationRight) {
                            equation.setLeft(cloneType(substitutionType));
                            changes = true;
                        }
                    }
                } else {
                    stack.addFirst(equationLeft);
                }

                if (equationRight instanceof TypeVariable) {
                    if (substitutionVariable != null && substitutionVariable.equals(equationRight)) {
                        equation.setRight(cloneType(substitutionType));
                        changes = true;
                    }
                } else {
                    stack.addFirst(equationRight);
                }

                while (!stack.isEmpty()) {
                    Type current = stack.pollFirst();
                    if (current instanceof TypeConnection) {
                        Type left = ((TypeConnection) current).getLeft();
                        if (left instanceof TypeVariable) {
                            if (substitutionVariable != null && substitutionVariable.equals(left)) {
                                ((TypeConnection) current).setLeft(cloneType(substitutionType));
                                changes = true;
                            }
                        } else {
                            stack.addFirst(left);
                        }

                        Type right = ((TypeConnection) current).getRight();
                        if (right instanceof TypeVariable) {
                            if (substitutionVariable != null && substitutionVariable.equals(right)) {
                                ((TypeConnection) current).setRight(cloneType(substitutionType));
                                changes = true;
                            }
                        } else {
                            stack.addFirst(right);
                        }
                    } else {
                        throw new RuntimeException("Lol kek");
                    }
                }
            }

            equations = removeDuplicates(nextGeneration);
        }

        return equations;
    }

    private static List<Equation> removeDuplicates(List<Equation> equations) {
        List<Equation> result = new ArrayList<>();
        for (Equation equation : equations) {
            boolean f = true;
            for (Equation eq : result) {
                if (eq.equals(equation)) {
                    f = false;
                    break;
                }
            }
            if (f) {
                result.add(equation);
            }
        }
        return result;
    }

    private static void checkRecursive(TypeVariable typeVariable, Type type) throws MyException {
        Queue<Type> queue = new ArrayDeque<>();
        queue.add(type);
        while (!queue.isEmpty()) {
            Type current = queue.poll();
            if (current instanceof TypeConnection) {
                queue.add(((TypeConnection) current).getLeft());
                queue.add(((TypeConnection) current).getRight());
            } else if (current instanceof TypeVariable) {
                if (typeVariable.getName().equals(((TypeVariable) current).getName())) {
                    throw new MyException("Found recursive dependency.");
                }
            } else {
                throw new RuntimeException("Lol kek");
            }
        }
    }

    private static Type cloneType(Type type) {
        if (type instanceof TypeVariable) {
            return new TypeVariable(((TypeVariable) type).getName());
        } else if (type instanceof TypeConnection) {
            TypeConnection typeConnection = (TypeConnection) type;
            return new TypeConnection(cloneType(typeConnection.getLeft()), cloneType(typeConnection.getRight()));
        } else {
            throw new RuntimeException("Lol kek");
        }
    }

    private static Type generateType() {
        MEOW++;
        return new TypeVariable("t" + MEOW);
    }

    private static class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }

    public static Set<String> findFreeVariables(Expression expression) {
        Set<String> freeVariables = new HashSet<>();
        Deque<String> variables = new ArrayDeque<>();

        Deque<Expression> stack = new ArrayDeque<>();
        stack.addFirst(expression);
        while (!stack.isEmpty()) {
            Expression current = stack.peekFirst();
            if (current instanceof Application) {
                Application application = (Application) current;
                Expression first = application.getFirst();
                stack.addFirst(first);
            } else if (current instanceof Lambda) {
                Lambda lambda = (Lambda) current;
                String name = lambda.getVariable().getName();
                Expression expr = lambda.getExpression();
                variables.addFirst(name);
                stack.addFirst(expr);
            } else if (current instanceof Variable) {
                while (!stack.isEmpty()) {
                    current = stack.pollFirst();
                    if (current instanceof Variable) {
                        if (!variables.contains(((Variable) current).getName())) {
                            freeVariables.add(((Variable) current).getName());
                        }
                    } else if (current instanceof Lambda) {
                        variables.pollFirst();
                    }
                    Expression parent = stack.peekFirst();
                    if (parent instanceof Application && ((Application) parent).getFirst() == current) {
                        Expression second = ((Application) parent).getSecond();
                        stack.addFirst(second);
                        break;
                    }
                }
            } else {
                throw new IllegalStateException("Lol kek");
            }
        }
        return freeVariables;
    }
}
