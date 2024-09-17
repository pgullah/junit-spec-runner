package io.github.pgullah.jsr.model;

import io.github.pgullah.jsr.conversion.MethodArgsConverter;
import io.github.pgullah.jsr.conversion.TypeConverter;

import java.util.Optional;

public record TestSpec(String path, Class<?> sourceClass, String method,
                       boolean includeHeader,
                       Optional<Class<? extends MethodArgsConverter>> argsConverter,
                       Optional<Class<? extends TypeConverter<String, ?>>> resultConverter) {

    public TestSpec {
        if (argsConverter.filter(c -> c == MethodArgsConverter.None.class).isPresent()) {
            throw new IllegalArgumentException("Method args converter must not be of None type");
        }

        if (resultConverter.filter(c -> c == TypeConverter.None.class).isPresent()) {
            throw new IllegalArgumentException("Result converter must not be of None type");
        }
    }

    public static Builder testSpecBuilderOf(String path, Class<?> sourceClass, String method) {
        return new Builder(path, sourceClass, method);
    }

    public static final class Builder {
        private final String path;
        private final Class<?> sourceClass;
        private final String method;
        private boolean includeHeader;
        private Class<? extends MethodArgsConverter> argsConverter;
        private Class<? extends TypeConverter<String, ?>> resultConverter;

        private Builder(String path, Class<?> sourceClass, String method) {
            this.path = path;
            this.sourceClass = sourceClass;
            this.method = method;
        }

        public Builder argsConverter(Class<? extends MethodArgsConverter> argsConverter) {
            if (argsConverter != MethodArgsConverter.None.class) {
                this.argsConverter = argsConverter;
            }
            return this;
        }

        public Builder resultConverter(Class<? extends TypeConverter<String, ?>> resultConverter) {
            if (resultConverter != TypeConverter.None.class) {
                this.resultConverter = resultConverter;
            }
            return this;
        }

        public Builder includeHeader(boolean includeHeader) {
            this.includeHeader = includeHeader;
            return this;
        }

        public TestSpec build() {
            return new TestSpec(path, sourceClass, method, includeHeader,
                    Optional.ofNullable(argsConverter),
                    Optional.ofNullable(resultConverter));
        }
    }
}
