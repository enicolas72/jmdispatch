package net.eric_nicolas.jmdispatch;

public class DispatchTable2 extends DispatchTableAbstract<Functor2> {

    public DispatchTable2() {
        super(2, Functor2.class, new FunctorImplementationBuilder2());
    }

    public DispatchTable2 autoregister(Class<?> aclass) {
        return (DispatchTable2) super.autoregister(aclass);
    }

    public void dispatch(Object v1, Object v2) {
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
        method.f(v1, v2);
    }

    private Functor2 findExact(Class<?> type1, Class<?> type2) {
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i][0] == type1 && keys[i][1] == type2) {
                return functors[i];
            }
        }
        return null;
    }
}