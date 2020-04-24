import expression.Expression;
import expression.Lambda;
import inference.InferenceNode;
import inference.InferenceUtils;
import inference.Type;
import parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Task3 {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line;

        line = reader.readLine();
        line = line.replace('’', '\'');
        Parser parser = new Parser();
        Expression expression = parser.parse(line);

        InferenceNode inferenceNode = InferenceUtils.infer(expression);
        if (inferenceNode == null) {
            System.out.println("Expression has no type");
        } else {
            Set<String> freeVariables = InferenceUtils.findFreeVariables(expression);
            Map<String, Type> context = InferenceUtils.generateContext(inferenceNode, freeVariables);
            printNode(0, inferenceNode, context);
        }
    }

    private static void printNode(int x, InferenceNode node, Map<String, Type> context) {
        printTabs(x);
        printContext(context);
        System.out.print("|- " + node.getExpression() + " : " + node.getType());
        printRule(node.getChildren().length);
        System.out.println();

        Map<String, Type> c = new HashMap<>();
        if (node.getExpression() instanceof Lambda) {
            Set<String> kek = new HashSet<>();
            kek.add(((Lambda) node.getExpression()).getVariable().getName());
            c = InferenceUtils.generateContext(node, kek);
        }
        for (Map.Entry<String, Type> stringTypeEntry : context.entrySet()) {
            c.put(stringTypeEntry.getKey(), stringTypeEntry.getValue());
        }

        for (InferenceNode child : node.getChildren()) {
            printNode(x + 1, child, c);
        }
    }

    private static void printContext(Map<String, Type> context) {
        String x = context.entrySet().stream()
                .map(stringTypeEntry -> stringTypeEntry.getKey() + " : " + stringTypeEntry.getValue())
                .collect(Collectors.joining(", "));
        if (x.length() > 0) {
            x += " ";
        }
        System.out.print(x);
    }

    private static void printTabs(int x) {
        for (int i = 0; i < x; i++) {
            System.out.print("*   ");
        }
    }

    private static void printRule(int x) {
        int rule;
        if (x == 0) {
            rule = 1;
        } else if (x == 1) {
            rule = 3;
        } else if (x == 2) {
            rule = 2;
        } else {
            rule = 0;
        }
        System.out.print(" [rule #" + rule +"]");
    }
}
