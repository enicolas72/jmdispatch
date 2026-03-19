package net.eric_nicolas.jmdispatch.impl;

import net.eric_nicolas.jmdispatch.InvalidDispatchException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class FunctorImplementationBuilderAbstract {

    static boolean TRACE_ASM = false;

    protected static final Type otype = Type.getType(Object.class);
    protected final Class<?> classOfFunctor;
    private final int nTypes;

    FunctorImplementationBuilderAbstract(Class<?> classOfFunctor, int nTypes) {
        this.classOfFunctor = classOfFunctor;
        this.nTypes = nTypes;
    }

    static class MyClassLoader extends ClassLoader {
        MyClassLoader(ClassLoader parent) {
            super(parent);
        }
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    private static final Map<ClassLoader, MyClassLoader> CLASS_LOADERS = new ConcurrentHashMap<>();
    private static final AtomicLong CLASS_COUNTER = new AtomicLong();

    private static MyClassLoader getClassLoader(Class<?> aclass) {
        ClassLoader parent = aclass.getClassLoader();
        if (parent == null) parent = ClassLoader.getSystemClassLoader();
        return CLASS_LOADERS.computeIfAbsent(parent, MyClassLoader::new);
    }

    Class<?> buildLambaImplementationClass(Class<?> aclass, java.lang.reflect.Method amethod, boolean isStatic) {
        // get the types of the method's parameters
        Parameter[] parameters = amethod.getParameters();
        if (parameters.length != nTypes) {
            throw new InvalidDispatchException("Method marked with @Dispatch with number of arguments <> " + nTypes + ": " + amethod.getName());
        }
        Type[] types = new Type[nTypes];
        for (int i = 0; i < nTypes; ++i) types[i] = Type.getType(parameters[i].getType());

        // build a unique class name (in the same package as the target method)
        String className = aclass.getCanonicalName() + "-" + amethod.getName() + "-" + CLASS_COUNTER.getAndIncrement();

        Type targetClass = Type.getType(aclass);
        Method targetMethod = Method.getMethod(amethod);

        // create the class's bytecode
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        if (TRACE_ASM) {
            PrintWriter pw = new PrintWriter(System.out);
            TraceClassVisitor tc = new TraceClassVisitor(cw, pw);
            buildLambdaClass(tc, className, targetClass, targetMethod, types, isStatic);
        } else {
            buildLambdaClass(cw, className, targetClass, targetMethod, types, isStatic);
        }
        byte[] byteCode = cw.toByteArray();

        // return the created class, for further instantiation
        return getClassLoader(aclass).defineClass(className, byteCode);
    }

    private static final String TARGET_FIELD = "target";

    private void buildLambdaClass(ClassVisitor cv, String className, Type targetClass, Method targetMethod, Type[] types, boolean isStatic) {
        String internalClassName = className.replace('.', '/');
        String innerClassName = classOfFunctor.getCanonicalName().replace('.', '/');

        cv.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_TRANSITIVE,
                internalClassName,
                null,
                // inherit from Object (java's default)
                "java/lang/Object",
                // implements FUNCTOR
                new String[]{innerClassName});

        if (isStatic) {
            buildDefaultConstructor(cv);
        } else {
            // add a field to hold the handler instance
            cv.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, TARGET_FIELD,
                    otype.getDescriptor(), null, null).visitEnd();
            buildInstanceConstructor(cv, internalClassName);
        }

        buildFOverload(cv, internalClassName, targetClass, targetMethod, types, isStatic);

        // finalize the class
        cv.visitEnd();
    }

    // generate the default no-arg constructor (for static handlers)
    private void buildDefaultConstructor(ClassVisitor cv) {
        Method m = Method.getMethod("void <init>()");
        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cv);
        // put 'this' onto the stack
        ga.loadThis();
        // call super()==Object.<init>() constructor
        ga.invokeConstructor(otype, m);
        //
        ga.returnValue();
        ga.endMethod();
    }

    // generate the constructor that takes the handler instance (for instance handlers)
    private void buildInstanceConstructor(ClassVisitor cv, String internalClassName) {
        Method m = Method.getMethod("void <init>(Object)");
        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cv);
        // call super()
        ga.loadThis();
        ga.invokeConstructor(otype, Method.getMethod("void <init>()"));
        // this.target = arg0
        ga.loadThis();
        ga.loadArg(0);
        ga.putField(Type.getObjectType(internalClassName), TARGET_FIELD, otype);
        //
        ga.returnValue();
        ga.endMethod();
    }

    // generate FUNCTOR.f() overload
    protected void buildFOverload(ClassVisitor cv, String internalClassName, Type targetClass, Method targetMethod, Type[] types, boolean isStatic) {
        java.lang.reflect.Method[] methods = classOfFunctor.getDeclaredMethods();
        assert methods.length == 1;
        Method m = Method.getMethod(methods[0]);
        GeneratorAdapter ga = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cv);

        if (!isStatic) {
            // load this.target and cast to the handler class
            ga.loadThis();
            ga.getField(Type.getObjectType(internalClassName), TARGET_FIELD, otype);
            ga.checkCast(targetClass);
        }

        // transfer target's arguments onto the stack
        // call an abstract method: actual arguments transfer differ whether it's -2 or -N implementation
        transferFunctorArguments(ga, types);

        // call the target
        if (isStatic) {
            ga.invokeStatic(targetClass, targetMethod);
        } else {
            ga.invokeVirtual(targetClass, targetMethod);
        }

        // handle return value: functor returns Object, but target may return void or a primitive
        Type returnType = targetMethod.getReturnType();
        if (returnType.equals(Type.VOID_TYPE)) {
            // void target => return null
            ga.visitInsn(Opcodes.ACONST_NULL);
        } else if (returnType.getSort() != Type.OBJECT && returnType.getSort() != Type.ARRAY) {
            // primitive target => box it
            ga.box(returnType);
        }
        // reference target => already an Object on the stack
        ga.returnValue();
        ga.endMethod();
    }

    protected abstract void transferFunctorArguments(GeneratorAdapter ga, Type[] types);
}

