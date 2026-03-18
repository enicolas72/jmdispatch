package net.eric_nicolas.jmdispatch.impl;

import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;

final class DispatchTableN extends DispatchTableAbstract<FunctorN> implements DispatchTable {

    @Override
    public DispatchTableN autoregister(Class<?> aclass) {
        return (DispatchTableN) super.autoregister(aclass);
    }

    @Override
    public DispatchTableN autoregister(Object instance) {
        return (DispatchTableN) super.autoregister(instance);
    }

    @Override
    public Object dispatch(Object v1) {
        throw new IllegalArgumentException("Expected " + nTypes + " arguments, got 1");
    }

    @Override
    public Object dispatch(Object v1, Object v2) {
        throw new IllegalArgumentException("Expected " + nTypes + " arguments, got 2");
    }

    @Override
    public Object dispatch(Object... values) {
        if (values.length != nTypes)
            throw new IllegalArgumentException("Expected " + nTypes + " arguments, got " + values.length);

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
            method = findClosest(keys, functors, types); // throws if not found or ambiguous

            // add to the table for further lookups
            append(types, method);
        }

        // call the found method
        return method.f(values);
    }

    // ---

    DispatchTableN(int nTypes) {
        super(nTypes, FunctorN.class, new FunctorImplementationBuilderArray(FunctorN.class, nTypes));
        if (nTypes < 2) throw new IllegalArgumentException("DispatchTableN requires nTypes >= 2, got " + nTypes);
    }

    FunctorN findExact(Object[] values) {
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
