package net.eric_nicolas.jmdispatch;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

// public void f(object v1, Object v2) {
//    aclass.amethod((type[0]) v1, (type[1]) v2);
// }
public class FunctorImplementationBuilderDirect extends FunctorImplementationBuilderAbstract {
    FunctorImplementationBuilderDirect(Class<?> classOfFunctor, int nTypes) {
        super(classOfFunctor, nTypes);
    }

    @Override
    protected void transferFunctorArguments(GeneratorAdapter ga, Type[] types) {
        for (int t = 0; t < types.length; ++t) {
            // load the t-th caller argument on to the stack
            ga.loadArg(t);
            // convert it to target 'type'
            ga.unbox(types[t]);
        }
    }
}
