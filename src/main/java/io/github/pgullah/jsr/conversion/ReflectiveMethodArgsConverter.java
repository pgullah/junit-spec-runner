package io.github.pgullah.jsr.conversion;

import java.util.stream.IntStream;

public class ReflectiveMethodArgsConverter implements MethodArgsConverter {
    private final Class[] methodParameterTypes;
    private final TypeConversionService typeConversionService;

    public ReflectiveMethodArgsConverter(TypeConversionService typeConversionService, Class[] methodParameterTypes) {
        this.methodParameterTypes = methodParameterTypes;
        this.typeConversionService = typeConversionService;
    }

    @Override
    public Object[] convert(String[] input) {
        return IntStream.range(0, methodParameterTypes.length)
                .mapToObj(i -> convertToTargetType(input[i], methodParameterTypes[i]))
                .toArray();
    }

    private Object convertToTargetType(String value, Class<?> targetType) {
        return typeConversionService.convert(value, targetType);
    }
}
