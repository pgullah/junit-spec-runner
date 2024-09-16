package io.github.pgullah.jsr.conversion;

import java.util.ArrayList;
import java.util.List;
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
        /*final List<Object> convertedArgs = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (i >= methodParameterTypes.length) {
                throw new RuntimeException("Input size [%d] doesn't match with the required method arguments arity [%d]!".formatted(i, methodParameterTypes.length));
            }
            String value = input[i];
            convertedArgs.add(convertToTargetType(value, methodParameterTypes[i]));
        }
        return convertedArgs.toArray(new Object[0]);*/
    }

    private Object convertToTargetType(String value, Class<?> targetType) {
        return typeConversionService.lookupConverter(targetType)
                .map(conversion -> conversion.execute(value))
                .orElseThrow(() -> new RuntimeException("No converter found for the element:" + value));
    }
}
