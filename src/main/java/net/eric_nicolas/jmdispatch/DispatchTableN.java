package net.eric_nicolas.jmdispatch;

public class DispatchTableN extends DispatchTableAbstract<FunctorN> {

    public DispatchTableN(int nTypes) {
        super(nTypes, FunctorN.class, new FunctorImplementationBuilderN(nTypes));
        if (nTypes < 2) throw new RuntimeException("DispatchTableN should be used only with nTypes >= 2");
    }

    public DispatchTableN autoregister(Class<?> aclass) {
        return (DispatchTableN) super.autoregister(aclass);
    }

    public void dispatch(Object... values) {
        if (values.length != nTypes)
            throw new RuntimeException("Calling dispatch with " + values.length + " parameters for a dispatch(" + nTypes + ")");

        // null arguments cannot be dispatched
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == null) {
                throw new DispatchNoMatchException("Cannot dispatch on null argument at position " + i);
            }
        }

        // search for exact match, pass-in values to delay the (costly) creation of Class<?>[] types
        FunctorN method = findExact(values);
        if (method == null) {
            // no direct match => search for the closest match
            Class<?>[] types = fromValues(values);
            method = findClosest(keys, functors, types); // throws is not found or ambiguous

            // add to the table for further lookups
            append(types, method);
        }

        // call the found method
        method.f(values);
    }

    private FunctorN findExact(Object[] values) {
        Class<?> type0 = values[0].getClass();
        Class<?> type1 = values[1].getClass();
        outer:
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] != type0) continue;
            if (keys[i][1] != type1) continue;
            for (int t = 2; t < nTypes; ++t) {
                if (keys[i][t] != values[t].getClass()) continue outer;
            }
            return functors[i];
        }
        return null;
    }
}
