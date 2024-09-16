package io.github.pgullah.jsr.conversion;

import java.util.Collection;

public interface MethodArgsConverter extends TypeConverter<String[], Object[]> {

    /**
     * This marker class is only to be used with annotations, to
     * indicate that <b>no method args serializer is configured</b>.
     *<p>
     * Specifically, this class is to be used as the marker for
     */
    abstract class None implements MethodArgsConverter { }

}
