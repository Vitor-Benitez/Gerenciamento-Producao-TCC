package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class AppTest {


    @Test
    public void testIsPrime() {
        NumberUtil util = new NumberUtil();
        
        assertTrue(util.isPrime(2), "2 deveria ser considerado primo");
        assertTrue(util.isPrime(13), "13 deveria ser considerado primo");
        assertFalse(util.isPrime(1), "1 não é considerado primo");
        assertFalse(util.isPrime(10), "10 não é considerado primo");
    }


    @Test
    public void testReverseString() {
        StringUtil util = new StringUtil();
        
        assertEquals("odnum", util.reverse("mundo"), "Erro ao reverter 'mundo'");
        assertEquals("aralA", util.reverse("Alara"), "Erro ao reverter 'Alara'");
        assertEquals("", util.reverse(""), "Erro ao reverter string vazia");
    }


    @Test
    public void testDiscountCalculator() {
        DiscountCalculator calc = new DiscountCalculator();
        
        assertEquals(90.0, calc.calculateDiscount(100.0, 10.0), 0.001, "10% de 100 deveria ser 90");
        assertEquals(50.0, calc.calculateDiscount(100.0, 50.0), 0.001, "50% de 100 deveria ser 50");
        assertEquals(100.0, calc.calculateDiscount(100.0, 0.0), 0.001, "0% de 100 deveria ser 100");
    }
}


class NumberUtil {
    public boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}

class StringUtil {
    public String reverse(String text) {
        return new StringBuilder(text).reverse().toString();
    }
}

class DiscountCalculator {
    public double calculateDiscount(double value, double discountPercent) {
        return value - (value * discountPercent / 100);
    }
}
