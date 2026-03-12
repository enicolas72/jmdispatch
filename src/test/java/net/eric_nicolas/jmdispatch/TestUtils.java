package net.eric_nicolas.jmdispatch;

import org.junit.Assert;

public class TestUtils {

    static void assertEquals(String expected, String result) {
        expected = expected.trim().replaceAll("\\R", "\n");
        result = result.trim().replaceAll("\\R", "\n");
        Assert.assertEquals(expected, result);
    }

    public static class A {
        int a;
        A(int a) {
            this.a = a;
        }
    }
    public static class B extends A {
        int b;
        B(int a, int b) {
            super(a);
            this.b = b;
        }
    }
    public static class C extends B {
        int c;
        C(int a, int b, int c) {
            super(a, b);
            this.c = c;
        }
    }
    public static class X {
        int x;
        X(int x) {
            this.x = x;
        }
    }
    public static class Y extends X {
        int y;
        Y(int x, int y) {
            super(x);
            this.y = y;
        }
    }
}
