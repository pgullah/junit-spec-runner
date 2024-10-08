package io.github.pgullah.jsr.examples;

import io.github.pgullah.jsr.AbstractGenericTestRunner;
import io.github.pgullah.jsr.examples.sut.ClassMethodTaggedWithSpecAnnotation;
import io.github.pgullah.jsr.examples.sut.FizzBuzz;
import io.github.pgullah.jsr.examples.sut.RegexMatching;
import io.github.pgullah.jsr.provider.TestSpecProvider;

import static io.github.pgullah.jsr.model.TestSpec.testSpecBuilderOf;

public class TestSpecRunner extends AbstractGenericTestRunner {

    @Override
    public TestSpecProvider testSpecProvider() {
        return TestSpecProvider.builder()
                .addAnnotated(ClassMethodTaggedWithSpecAnnotation.class)
                .addSimple("/specs/simple.spec", FizzBuzz.class, "solution")
                .addSimple(testSpecBuilderOf("/specs/simple-with-header.spec", FizzBuzz.class, "solution")
                        .includeHeader(true)
                        .build()
                )
                .addSimple("/specs/multi-test-methods.spec", RegexMatching.class, "solution.*")
                .build();
    }
}
