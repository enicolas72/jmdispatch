package net.eric_nicolas.jmdispatch.impl;

import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;

final class DispatchTable1 extends DispatchTableAbstract<Functor1> implements DispatchTable {

    @Override
    public DispatchTable1 autoregister(Class<?> aclass) {
        return (DispatchTable1) super.autoregister(aclass);
    }

    @Override
    public DispatchTable1 autoregister(Object instance) {
        return (DispatchTable1) super.autoregister(instance);
    }

    @Override
    public Object dispatch(Object v) {
        // null arguments cannot be dispatched
        if (v == null) throw new DispatchNoMatchException("Cannot dispatch on null argument(s):  (null) ");

        // search for exact match, pass-in individual types to delay the (costly) creation of Class<?>[] types
        Functor1 method = findExact(v.getClass());
        if (method == null) {
            // no direct match => search for the closest match
            Class<?>[] types = new Class<?>[]{v.getClass()};
            method = findClosest(keys, functors, types); // throws if not found or ambiguous

            // add to the table for further Lookups
            append(types, method);
        }

        // call the found method
        return method.f(v);
    }

    @Override
    public Object dispatch(Object v1, Object v2) {
        throw new IllegalArgumentException("Expected 1 argument, got 2");
    }

    @Override
    public Object dispatch(Object... values) {
        if (values == null) throw new DispatchNoMatchException("Cannot dispatch on null argument(s):  (null) ");
        throw new IllegalArgumentException("Expected 1 argument, got " + values.length);
    }

    // ---

    DispatchTable1() {
        super(1, Functor1.class, new FunctorImplementationBuilderDirect(Functor1.class, 1));
    }

    Functor1 findExact(Class<?> type1) {
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] == type1) {
                return functors[i];
            }
        }
        return null;
    }
}