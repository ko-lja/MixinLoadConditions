package lu.kolja.mixinloadconditions.reflection;

import java.util.List;
import java.util.function.Consumer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;

/**
 * Reflective access wrapper for Mixin's internal {@link Extensions}
 * implementation.
 *
 * <p>The public extension registry interface only exposes read-only views. The
 * addon needs mutable access so it can move its extension to the front of both
 * the registered and active extension lists.
 */
public final class ExtensionsAccess {
    private final Extensions reference;
    private final FieldReference<List<IExtension>> extensionsField;
    private final FieldReference<List<IExtension>> activeExtensionsField;

    private ExtensionsAccess(Extensions reference) {
        this.reference = reference;
        this.extensionsField = new FieldReference<>(reference.getClass(), "extensions");
        this.activeExtensionsField = new FieldReference<>(reference.getClass(), "activeExtensions");
    }

    /**
     * Runs the supplied consumer only when the registry is Mixin's internal
     * {@link Extensions} implementation.
     *
     * @param reference extension registry to inspect
     * @param consumer consumer to invoke when access is possible
     */
    public static void tryAs(IExtensionRegistry reference, Consumer<ExtensionsAccess> consumer) {
        if (reference instanceof Extensions) {
            consumer.accept(new ExtensionsAccess((Extensions) reference));
        }
    }

    /**
     * Returns the mutable list of all registered extensions.
     *
     * @return mutable internal extension list
     */
    public List<IExtension> getExtensions() {
        return this.extensionsField.get(this.reference);
    }

    /**
     * Returns the current active extension list.
     *
     * @return active extension list
     */
    public List<IExtension> getActiveExtensions() {
        return this.reference.getActiveExtensions();
    }

    /**
     * Replaces the active extension list used by the current transformer.
     *
     * @param extensions reordered active extension list
     */
    public void setActiveExtensions(List<IExtension> extensions) {
        this.activeExtensionsField.set(this.reference, extensions);
    }
}
