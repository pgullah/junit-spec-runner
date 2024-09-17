package io.github.pgullah.jsr.conversion;

import io.github.pgullah.jsr.util.ReflectUtils;
import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.ObjectConversion;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

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
                Object convertedValue = typeConversionService.convert(el, targetType.getComponentType());
                Array.set(arr, i, convertedValue);
            }
            return arr;
        } else {
            return Arrays.stream(elements)
                    .map(String::trim)
                    // TODO: Identify the elements type in collection
                    .map(eachElement -> typeConversionService.convert(eachElement, String.class))
                    .collect(Collectors.toCollection(this::collectionSupplier));
        }
    }

    private Collection<Object> collectionSupplier() {
        if (ReflectUtils.isConcreteClass(targetType) && targetType.isAssignableFrom(Collection.class)) {
            // assuming we get only collection items here
            return (Collection<Object>) ReflectUtils.newInstance(targetType);
        } else if (targetType.isAssignableFrom(List.class)) {
            return new ArrayList<>();
        } else if (targetType.isAssignableFrom(Set.class)) {
            return new HashSet<>();
        }
        throw new UnsupportedOperationException("TargetType %s is not a collection type!".formatted(targetType));
    }
}