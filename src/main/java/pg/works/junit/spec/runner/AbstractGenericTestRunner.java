package pg.works.junit.spec.runner;

import pg.works.junit.spec.runner.model.TestSpec;
import pg.works.junit.spec.runner.util.ReflectUtils;
import pg.works.junit.spec.runner.conversion.TypeConversionService;
import pg.works.junit.spec.runner.provider.TestSpecProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Parent class for any Dynamic test runner classes for more customization
 */
public abstract class AbstractGenericTestRunner {
    private final TypeConversionService typeConversionService = new TypeConversionService();

    @TestFactory
    final Stream<DynamicContainer> dynamicTestsRunner() {
        return testSpecProvider().get().flatMap(this::buildDynamicTests);
    }

    public abstract TestSpecProvider testSpecProvider();

    protected void registerCustomerConverters(TypeConversionService typeConversionService) {
    }

    private Stream<DynamicContainer> buildDynamicTests(TestSpec testSpec) {
        final String path = testSpec.path();
        final Pattern methodPattern = Pattern.compile(testSpec.method());
        final Class classToTest = testSpec.sourceClass();
        return ReflectUtils.filterClassMethods(classToTest, clazzMethod -> methodPattern.matcher(clazzMethod.getName()).matches())
                .map(method -> buildDynamicTests(path, classToTest, method))
                .map(tests -> DynamicContainer.dynamicContainer(classToTest.getName(), tests));
    }

    private Stream<DynamicTest> buildDynamicTests(String path, Class classToTest, Method method) {
        CsvParserSettings settings = new CsvParserSettings();
//        settings.setHeaderExtractionEnabled(true);
        final Class<?>[] methodParameterTypes = method.getParameterTypes();
        final Class<?> methodReturnType = method.getReturnType();
        final CsvParser csvParser = new CsvParser(settings);
        try (var input = new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(path)))) {
            Stream.Builder<DynamicTest> streamBuilder = Stream.builder();
            final var recordIterator = csvParser.iterateRecords(input).iterator();
            for (int recordIdx = 0; recordIterator.hasNext(); recordIdx++) {
                final var record = recordIterator.next();
                final int columnCount = record.getValues().length;
                final int methodParameterCount = methodParameterTypes.length;
                final int lastColumnIndex = columnCount - 1;
                final int inputParamCount = columnCount - 1;
                assertEquals(methodParameterCount, inputParamCount,
                        "Expected parameter count and input parameter count should match");
                final Object[] methodArgs = IntStream.range(0, methodParameterTypes.length)
                        .mapToObj(i -> convertToTargetType(record.getString(i), methodParameterTypes[i]))
                        .toArray();
                final Object actual = ReflectUtils.executeMethod(classToTest, method, methodArgs);
                final Object expected = convertToTargetType(record.getString(lastColumnIndex), methodReturnType);
                streamBuilder.add(dynamicTest(formatTestCaseName(recordIdx, method, record.getValues()), () -> {
                    if (methodReturnType.isArray()) {
                        Assertions.assertArrayEquals((Object[]) expected, (Object[]) actual);
                    } else {
                        Assertions.assertEquals(expected, actual);
                    }
                }));
            }
            return streamBuilder.build();
        } catch (Throwable ex) {
            return Stream.of(
                    dynamicTest(formatTestCaseName(0, method, new Object[]{}), () -> assertionFailure().cause(ex).buildAndThrow())
            );
        }
    }

    protected String formatTestCaseName(int testCaseIndex, Method method, Object[] params) {
        final String paramsCsv = Arrays.stream(params).map(Object::toString).collect(Collectors.joining(","));
        return String.format("[%d] %s(): %s", testCaseIndex, method.getName(), paramsCsv);
    }

    private Object convertToTargetType(String value, Class<?> targetType) {
        return typeConversionService.lookupConverter(targetType)
                .map(c -> c.execute(value))
                .orElseThrow(() -> new RuntimeException("No converter found for the element:" + value));
    }

}
