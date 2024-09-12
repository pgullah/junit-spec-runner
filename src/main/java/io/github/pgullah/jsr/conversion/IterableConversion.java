package io.github.pgullah.jsr.conversion;

import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.ObjectConversion;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Optional;

/**
 * Converts Iterables objects like., Array/List
 */
public class IterableConversion extends ObjectConversion {
    private final Class<?> targetType;
    private final TypeConversionService typeConversionService;

    public IterableConversion(TypeConversionService typeConversionService, Class<?> targetType) {
        this.typeConversionService = typeConversionService;
        this.targetType = targetType;

    }

    @Override
    protected Object fromString(String s) {
        final String[] elements = s.replaceAll("^\\[|]$", "").split(",");
        if (targetType.isArray()) {
            Object arr = Array.newInstance(targetType.getComponentType(), elements.length);
            for (int i = 0; i < elements.length; i++) {
                Object el = elements[i];
                Object convertedValue = typeConversionService.lookupConverter(targetType.getComponentType()).map(c -> c.execute(el)).orElse(el);
                Array.set(arr, i, convertedValue);
            }
            return arr;
        } else {
            //TODO: implement other collections
            return Arrays.stream(elements)
                    .map(String::trim)
                    //TODO: any type hints to pass the expected data type with the collection ??
                    .map(eachElement -> typeConversionService.lookupConverter(String.class).map(c -> c.execute(eachElement)))
                    .flatMap(Optional::stream)
                    .toList();
        }
    }
}