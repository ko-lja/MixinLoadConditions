package lu.kolja.mixinloadconditions.condition;

import lu.kolja.mixinloadconditions.LoadCondition;
import lu.kolja.mixinloadconditions.reflection.TargetClassContextAccess;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.service.MixinService;

/**
 * Mixin extension that removes conditionally-disabled mixins before
 * application.
 *
 * <p>The extension runs during {@link #preApply(ITargetClassContext)} and
 * mutates the pending mixin set held by Mixin's internal target-class context.
 * Condition metadata is cached by mixin class name because the annotation
 * values are immutable after class loading.
 */
public final class LoadConditionApplicatorExtension implements IExtension {
    private static final ILogger LOGGER = MixinService.getService().getLogger("mixinloadconditions");
    private static final int CLASS_READER_FLAGS = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;

    private final ConcurrentMap<String, LoadConditionMetadata> metadataByMixinClass = new ConcurrentHashMap<>();

    /**
     * Keeps the extension active in every Mixin environment.
     *
     * @param environment current Mixin environment
     * @return always {@code true}
     */
    @Override
    public boolean checkActive(MixinEnvironment environment) {
        return true;
    }

    /**
     * Filters the pending mixin set for the supplied target class.
     *
     * @param context target-class application context
     */
    @Override
    public void preApply(ITargetClassContext context) {
        TargetClassContextAccess.tryAs(context, access -> this.filterMixins(access.getMixins(), context.toString()));
    }

    /**
     * Evaluates each mixin against the currently-loaded mod ids and removes any
     * mixin whose {@link LoadCondition} does not
     * match.
     *
     * @param mixins pending mixins for the target class
     * @param targetClassName human-readable target class name for debug logging
     */
    private void filterMixins(SortedSet<IMixinInfo> mixins, String targetClassName) {
        Set<String> loadedMods = LoadedMods.get();
        mixins.removeIf(mixinInfo -> {
            LoadConditionMetadata metadata = this.metadataByMixinClass.computeIfAbsent(
                    mixinInfo.getClassName(),
                    ignored -> LoadConditionMetadata.from(mixinInfo, CLASS_READER_FLAGS)
            );
            LoadConditionDecision decision = LoadConditionMatcher.evaluate(loadedMods, metadata);
            if (!decision.shouldApply()) {
                LOGGER.debug("Skipping mixin {} for target {} because {}", mixinInfo.getClassName(), targetClassName, decision.reason());
            }
            return !decision.shouldApply();
        });
    }

    /**
     * No post-apply work is required because all decisions are made before any
     * mixin is applied.
     *
     * @param context target-class application context
     */
    @Override
    public void postApply(ITargetClassContext context) {
    }

    /**
     * This extension does not export bytecode.
     *
     * @param env current Mixin environment
     * @param name exported class name
     * @param force whether export was forced
     * @param classNode class node that would be exported
     */
    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {
    }
}
