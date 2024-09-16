package io.github.pgullah.jsr.conversion;

public interface TypeConverter<I, O> {

    O convert(I input);

    /**
     * This marker class is only to be used with annotations, to
     * indicate that <b>no type converter is configured</b>.
     * <p>
     * Specifically, this class is to be used as the marker for
     */
    abstract class None implements TypeConverter<String, Object> {
    }
}
