package io.github.pgullah.jsr.provider;

import io.github.pgullah.jsr.model.TestSpec;

import java.util.List;
import java.util.stream.Stream;

public final class MultiTestSpecProvider implements TestSpecProvider {
    private final List<TestSpecProvider> providerList;

    public MultiTestSpecProvider(List<TestSpecProvider> providerList) {
        this.providerList = providerList;
    }

    @Override
    public Stream<TestSpec> get() {
        return this.providerList.stream().flatMap(TestSpecProvider::get);
    }
}
