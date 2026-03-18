package net.eric_nicolas.jmdispatch.impl;

import net.eric_nicolas.jmdispatch.DispatchNoMatchException;
import net.eric_nicolas.jmdispatch.DispatchTable;

final class DispatchTable2 extends DispatchTableAbstract<Functor2> implements DispatchTable {

    @Override
    public DispatchTable2 autoregister(Class<?> aclass) {
        return (DispatchTable2) super.autoregister(aclass);
    }

    @Override
    public DispatchTable2 autoregister(Object instance) {
        return (DispatchTable2) super.autoregister(instance);
    }

    @Override
    public Object dispatch(Object v1) {
        throw new IllegalArgumentException("Expected 2 arguments, got 1");
    }

    @Override
    public Object dispatch(Object v1, Object v2) {
        // null arguments cannot be dispatched
        if (v1 == null || v2 == null) {
            String t1 = v1 == null ? "null" : v1.getClass().getCanonicalName();
            String t2 = v2 == null ? "null" : v2.getClass().getCanonicalName();
            throw new DispatchNoMatchException("Cannot dispatch on null argument(s):  (" + t1 + "," + t2 + ") ");
        }

        // search for exact match, pass-in individual types to delay the (costly) creation of Class<?>[] types
        Functor2 method = findExact(v1.getClass(), v2.getClass());
        if (method == null) {
            // no direct match => search for the closest match
            Class<?>[] types = new Class<?>[]{v1.getClass(), v2.getClass()};
            method = findClosest(keys, functors, types); // throws if not found or ambiguous

            // add to the table for further Lookups
            append(types, method);
        }

        // call the found method
        return method.f(v1, v2);
    }

    @Override
    public Object dispatch(Object... values) {
        if (values == null) throw new DispatchNoMatchException("Cannot dispatch on null argument(s):  (null) ");
        throw new DispatchNoMatchException("Expected 2 arguments, got " + values.length);
    }

    // ---

    DispatchTable2() {
        super(2, Functor2.class, new FunctorImplementationBuilderDirect(Functor2.class, 2));
    }

    Functor2 findExact(Class<?> type1, Class<?> type2) {
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] == type1 && keys[i][1] == type2) {
                return functors[i];
            }
        }
        return null;
    }
}