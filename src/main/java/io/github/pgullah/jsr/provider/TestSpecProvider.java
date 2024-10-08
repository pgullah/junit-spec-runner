package io.github.pgullah.jsr.provider;

import io.github.pgullah.jsr.model.TestSpec;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public interface TestSpecProvider {

    Stream<TestSpec> get();

    static Builder builder() {
        return new Builder();
    }

    class Builder {
        private final List<TestSpecProvider> providerList = new LinkedList<>();

        private Builder() {
        }

        public Builder addAnnotated(Class<?>... classes) {
            providerList.add(new AnnotatedTestSpecProvider(Arrays.asList(classes)));
            return this;
        }

        public Builder addSimple(String specPath, Class<?> clazz, String method) {
            return addSimple(TestSpec.testSpecBuilderOf(specPath, clazz, method).build());
        }

        public Builder addSimple(TestSpec testSpec) {
            providerList.add(() -> Stream.of(testSpec));
            return this;
        }


        public TestSpecProvider build() {
            return new MultiTestSpecProvider(providerList);
        }

    }
}
