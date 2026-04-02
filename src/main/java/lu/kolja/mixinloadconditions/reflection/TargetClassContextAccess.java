package lu.kolja.mixinloadconditions.reflection;

import java.util.SortedSet;
import java.util.function.Consumer;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

/**
 * Reflective access wrapper for Mixin's internal target-class context.
 *
 * <p>The public {@link ITargetClassContext} interface does not expose the
 * pending mixin set, so this helper reaches into Mixin's concrete
 * implementation when available.
 */
public final class TargetClassContextAccess {
    private final ITargetClassContext reference;
    private final FieldReference<SortedSet<?>> mixinsField;

    private TargetClassContextAccess(ITargetClassContext reference) {
        this.reference = reference;
        this.mixinsField = new FieldReference<>(reference.getClass(), "mixins");
    }

    /**
     * Runs the supplied consumer only when the context is Mixin's internal
     * target-class context implementation.
     *
     * @param reference context to inspect
     * @param consumer consumer to invoke when reflective access is possible
     */
    public static void tryAs(ITargetClassContext reference, Consumer<TargetClassContextAccess> consumer) {
        if (reference.getClass().getName().equals("org.spongepowered.asm.mixin.transformer.TargetClassContext")) {
            consumer.accept(new TargetClassContextAccess(reference));
        }
    }

    /**
     * Returns the mutable sorted set of pending mixins for the current target
     * class.
     *
     * @return pending mixin set
     */
    @SuppressWarnings("unchecked")
    public SortedSet<IMixinInfo> getMixins() {
        return (SortedSet<IMixinInfo>) this.mixinsField.get(this.reference);
    }
}
