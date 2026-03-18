package net.eric_nicolas.jmdispatch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a dispatch handler for multiple dispatch.
 *
 * <p>Annotated methods are discovered by {@link DispatchTable#autoregister(Class)}
 * or {@link DispatchTable#autoregister(Object)} and registered as candidate handlers
 * in the dispatch table. At dispatch time, the framework selects the best-matching handler
 * based on the runtime types of all arguments.
 *
 * <p>Annotated methods must be <b>concrete</b> (not abstract) and may be either
 * static or instance methods. All parameter types must be <b>concrete classes</b>
 * (not interfaces or abstract classes), since dispatch relies on walking the
 * superclass chain to compute inheritance distance.
 *
 * <p>Static methods can be registered via {@code autoregister(Class)}.
 * Instance methods require {@code autoregister(instance)} so the dispatch table
 * can bind the method to a specific receiver. Both static and instance methods
 * can coexist in the same class.
 *
 * <p>Handlers may return any type (object, primitive, or void). Return values are
 * boxed and returned as {@code Object} from {@code dispatch()}; void handlers
 * return {@code null}.
 *
 * @see DispatchTable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Dispatch {
}
