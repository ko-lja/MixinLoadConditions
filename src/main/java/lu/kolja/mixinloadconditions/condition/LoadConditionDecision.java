package lu.kolja.mixinloadconditions.condition;

/**
 * Immutable result of evaluating one mixin's load conditions.
 *
 * @param shouldApply whether the mixin should remain in the pending mixin set
 * @param reason human-readable explanation for debug logging
 */
record LoadConditionDecision(boolean shouldApply, String reason) {
}
