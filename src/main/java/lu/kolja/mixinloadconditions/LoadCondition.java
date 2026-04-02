package lu.kolja.mixinloadconditions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares mod-dependent load rules for a mixin class.
 *
 * <p>The annotation is read directly from the mixin bytecode during Mixin's
 * pre-application extension phase. This allows the extension to decide whether
 * a mixin should remain in the pending mixin set before the target class is
 * transformed.
 *
 * <p>{@link #loadIf()} is treated as an all-of requirement. Every listed mod id
 * must already be present in Forge's loading mod list for the mixin to apply.
 *
 * <p>{@link #loadIfAny()} is treated as an any-of requirement. When the array
 * is non-empty, at least one listed mod id must be present for the mixin to
 * apply.
 *
 * <p>{@link #ignoreIf()} and {@link #ignoreIfAny()} are treated as any-of
 * exclusions. If any listed mod id is present, the mixin is removed even when
 * the positive conditions are also satisfied.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface LoadCondition {
    /**
     * Lists mod ids that must all be loaded for the annotated mixin to apply.
     *
     * @return required Forge mod ids
     */
    String[] loadIf() default {};

    /**
     * Lists mod ids where at least one must be loaded for the annotated mixin
     * to apply.
     *
     * @return alternative Forge mod ids
     */
    String[] loadIfAny() default {};

    /**
     * Lists mod ids that immediately prevent the annotated mixin from applying.
     *
     * @return excluded Forge mod ids
     */
    String[] ignoreIf() default {};

    /**
     * Lists mod ids where any loaded entry immediately prevents the annotated
     * mixin from applying.
     *
     * <p>This is an explicit any-of alias for {@link #ignoreIf()} so the API
     * mirrors {@link #loadIfAny()}.
     *
     * @return excluded Forge mod ids
     */
    String[] ignoreIfAny() default {};
}
