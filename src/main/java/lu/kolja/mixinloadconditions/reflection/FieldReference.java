package lu.kolja.mixinloadconditions.reflection;

import java.lang.reflect.Field;

/**
 * Small typed wrapper around a reflective field lookup.
 *
 * @param <T> field value type
 */
public final class FieldReference<T> {
    private final Field field;

    /**
     * Resolves and opens a declared field on the supplied class.
     *
     * @param clazz declaring class
     * @param fieldName declared field name
     */
    public FieldReference(Class<?> clazz, String fieldName) {
        try {
            this.field = clazz.getDeclaredField(fieldName);
            this.field.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException("Unable to find field " + fieldName + " on " + clazz.getName(), exception);
        }
    }

    /**
     * Reads the field from the supplied instance.
     *
     * @param instance instance to read from
     * @return field value
     */
    @SuppressWarnings("unchecked")
    public T get(Object instance) {
        try {
            return (T) this.field.get(instance);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to read " + this.field.getName(), exception);
        }
    }

    /**
     * Writes the field on the supplied instance.
     *
     * @param instance instance to update
     * @param value new field value
     */
    public void set(Object instance, T value) {
        try {
            this.field.set(instance, value);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to write " + this.field.getName(), exception);
        }
    }
}
