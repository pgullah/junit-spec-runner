package io.github.pgullah.jsr.examples.sut;

import io.github.pgullah.jsr.annotation.Spec;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassMethodTaggedWithSpecAnnotation {

    /**
     * @param aList - Arbitrarily deeply nested object
     * @return - flattened list of alternate objects
     */
    @Spec(path = "/specs/nested-list-ops-extract-alt-objs.spec")
    public List<Object> extractAlternateObjects(List<Object> aList) {
        List<Object> result = new LinkedList<>();
        updateRecursively(aList, result);
        return IntStream.range(0, result.size())
                .filter(i -> i %2 ==0)
                .mapToObj(i -> result.get(i))
                .collect(Collectors.toList());
    }

    private void updateRecursively(List<Object> aList, List<Object> result) {
        for (Object item : aList) {
            if(item instanceof List) {
                updateRecursively((List<Object>) item, result);
            } else if(item instanceof String text) {
                result.add(text.toUpperCase());
            } else {
                result.add(item);
            }
        }
    }

}
