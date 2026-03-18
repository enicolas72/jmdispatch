package net.eric_nicolas.jmdispatch;

import org.junit.Assert;

public class TestUtils {

    public static void assertEquals(String expected, String result) {
        expected = expected.trim().replaceAll("\\R", "\n");
        result = result.trim().replaceAll("\\R", "\n");
        Assert.assertEquals(expected, result);
    }

    public static class A {
        public int a;
        public A(int a) {
            this.a = a;
        }
    }
    public static class B extends A {
        public int b;
        public B(int a, int b) {
            super(a);
            this.b = b;
        }
    }
    public static class C extends B {
        public int c;
        public C(int a, int b, int c) {
            super(a, b);
            this.c = c;
        }
    }
    public static class X {
        public int x;
        public X(int x) {
            this.x = x;
        }
    }
    public static class Y extends X {
        public int y;
        public Y(int x, int y) {
            super(x);
            this.y = y;
        }
    }
}
