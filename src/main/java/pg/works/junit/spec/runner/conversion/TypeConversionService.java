package pg.works.junit.spec.runner.conversion;

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
                new String[]{"true", "TRUE", "Y"}, new String[]{"false", "FALSE", "N"})
        );
        converterRegistry.put(String.class, PassThroughConversion.INSTANCE);
    }

    public Optional<Conversion> lookupConverter(Class<?> targetType) {
        Conversion<String, ?> conversion = converterRegistry.get(targetType);
        if (conversion == null) {
            if (targetType.isArray() || targetType.isAssignableFrom(List.class)) {
                conversion = new IterableConversion(this, targetType);
            }
        }
        return Optional.ofNullable(conversion);
    }

    private static class PassThroughConversion extends ObjectConversion<String> {
        private static final PassThroughConversion INSTANCE = new PassThroughConversion();

        private PassThroughConversion() {
            // no init;
        }

        @Override
        protected String fromString(String s) {
            return s;
        }
    }

    public <S, T> void registerConverter(Class<S> sourceType, Conversion<String, T> targetType) {
        converterRegistry.put(sourceType, targetType);
    }
}
