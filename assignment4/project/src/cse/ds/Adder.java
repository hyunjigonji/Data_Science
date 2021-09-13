package cse.ds;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class Adder {
    public String add(String expr) {
        String[] exprSplit = expr.split("+");
        String input1 = exprSplit[0], input2 = exprSplit[1];

        Integer maxLength = Math.max(input1.length(), input2.length());

        ArrayList<Integer> upper = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0 ; i < maxLength ; i++){
            upper.add(0);
            result.add(0);
        }
        for(int i = maxLength-1; i >= 0 ; i--){
            Integer input1Num = input1.charAt(i)-'0', input2Num = input2.charAt(i)-'0';

        }


        return null;
    }
}

class AdderTest {
    private final Adder adder = new Adder();

    @Test
    public void addSimple() {
        String expr = "111+223";

        String answer = adder.add(expr);

        assertEquals("334", answer);
    }

    @Test
    public void add999() {
        String expr = "123+77";

        String answer = adder.add(expr);

        assertEquals("200", answer);
    }

    @Test
    public void veryBigNumber() {
        String expr = "9999999999999999999999999999999999999999999999999999999999999999+1";

        String answer = adder.add(expr);

        assertEquals("10000000000000000000000000000000000000000000000000000000000000000", answer);
    }
}
