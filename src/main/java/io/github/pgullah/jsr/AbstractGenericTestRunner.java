package io.github.pgullah.jsr;

import io.github.pgullah.jsr.conversion.MethodArgsConverter;
import io.github.pgullah.jsr.conversion.ReflectiveMethodArgsConverter;
import io.github.pgullah.jsr.conversion.TypeConversionService;
import io.github.pgullah.jsr.conversion.TypeConverter;
import io.github.pgullah.jsr.model.TestSpec;
import io.github.pgullah.jsr.provider.TestSpecProvider;
import io.github.pgullah.jsr.util.ReflectUtils;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
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

    protected String formatTestCaseName(int testCaseIndex, Method method, Object[] params) {
        final String paramsCsv = Arrays.stream(params).map(Object::toString).collect(Collectors.joining(","));
        return String.format("[%d] %s(): %s", testCaseIndex, method.getName(), paramsCsv);
    }

    /**
     * Hook to register your own custom converters
     *
     * @see org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversion
     */
    protected void registerCustomerConverters(TypeConversionService typeConversionService) {
    }

    private Stream<DynamicContainer> buildDynamicTests(TestSpec testSpec) {
        final Pattern methodPattern = Pattern.compile(testSpec.method());
        final Class<?> classToTest = testSpec.sourceClass();
        return ReflectUtils.filterClassMethods(classToTest, clazzMethod -> methodPattern.matcher(clazzMethod.getName()).matches())
                .map(method -> buildDynamicTests(testSpec, method))
                .map(tests -> dynamicContainer(classToTest.getName(), tests));
    }

    private Stream<DynamicTest> buildDynamicTests(TestSpec testSpec, Method method) {
        final String path = testSpec.path();
        final Class<?> classToTest = testSpec.sourceClass();
        final CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(testSpec.includeHeader());
        final Class<?>[] methodParameterTypes = method.getParameterTypes();
        final Class<?> methodReturnType = method.getReturnType();
        final Supplier<MethodArgsConverter> defaultMethodArgsConverter = () -> new ReflectiveMethodArgsConverter(typeConversionService, methodParameterTypes);
        final Supplier<TypeConverter<String, ?>> defaultResultConverter = () -> input -> typeConversionService.convert(input, methodReturnType);
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
                assertEquals(methodParameterCount, inputParamCount, "Expected parameter count and input parameter count should match");
                final Object[] methodArgs = getOrDefaultTypeConverter(testSpec.argsConverter(), defaultMethodArgsConverter)
                        .convert(record.getValues());
                final Object actual = ReflectUtils.executeMethod(classToTest, method, methodArgs);
                final Object expected = getOrDefaultTypeConverter(testSpec.resultConverter(), defaultResultConverter)
                        .convert(record.getString(lastColumnIndex));
                final var testCaseName = formatTestCaseName(recordIdx, method, record.getValues());
                final var dynamicTest = dynamicTest(testCaseName, () -> {
                    if (methodReturnType.isArray()) {
                        assertArrayEquals((Object[]) expected, (Object[]) actual);
                    } else {
                        assertEquals(expected, actual);
                    }
                });
                streamBuilder.add(dynamicTest);
            }
            return streamBuilder.build();
        } catch (Throwable err) {
            return Stream.of(
                    dynamicTest(formatTestCaseName(0, method, new Object[]{}), () -> assertionFailure().cause(err).buildAndThrow())
            );
        }
    }

    private static <TC extends TypeConverter<?, ?>> TC getOrDefaultTypeConverter(
            final Optional<Class<? extends TC>> baseConverter, final Supplier<TC> defaultMethodArgsConverter) {
        return baseConverter
                .map((clazz) -> (TC) ReflectUtils.newInstance(clazz))
                .orElseGet(defaultMethodArgsConverter);
    }

}
