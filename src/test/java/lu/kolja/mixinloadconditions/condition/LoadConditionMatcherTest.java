package lu.kolja.mixinloadconditions.condition;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LoadConditionMatcherTest {
    @Test
    void appliesWithoutConditions() {
        LoadConditionDecision decision = LoadConditionMatcher.evaluate(
                Set.of("examplemod"),
                new LoadConditionMetadata(List.of(), List.of(), List.of(), List.of())
        );

        assertTrue(decision.shouldApply());
    }

    @Test
    void appliesWhenAnyOptionalModIsLoaded() {
        LoadConditionDecision decision = LoadConditionMatcher.evaluate(
                Set.of("modb"),
                new LoadConditionMetadata(List.of(), List.of("moda", "modb"), List.of(), List.of())
        );

        assertTrue(decision.shouldApply());
    }

    @Test
    void rejectsWhenNoOptionalModsAreLoaded() {
        LoadConditionDecision decision = LoadConditionMatcher.evaluate(
                Set.of("corelib"),
                new LoadConditionMetadata(List.of(), List.of("moda", "modb"), List.of(), List.of())
        );

        assertFalse(decision.shouldApply());
    }

    @Test
    void requiresAllMandatoryModsAndOneOptionalMod() {
        LoadConditionDecision decision = LoadConditionMatcher.evaluate(
                Set.of("corelib", "modb"),
                new LoadConditionMetadata(List.of("corelib"), List.of("moda", "modb"), List.of(), List.of())
        );

        assertTrue(decision.shouldApply());
    }

    @Test
    void ignoreIfStillWinsOverSatisfiedPositiveConditions() {
        LoadConditionDecision decision = LoadConditionMatcher.evaluate(
                Set.of("corelib", "modb", "modc"),
                new LoadConditionMetadata(List.of("corelib"), List.of("moda", "modb"), List.of("modc"), List.of())
        );

        assertFalse(decision.shouldApply());
    }

    @Test
    void ignoreIfAnyAlsoWinsOverSatisfiedPositiveConditions() {
        LoadConditionDecision decision = LoadConditionMatcher.evaluate(
                Set.of("corelib", "modb", "modc"),
                new LoadConditionMetadata(List.of("corelib"), List.of("moda", "modb"), List.of(), List.of("modc"))
        );

        assertFalse(decision.shouldApply());
    }
}
