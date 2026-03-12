package net.eric_nicolas.jmdispatch;

import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DispatchTableAbstract<FUNCTOR> {

    public DispatchTableAbstract(int nTypes, Class<?> classOfFunctor, FunctorImplementationBuilderAbstract functorImplementationBuilder) {
        this.nTypes = nTypes;
        this.classOfFunctor = classOfFunctor;
        this.functorImplementationBuilder = functorImplementationBuilder;
        this.keys = new Class<?>[0][];
        // noinspection unchecked
        this.functors = (FUNCTOR[]) Array.newInstance(classOfFunctor, 0);
    }

    public DispatchTableAbstract<FUNCTOR> autoregister(Class<?> aclass) {
        var methods = aclass.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methods[i].getAnnotation(Dispatch.class) != null) {
                autoregister(aclass, methods[i], i);
            }
        }
        return this;
    }

    private void autoregister(Class<?> aclass, java.lang.reflect.Method method, int i) {
        try {
            Class<?> functorImplementation = functorImplementationBuilder.buildLambaImplementationClass(aclass, method, i);
            // noinspection unchecked
            FUNCTOR functor = (FUNCTOR) functorImplementation.getDeclaredConstructors()[0].newInstance();

            //
            Parameter[] parameters = method.getParameters();
            Class<?>[] types = new Class<?>[nTypes];
            for (int t = 0; t < nTypes; ++t) types[t] = primitiveToBoxed(parameters[t].getType());

            // check that the method is not already registered
            for (Class<?>[] key : keys) {
                if (equals(key, types)) {
                    throw new RuntimeException("Registering a method on an already existing types signature " + dump(types));
                }
            }

            // register the method
            append(types, functor);
        } catch (Throwable e) {
            throw new RuntimeException("Cannot autoregister: " + aclass.getCanonicalName() + "." + method.getName(), e);
        }
    }

    // ---

    protected final int nTypes;
    protected final Class<?> classOfFunctor;
    protected final FunctorImplementationBuilderAbstract functorImplementationBuilder;
    protected Class<?>[][] keys;
    protected FUNCTOR[] functors;

    protected void append(Class<?>[] types, FUNCTOR functor) {
        int nt = keys.length;
        {
            Class<?>[][] array2 = new Class<?>[nt + 1][];
            System.arraycopy(keys, 0, array2, 1, nt);
            array2[0] = types;
            keys = array2;
        }

        {
            // noinspection unchecked
            FUNCTOR[] array2 = (FUNCTOR[]) Array.newInstance(classOfFunctor, nt + 1);
            System.arraycopy(functors, 0, array2, 1, nt);
            array2[0] = functor;
            functors = array2;
        }
    }

    // -- Find cLosest algorithm
    //    Two matches are equivalent if the 'distances' arrays, sorted, are the same
    //    For instance (1, 0, 2) == (2, 1, 0)
    static class Distance {
        int[] distances;
        Class<?>[] to;

        Distance(int nTypes) {
            this.distances = new int[nTypes];
            this.to = new Class<?>[nTypes];
        }

        int norm() {
            int result = 0;
            for (int d : distances) result += d * d;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Distance d0 = this;
            Distance d1 = (Distance) o;

            int[] sorted0 = d0.distances.clone();
            int[] sorted1 = d1.distances.clone();
            Arrays.sort(sorted0);
            Arrays.sort(sorted1);
            return Arrays.equals(sorted0, sorted1);
        }

        @Override
        public int hashCode() {
            int[] sorted = distances.clone();
            Arrays.sort(sorted);
            return Arrays.hashCode(sorted);
        }
    }

    private static class FoundMatches<FUNCTOR> {
        ArrayList<FUNCTOR> functors = new ArrayList<>();
        ArrayList<Distance> distances = new ArrayList<>();
    }

    static <FUNCTOR> FUNCTOR findClosest(Class<?>[][] keys, FUNCTOR[] functors, Class<?>[] types) {
        int nTypes = types.length;
        Map<Distance, FoundMatches<FUNCTOR>> distances2matches = new HashMap<>();

        int lowestDistanceNorm = Integer.MAX_VALUE;
        FoundMatches<FUNCTOR> lowestDistanceMatches = null;
        outer:
        for (int i = 0; i < keys.length; ++i) {
            // compute the distance from types to key
            // continue the keys loop if one of the types do not match at all (distance < 0)
            Distance distance = new Distance(nTypes);
            for (int t = 0; t < nTypes; ++t) {
                int distance_t = distance(keys[i][t], types[t]);
                if (distance_t < 0) continue outer;
                distance.distances[t] = distance_t;
                distance.to[t] = keys[i][t];
            }

            // store the found matches for each equivalent distance
            FoundMatches<FUNCTOR> matches = distances2matches.computeIfAbsent(distance, _ -> new FoundMatches<>());
            matches.functors.add(functors[i]);
            matches.distances.add(distance);

            // keep track of the lowest distance (as measured by the norm)
            if (distance.norm() < lowestDistanceNorm) {
                lowestDistanceNorm = distance.norm();
                lowestDistanceMatches = matches;
            }
        }

        // no match found => throw exception
        if (lowestDistanceMatches == null) {
            throw new DispatchNoMatchException("No dispatch match found for: " + dump(types));
        }
        // multiple matches found => throw exception
        if (lowestDistanceMatches.functors.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lowestDistanceMatches.distances.size(); ++i) {
                if (i > 0) sb.append(" ; ");
                sb.append(dump(lowestDistanceMatches.distances.get(i).to));
            }
            throw new DispatchAmbiguousException("Multiple dispatch matches found for: " + dump(types) + ". Ambiguous call. Possible matches are {" + sb + "}");
        }
        // only one match found => return it
        return lowestDistanceMatches.functors.getFirst();
    }

    // --- Class<?> types helpers

    // the distance from type to targetType in count of inheritance steps
    // - if 'type' does not inherit from 'targetType' => -1
    // - if 'type' is targetType => 0
    // - if 'type's superclass is 'targetType' => 1
    // - etc.
    static int distance(Class<?> targetType, Class<?> type) {
        if (!targetType.isAssignableFrom(type)) return -1;
        if (targetType == type) return 0;
        for (int d = 0; ; ++d) {
            type = type.getSuperclass();
            if (type == targetType) return d + 1;
        }
    }

    static boolean equals(Class<?>[] types1, Class<?>[] types2) {
        for (int t = 0; t < types1.length; ++t) {
            if (types1[t] != types2[t]) return false;
        }
        return true;
    }

    static String dump(Class<?>[] types) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        for (int t = 0; t < types.length; ++t) {
            if (t > 0) sb.append(",");
            sb.append(types[t].getCanonicalName());
        }
        sb.append(") ");
        return sb.toString();
    }

    static Class<?>[] fromValues(Object[] values) {
        Class<?>[] types = new Class<?>[values.length];
        for (int t = 0; t < values.length; ++t) {
            types[t] = values[t].getClass();
        }
        return types;
    }

    static Class<?> primitiveToBoxed(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == int.class) return Integer.class;
            else if (type == long.class) return Long.class;
            else if (type == float.class) return Float.class;
            else if (type == double.class) return Double.class;
            else if (type == char.class) return Character.class;
            else throw new RuntimeException("Unhandled primitive type: " + type.getCanonicalName());
        } else {
            return type;
        }
    }
}
