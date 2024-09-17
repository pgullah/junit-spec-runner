package io.github.pgullah.jsr.conversion;

import org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TypeConversionService {
    private final Map<Class<?>, Conversion<String, ?>> converterRegistry = new HashMap<>();

    {
        converterRegistry.put(int.class, new IntegerConversion());
        converterRegistry.put(long.class, new LongConversion());
        converterRegistry.put(float.class, new FloatConversion());
        converterRegistry.put(double.class, new DoubleConversion());
        converterRegistry.put(boolean.class, new BooleanConversion(
                new String[]{"true", "True", "TRUE", "Y", "n"}, new String[]{"false", "False", "FALSE", "N", "n"})
        );
        converterRegistry.put(String.class, NoopConversion.INSTANCE);
    }

    private Optional<Conversion> lookupConverter(Class<?> targetType) {
        Conversion<String, ?> conversion = converterRegistry.get(targetType);
        if (conversion == null) {
            if (targetType.isArray() || targetType.isAssignableFrom(List.class)) {
                conversion = new IterableConversion(this, targetType);
            }
        }
        return Optional.ofNullable(conversion);
    }

    public <T> T convert(Object value, Class<T> targetType) {
        final Conversion conversion = lookupConverter(targetType).orElseThrow(() -> new RuntimeException("No converter found for the element:" + value));
        return (T) conversion.execute(value);
    }

    public <S, T> void registerConverter(Class<S> sourceType, Conversion<String, T> targetType) {
        converterRegistry.put(sourceType, targetType);
    }

    private static class NoopConversion extends ObjectConversion<String> {
        private static final NoopConversion INSTANCE = new NoopConversion();

        private NoopConversion() {
            // no init;
        }

        @Override
        protected String fromString(String s) {
            return s;
        }
    }
}
