package pg.works.junit.spec.runner.provider;

import pg.works.junit.spec.runner.model.TestSpec;

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
