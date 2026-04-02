package lu.kolja.mixinloadconditions.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluates sanitized {@link LoadConditionMetadata} against the currently
 * loaded Forge mod ids.
 */
final class LoadConditionMatcher {
    private LoadConditionMatcher() {
    }

    /**
     * Computes whether a mixin should apply.
     *
     * <p>The decision order is intentional:
     *
     * <ol>
     * <li>If there are no conditions, the mixin applies.</li>
     * <li>If any {@code ignoreIf} or {@code ignoreIfAny} entry is loaded, the
     * mixin is rejected.</li>
     * <li>If any {@code loadIf} entry is missing, the mixin is rejected.</li>
     * <li>If {@code loadIfAny} is declared and none of its entries are loaded,
     * the mixin is rejected.</li>
     * <li>Otherwise the mixin applies.</li>
     * </ol>
     *
     * @param loadedMods lowercase loaded Forge mod ids
     * @param metadata sanitized annotation metadata
     * @return evaluation result and explanation
     */
    static LoadConditionDecision evaluate(Set<String> loadedMods, LoadConditionMetadata metadata) {
        if (!metadata.hasConditions()) {
            return new LoadConditionDecision(true, "no load conditions");
        }

        List<String> ignoredMods = new ArrayList<>();
        for (String modId : metadata.ignoreIf()) {
            if (loadedMods.contains(modId)) {
                ignoredMods.add(modId);
            }
        }
        if (!ignoredMods.isEmpty()) {
            return new LoadConditionDecision(false, "ignoreIf matched loaded mods " + ignoredMods);
        }

        List<String> ignoredAnyMods = new ArrayList<>();
        for (String modId : metadata.ignoreIfAny()) {
            if (loadedMods.contains(modId)) {
                ignoredAnyMods.add(modId);
            }
        }
        if (!ignoredAnyMods.isEmpty()) {
            return new LoadConditionDecision(false, "ignoreIfAny matched loaded mods " + ignoredAnyMods);
        }

        List<String> missingRequiredMods = new ArrayList<>();
        for (String modId : metadata.loadIf()) {
            if (!loadedMods.contains(modId)) {
                missingRequiredMods.add(modId);
            }
        }
        if (!missingRequiredMods.isEmpty()) {
            return new LoadConditionDecision(false, "missing required mods " + missingRequiredMods);
        }

        if (!metadata.loadIfAny().isEmpty()) {
            List<String> matchedOptionalMods = new ArrayList<>();
            for (String modId : metadata.loadIfAny()) {
                if (loadedMods.contains(modId)) {
                    matchedOptionalMods.add(modId);
                }
            }
            if (matchedOptionalMods.isEmpty()) {
                return new LoadConditionDecision(false, "none of loadIfAny were loaded " + metadata.loadIfAny());
            }
        }

        return new LoadConditionDecision(true, "conditions satisfied");
    }
}
