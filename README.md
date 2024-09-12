## Dynamic Junit Spec Runner
This project simplifies the creation of junit jupiter tests dynamically by specifying the test spec in a simple JSON format.

## Sample Test Runner

```java
import sut.examples.eztest.runner.ClassMethodTaggedWithSpecAnnotation;
import sut.examples.eztest.runner.FizzBuzz;
import sut.examples.eztest.runner.RegexMatching;
import pg.works.junit.spec.runner.AbstractGenericTestRunner;
import provider.eztest.runner.TestSpecProvider;

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
```
![image](assets/images/sample-run-intellij.png)

