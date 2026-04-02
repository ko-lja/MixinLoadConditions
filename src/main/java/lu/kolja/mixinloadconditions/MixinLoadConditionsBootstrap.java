package lu.kolja.mixinloadconditions;

import lu.kolja.mixinloadconditions.condition.LoadConditionApplicatorExtension;
import lu.kolja.mixinloadconditions.ext.ExtensionRegistrar;
import lu.kolja.mixinloadconditions.reflection.ExtensionsAccess;
import java.util.List;
import java.util.stream.Collectors;

import lu.kolja.mixinloadconditions.platform.forge.MixinLoadConditionsConfigPlugin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;

/**
 * Bootstraps the {@code MixinLoadConditions} runtime hooks.
 *
 * <p>This class is invoked from the tiny Forge-side {@link
 * MixinLoadConditionsConfigPlugin
 * config plugin}. It registers the global extension that filters pending
 * mixins and then moves that extension to the front of the active extension
 * list so condition checks happen as early as possible.
 */
public final class MixinLoadConditionsBootstrap {
    /**
     * Stable logical name for the extension.
     */
    public static final String NAME = "mixinloadconditions";

    /**
     * Current library version.
     */
    public static final String VERSION = "0.1.0";

    private static boolean initialized = false;

    private MixinLoadConditionsBootstrap() {
    }

    /**
     * Registers the global load-condition extension once for the current Mixin
     * transformer instance.
     */
    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        ExtensionRegistrar.register(new LoadConditionApplicatorExtension());
    }

    /**
     * Moves the load-condition extension to the front of the registered and
     * active extension lists.
     *
     * <p>Running first keeps the pending mixin set trimmed before other
     * extensions start performing work based on mixins that may ultimately be
     * skipped.
     */
    public static void reOrderExtensions() {
        IMixinTransformer transformer = (IMixinTransformer) MixinEnvironment.getDefaultEnvironment().getActiveTransformer();
        ExtensionsAccess.tryAs(transformer.getExtensions(), access -> {
            List<IExtension> allExtensions = access.getExtensions();
            List<IExtension> loadConditionExtensions = allExtensions.stream()
                    .filter(LoadConditionApplicatorExtension.class::isInstance)
                    .toList();

            if (loadConditionExtensions.isEmpty()) {
                return;
            }

            allExtensions.removeAll(loadConditionExtensions);
            allExtensions.addAll(0, loadConditionExtensions);

            List<IExtension> activeExtensions = access.getActiveExtensions();
            List<IExtension> reorderedActiveExtensions = activeExtensions.stream()
                    .filter(LoadConditionApplicatorExtension.class::isInstance)
                    .collect(Collectors.toList());
            activeExtensions.stream()
                    .filter(extension -> !(extension instanceof LoadConditionApplicatorExtension))
                    .forEach(reorderedActiveExtensions::add);
            access.setActiveExtensions(reorderedActiveExtensions);
        });
    }
}
