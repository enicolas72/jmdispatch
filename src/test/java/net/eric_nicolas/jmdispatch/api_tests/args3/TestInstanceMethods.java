package net.eric_nicolas.jmdispatch.api_tests.args3;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable;
import org.junit.Assert;
import org.junit.Test;

import net.eric_nicolas.jmdispatch.TestUtils.*;

public class TestInstanceMethods {

    public static class Handler3 {
        private final String tag;

        Handler3(String tag) {
            this.tag = tag;
        }

        @Dispatch
        public String handle(A a, X x, A a2) {
            return tag + ":A-X-A";
        }

        @Dispatch
        public String handle(B b, Y y, B b2) {
            return tag + ":B-Y-B";
        }
    }

    @Test
    public void testInstanceDispatchN() {
        DispatchTable table = DispatchTable.factory(3).autoregister(new Handler3("H"));

        Object r1 = table.dispatch(new A(1), new X(2), new A(3));
        Assert.assertEquals("H:A-X-A", r1);

        Object r2 = table.dispatch(new B(1, 2), new Y(3, 4), new B(5, 6));
        Assert.assertEquals("H:B-Y-B", r2);
    }

    @Test
    public void testInstanceDispatchNFallback() {
        DispatchTable table = DispatchTable.factory(3).autoregister(new Handler3("H"));

        // B,X,B => fallback to A,X,A handler
        Object r = table.dispatch(new B(1, 2), new X(3), new B(4, 5));
        Assert.assertEquals("H:A-X-A", r);
    }
}
