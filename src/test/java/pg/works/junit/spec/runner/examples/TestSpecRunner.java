package pg.works.junit.spec.runner.examples;

import pg.works.junit.spec.runner.examples.sut.ClassMethodTaggedWithSpecAnnotation;
import pg.works.junit.spec.runner.examples.sut.FizzBuzz;
import pg.works.junit.spec.runner.examples.sut.RegexMatching;
import pg.works.junit.spec.runner.AbstractGenericTestRunner;
import pg.works.junit.spec.runner.provider.TestSpecProvider;

public class TestSpecRunner extends AbstractGenericTestRunner {

    @Override
    public TestSpecProvider testSpecProvider() {
        return TestSpecProvider.builder()
                .addAnnotated(ClassMethodTaggedWithSpecAnnotation.class)
                .addSimple("/specs/simple.spec", FizzBuzz.class, "solution")
                .addSimple("/specs/multi-test-methods.spec", RegexMatching.class, "solution.*")
                .build();
    }
}
