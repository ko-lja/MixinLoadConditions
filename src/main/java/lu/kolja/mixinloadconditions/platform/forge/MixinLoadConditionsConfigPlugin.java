package lu.kolja.mixinloadconditions.platform.forge;

import lu.kolja.mixinloadconditions.MixinLoadConditionsBootstrap;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Forge bootstrap config plugin for the addon.
 *
 * <p>The plugin itself never approves or rejects mixins directly. Its only job
 * is to initialize the global extension and ensure that extension runs before
 * later extensions when the tiny bootstrap mixin config is loaded by Forge.
 */
public final class MixinLoadConditionsConfigPlugin implements IMixinConfigPlugin {
    /**
     * Initializes the addon when the bootstrap mixin configuration is loaded.
     *
     * @param mixinPackage package declared by the bootstrap mixin config
     */
    @Override
    public void onLoad(String mixinPackage) {
        MixinLoadConditionsBootstrap.init();
    }

    /**
     * This addon does not provide a dedicated refmap.
     *
     * @return {@code null}
     */
    @Override
    public String getRefMapperConfig() {
        return null;
    }

    /**
     * Always returns {@code true} because condition filtering is handled by the
     * global extension rather than by the config plugin.
     *
     * @param targetClassName target class name
     * @param mixinClassName mixin class name
     * @return always {@code true}
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    /**
     * No additional target registration is required.
     *
     * @param myTargets targets owned by this config
     * @param otherTargets targets owned by other configs
     */
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    /**
     * Reorders registered extensions after initialization.
     *
     * @return {@code null} because the bootstrap config contributes no mixin
     *     classes of its own
     */
    @Override
    public List<String> getMixins() {
        MixinLoadConditionsBootstrap.reOrderExtensions();
        return null;
    }

    /**
     * No pre-apply hook is required for the bootstrap config.
     *
     * @param targetClassName target class name
     * @param targetClass target class node
     * @param mixinClassName mixin class name
     * @param mixinInfo mixin metadata
     */
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    /**
     * No post-apply hook is required for the bootstrap config.
     *
     * @param targetClassName target class name
     * @param targetClass target class node
     * @param mixinClassName mixin class name
     * @param mixinInfo mixin metadata
     */
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
