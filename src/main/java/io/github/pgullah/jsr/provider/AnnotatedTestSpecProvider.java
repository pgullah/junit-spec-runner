package io.github.pgullah.jsr.provider;

import io.github.pgullah.jsr.annotation.Spec;
import io.github.pgullah.jsr.model.TestSpec;
import io.github.pgullah.jsr.util.ReflectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.github.pgullah.jsr.model.TestSpec.testSpecBuilderOf;

public final class AnnotatedTestSpecProvider implements TestSpecProvider {
    private final List<Class<?>> classes;

    AnnotatedTestSpecProvider(List<Class<?>> mandatoryClass) {
        this.classes = mandatoryClass;
    }

    @Override
    public Stream<TestSpec> get() {
        return classes.stream()
                .flatMap(cls -> ReflectUtils.filterClassMethods(cls, method -> lookupAnnotation(method) != null)
                        .map(method -> {
                            final Spec spec = lookupAnnotation(method);
                            return testSpecBuilderOf(spec.path(), cls, method.getName())
                                    .includeHeader(spec.includeHeader())
                                    .argsConverter(spec.argsConverter())
                                    .resultConverter(spec.resultConverter())
                                    .build();
                        }));
    }

    private static Spec lookupAnnotation(Method method) {
        return method.getDeclaredAnnotation(Spec.class);
    }

    public static TestSpecProvider of(Class<?>...classes) {
        return new AnnotatedTestSpecProvider(Arrays.asList(classes));
    }
}
