package reduction;

import expression.*;

import java.util.*;

public class ReductionUtils {
    private static int MEOW = 0;

    public static boolean reduce(Wrapper main) {
        Deque<Expression> stack = new ArrayDeque<>();
        stack.addFirst(main);
        while (!stack.isEmpty()) {
            Expression current = stack.peekFirst();
            if (current instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) current;
                Expression expr = wrapper.getExpression();
                if (isGoodApplication(expr)) {
                    wrapper.setExpression(transform((Application) expr));
                    return true;
                }
                stack.addFirst(expr);
            } else if (current instanceof Application) {
                Application application = (Application) current;
                Expression first = application.getFirst();
                if (isGoodApplication(first)) {
                    application.setFirst(transform((Application) first));
                    return true;
                }
                stack.addFirst(first);
            } else if (current instanceof Lambda) {
                Lambda lambda = (Lambda) current;
                Expression expr = lambda.getExpression();
                if (isGoodApplication(expr)) {
                    lambda.setExpression(transform((Application) expr));
                    return true;
                }
                stack.addFirst(expr);
            } else if (current instanceof Variable) {
                while (!stack.isEmpty()) {
                    current = stack.pollFirst();
                    Expression parent = stack.peekFirst();
                    if (parent instanceof Application && ((Application) parent).getSecond() != current) {
                        Expression second = ((Application) parent).getSecond();
                        if (isGoodApplication(second)) {
                            ((Application) parent).setSecond(transform((Application) second));
                            return true;
                        }
                        stack.addFirst(second);
                        break;
                    }
                }
            } else {
                throw new IllegalStateException("Lol kek");
            }
        }
        return false;
    }

    private static boolean isGoodApplication(Expression expression) {
        if (!(expression instanceof Application)) {
            return false;
        }

        Expression first = ((Application) expression).getFirst();
        while (first instanceof Wrapper) {
            first = ((Wrapper) first).getExpression();
        }

        return first instanceof Lambda;
    }

    private static void clear(Expression expression) {
        Deque<Expression> stack = new ArrayDeque<>();
        stack.addFirst(expression);
        while (!stack.isEmpty()) {
            Expression current = stack.peekFirst();
            if (current instanceof Application) {
                Application application = (Application) current;
                Expression first = application.getFirst();
                while (first instanceof Wrapper) {
                    application.setFirst(smartClone((Wrapper) first));
                    first = application.getFirst();
                }
                stack.addFirst(application.getFirst());
            } else if (current instanceof Lambda) {
                Lambda lambda = (Lambda) current;
                Expression expr = lambda.getExpression();
                while (expr instanceof Wrapper) {
                    lambda.setExpression(smartClone((Wrapper) expr));
                    expr = lambda.getExpression();
                }
                stack.addFirst(lambda.getExpression());
            } else if (current instanceof Variable) {
                while (!stack.isEmpty()) {
                    current = stack.pollFirst();
                    Expression parent = stack.peekFirst();
                    if (parent instanceof Application && ((Application) parent).getFirst() == current) {
                        Expression second = ((Application) parent).getSecond();
                        while (second instanceof Wrapper) {
                            ((Application) parent).setSecond(smartClone((Wrapper) second));
                            second = ((Application) parent).getSecond();
                        }
                        stack.addFirst(((Application) parent).getSecond());
                        break;
                    }
                }
            } else {
                throw new IllegalStateException("Lol kek");
            }
        }
    }

    private static Expression smartClone(Wrapper wrapper) {
        return smartCloneImpl(wrapper.getExpression());
    }

    private static Expression smartCloneImpl(Expression expression) {
        if (expression instanceof Variable) {
            Variable variable = (Variable) expression;
            return new Variable(variable.getName());
        } else if (expression instanceof Lambda) {
            Lambda lambda = (Lambda) expression;
            Variable variable = new Variable(lambda.getVariable().getName());
            return new Lambda(variable, smartCloneImpl(lambda.getExpression()));
        } else if (expression instanceof Application) {
            Application application = (Application) expression;
            Expression first = smartCloneImpl(application.getFirst());
            Expression second = smartCloneImpl(application.getSecond());
            return new Application(first, second);
        } else {
            return expression;
        }
    }

    private static String findFreeName(Set<String> names, Map<String, String> mapping) {
        while (true) {
            MEOW++;
            String newName = "v" + MEOW;
            if (!names.contains(newName) && !mapping.containsValue(newName)) {
                return newName;
            }
        }
    }

    private static Expression transform(Application expression) {
        Expression first = expression.getFirst();
        Expression second = expression.getSecond();
        while (first instanceof Wrapper) {
            first = smartClone((Wrapper) first);
        }
        clear(first);

        renameVariables(first, findVariables(second), new HashMap<>());

        Lambda lambda = (Lambda) first;
        Wrapper wrapper = new Wrapper(second);
        return substitute(lambda.getExpression(), lambda.getVariable(), wrapper);
    }

    private static Expression substitute(Expression expression, Variable target, Wrapper replacement) {
        if (expression instanceof Variable) {
            if (((Variable) expression).equals(target)) {
                return replacement;
            } else {
                return expression;
            }
        } else if (expression instanceof Lambda) {
            if (!((Lambda) expression).getVariable().equals(target)) {
                Expression expr = substitute(((Lambda) expression).getExpression(), target, replacement);
                ((Lambda) expression).setExpression(expr);
            }
            return expression;
        } else if (expression instanceof Application) {
            Expression first = substitute(((Application) expression).getFirst(), target, replacement);
            Expression second = substitute(((Application) expression).getSecond(), target, replacement);
            ((Application) expression).setFirst(first);
            ((Application) expression).setSecond(second);
            return expression;
        } else {
            throw new IllegalStateException("Lol kek");
        }
    }

    public static Set<String> findVariables(Expression expression) {
        Set<String> names = new HashSet<>();

        Queue<Expression> queue = new ArrayDeque<>();
        queue.add(expression);
        while (!queue.isEmpty()) {
            Expression current = queue.poll();
            if (current instanceof Application) {
                Application application = (Application) current;
                queue.add(application.getFirst());
                queue.add(application.getSecond());
            } else if (current instanceof Lambda) {
                Lambda lambda = (Lambda) current;
                names.add(lambda.getVariable().getName());
                queue.add(lambda.getExpression());
            } else if (current instanceof Variable) {
                names.add(((Variable) current).getName());
            } else if (current instanceof Wrapper) {
                queue.add(((Wrapper) current).getExpression());
            } else {
                throw new IllegalStateException("Lol kek");
            }
        }
        return names;
    }

    public static void renameVariables(Expression main, Set<String> names, Map<String, String> mapping) {
        Deque<String> variables = new ArrayDeque<>();

        Deque<Expression> stack = new ArrayDeque<>();
        stack.addFirst(main);
        while (!stack.isEmpty()) {
            Expression current = stack.peekFirst();
            if (current instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) current;
                Expression expr = wrapper.getExpression();
                stack.addFirst(expr);
            } else if (current instanceof Application) {
                Application application = (Application) current;
                Expression first = application.getFirst();
                stack.addFirst(first);
            } else if (current instanceof Lambda) {
                Lambda lambda = (Lambda) current;
                String name = lambda.getVariable().getName();
                Expression expr = lambda.getExpression();
                if (mapping.get(name) != null) {
                    lambda.getVariable().setName(mapping.get(name));
                } else {
                    String newName = (names.contains(name)) ? findFreeName(names, mapping) : name;
                    lambda.getVariable().setName(newName);
                    mapping.put(name, newName);
                }
                variables.addFirst(name);
                stack.addFirst(expr);
            } else if (current instanceof Variable) {
                while (!stack.isEmpty()) {
                    current = stack.pollFirst();
                    if (current instanceof Variable) {
                        if (variables.contains(((Variable) current).getName())) {
                            ((Variable) current).setName(mapping.get(((Variable) current).getName()));
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
    }
}
