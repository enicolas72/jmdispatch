package net.eric_nicolas.jmdispatch;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

// public void f(Object[] values) {
//    aclass.amethod((types[0])values[0], (types[1])values[1], ...);
// }
public class FunctorImplementationBuilderArray extends FunctorImplementationBuilderAbstract {
    FunctorImplementationBuilderArray(int nTypes) {
        super(FunctorN.class, nTypes);
    }

    @Override
    protected void transferFunctorArguments(GeneratorAdapter ga, Type[] types) {
        for (int t = 0; t < types.length; ++t) {
            // load the first argument (array) onto the stack
            ga.loadArg(0);
            // Load t-th element of the array onto the stack
            ga.push(t);
            ga.arrayLoad(otype);
            // convert it to target 'type'
            ga.unbox(types[t]);
        }
    }
}
