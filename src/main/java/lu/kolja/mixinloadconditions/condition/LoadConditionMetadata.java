package lu.kolja.mixinloadconditions.condition;

import lu.kolja.mixinloadconditions.LoadCondition;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

/**
 * Sanitized view of a mixin class's {@link LoadCondition} annotation.
 *
 * <p>Values are normalized to lowercase, trimmed, deduplicated, and converted
 * to immutable lists so the matcher can operate on stable data.
 */
record LoadConditionMetadata(List<String> loadIf, List<String> loadIfAny, List<String> ignoreIf, List<String> ignoreIfAny) {
    private static final LoadConditionMetadata NONE = new LoadConditionMetadata(List.of(), List.of(), List.of(), List.of());

    /**
     * Creates sanitized metadata.
     *
     * @param loadIf    required mod ids
     * @param loadIfAny alternative required mod ids
     * @param ignoreIf   excluded mod ids
     * @param ignoreIfAny explicit any-of excluded mod ids
     */
    LoadConditionMetadata {
    }

    /**
     * Reads and sanitizes the {@link LoadCondition} annotation from a mixin
     * class.
     *
     * @param mixinInfo        mixin to inspect
     * @param classReaderFlags ASM flags to use when creating the class node
     * @return immutable condition metadata
     */
    static LoadConditionMetadata from(IMixinInfo mixinInfo, int classReaderFlags) {
        AnnotationNode annotation = findAnnotation(mixinInfo, classReaderFlags);
        if (annotation == null) {
            return NONE;
        }

        return new LoadConditionMetadata(
                sanitize(Annotations.getValue(annotation, "loadIf", true)),
                sanitize(Annotations.getValue(annotation, "loadIfAny", true)),
                sanitize(Annotations.getValue(annotation, "ignoreIf", true)),
                sanitize(Annotations.getValue(annotation, "ignoreIfAny", true))
        );
    }

    /**
     * Returns the sanitized {@code loadIf} entries.
     *
     * @return required mod ids
     */
    @Override
    public List<String> loadIf() {
        return this.loadIf;
    }

    /**
     * Returns the sanitized {@code loadIfAny} entries.
     *
     * @return alternative required mod ids
     */
    @Override
    public List<String> loadIfAny() {
        return this.loadIfAny;
    }

    /**
     * Returns the sanitized {@code ignoreIf} entries.
     *
     * @return excluded mod ids
     */
    @Override
    public List<String> ignoreIf() {
        return this.ignoreIf;
    }

    /**
     * Returns the sanitized {@code ignoreIfAny} entries.
     *
     * @return explicit any-of excluded mod ids
     */
    @Override
    public List<String> ignoreIfAny() {
        return this.ignoreIfAny;
    }

    /**
     * Returns whether the mixin declared any condition at all.
     *
     * @return {@code true} if any condition list is non-empty
     */
    boolean hasConditions() {
        return !this.loadIf.isEmpty()
                || !this.loadIfAny.isEmpty()
                || !this.ignoreIf.isEmpty()
                || !this.ignoreIfAny.isEmpty();
    }

    /**
     * Looks up the annotation from the class node, checking invisible
     * annotations first because {@link LoadCondition} uses {@code CLASS}
     * retention.
     *
     * @param mixinInfo        mixin to inspect
     * @param classReaderFlags ASM flags to use when reading the class
     * @return matching annotation node or {@code null}
     */
    private static AnnotationNode findAnnotation(IMixinInfo mixinInfo, int classReaderFlags) {
        ClassNode classNode = mixinInfo.getClassNode(classReaderFlags);
        AnnotationNode annotation = Annotations.getInvisible(classNode, LoadCondition.class);
        if (annotation != null) {
            return annotation;
        }
        return Annotations.getVisible(classNode, LoadCondition.class);
    }

    /**
     * Normalizes raw annotation values to a stable matcher-friendly list.
     *
     * @param modIds raw annotation values
     * @return trimmed, lowercase, distinct mod ids
     */
    private static List<String> sanitize(List<String> modIds) {
        return modIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(modId -> !modId.isEmpty())
                .map(modId -> modId.toLowerCase(Locale.ROOT))
                .distinct()
                .toList();
    }
}
