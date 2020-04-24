import expression.Expression;
import expression.Wrapper;
import parser.Parser;
import reduction.ReductionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task2 {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line;
        long m;
        long k;

        line = reader.readLine();
        try {
            String[] numbers = line.split(" ");
            m = Long.parseLong(numbers[0]);
            k = Long.parseLong(numbers[1]);
            line = reader.readLine();
        } catch (Exception e) {
            m = 10000;
            k = 1;
        }

        Parser parser = new Parser();
        Expression expression = parser.parse(line);
        Wrapper wrapper = new Wrapper(expression);

        System.out.println(wrapper.getExpression());
        for (int i = 1; i <= m; i++) {
            boolean wow = ReductionUtils.reduce(wrapper);
            if ((wow && i % k == 0) || (!wow && (i - 1) % k != 0)) {
                System.out.println(wrapper.getExpression());
            }
            if (!wow) {
                break;
            }
        }
    }
}
