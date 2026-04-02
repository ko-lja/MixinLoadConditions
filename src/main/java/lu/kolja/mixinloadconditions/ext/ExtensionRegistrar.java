package lu.kolja.mixinloadconditions.ext;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;

/**
 * Registers runtime extensions against the active Mixin transformer.
 *
 * <p>This is a very small reflective convenience layer around Mixin internals.
 * The addon uses it to register its extension from a standard config plugin
 * entry point without replacing the existing transformer setup.
 */
public final class ExtensionRegistrar {
    private ExtensionRegistrar() {
    }

    /**
     * Appends an extension to the active transformer's extension registry.
     *
     * @param extension extension instance to register
     */
    public static void register(IExtension extension) {
        IMixinTransformer transformer = (IMixinTransformer) MixinEnvironment.getDefaultEnvironment().getActiveTransformer();
        Extensions extensions = (Extensions) transformer.getExtensions();
        extensions.add(extension);
    }
}
