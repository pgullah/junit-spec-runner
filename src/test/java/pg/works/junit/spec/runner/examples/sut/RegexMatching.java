package pg.works.junit.spec.runner.examples.sut;

import java.util.Stack;

/**
 * <a href="https://leetcode.com/problems/regular-expression-matching/description/">Regex Matching</a>
 */
public class RegexMatching {

    // Given an input string s and a pattern p, implement regular expression matching with support for '.' and '*' where:
    //
    //'.' Matches any single character.
    //'*' Matches zero or more of the preceding element.
    //The matching should cover the entire input string (not partial).
    // examples:
    //    "aa,a,false",
    //    "aa,a*,true",
    //    "ab,.*,true",
    //     * should match previous char 0 or more times.
    //    "aab,c*a*b,true"
    public static boolean solutionRecursive(String text, String pattern) {
        return matchRecursively(text, pattern, 0, 0);
    }

    public static boolean matchRecursively(String text, String pattern, int ti, int pi) {
        if (pi >= pattern.length() && ti >= text.length()) {
            return true;
        }
        if (pi >= pattern.length()) {
            return false;
        }
        boolean match = ti < text.length() && pattern.charAt(pi) == text.charAt(ti) || pattern.charAt(pi) == '.';
        if (pi + 1 < pattern.length() && pattern.charAt(pi + 1) == '*') {
            // Two options:
            // 1. Skip the '*' and its preceding element (move pi by 2)
            boolean lookAheadMatch = matchRecursively(text, pattern, ti, pi + 2);
            if (lookAheadMatch) {
                return true;
            }
            // 2. If there is a match, continue with ti + 1 to consume more text (keep pi in place)
            return match && matchRecursively(text, pattern, ti + 1, pi);
        }
        // Otherwise, if there's a match, proceed to the next characters
        else if (match) {
            return matchRecursively(text, pattern, ti + 1, pi + 1);
        }
        return false;
    }

    public static boolean solutionRecursiveCached(String text, String pattern) {
        int maxBounds = Math.max(text.length(), pattern.length())+1;
        Boolean[][] cache = new Boolean[maxBounds][maxBounds];
        return matchRecursivelyWithCache(text, pattern, 0, 0, cache);
    }

    public static boolean matchRecursivelyWithCache(String text, String pattern, int ti, int pi, Boolean[][] cache) {
        Boolean cachedResult = cache[ti][pi];
        if (cachedResult != null) {
            return cachedResult;
        }
        if (pi >= pattern.length() && ti >= text.length()) {
            return true;
        }
        if (pi >= pattern.length()) {
            return false;
        }
        boolean match = ti < text.length() && pattern.charAt(pi) == text.charAt(ti) || pattern.charAt(pi) == '.';
        if (pi + 1 < pattern.length() && pattern.charAt(pi + 1) == '*') {
            // Two options:
            // 1. Skip the '*' and its preceding element (move pi by 2)
            boolean lookAheadMatch = matchRecursivelyWithCache(text, pattern, ti, pi + 2, cache);
            boolean result;
            if (lookAheadMatch) {
                result = true;
            }
            // 2. If there is a match, continue with ti + 1 to consume more text (keep pi in place)
            else {
                result = match && matchRecursivelyWithCache(text, pattern, ti + 1, pi, cache);
            }
            cache[ti][pi] = result;
            return result;
        }
        // Otherwise, if there's a match, proceed to the next characters
        else if (match) {
            boolean result = matchRecursively(text, pattern, ti + 1, pi + 1);
            cache[ti][pi] = result;
            return result;
        }
        return false;
    }

    public static boolean solutionIterative(String text, String pattern) {
        // Stack to simulate recursion
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{0, 0});  // Push initial state (ti = 0, pi = 0)

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int ti = current[0], pi = current[1];

            // If both text and pattern are exhausted, return true
            if (pi >= pattern.length() && ti >= text.length()) {
                return true;
            }
            // If pattern is exhausted but text is not, continue
            if (pi >= pattern.length()) {
                continue;
            }

            // Check if characters match or if the pattern has a '.'
            boolean match = ti < text.length() && (pattern.charAt(pi) == text.charAt(ti) || pattern.charAt(pi) == '.');

            // If there's a '*' in the next position
            if (pi + 1 < pattern.length() && pattern.charAt(pi + 1) == '*') {
                // Two options:
                // 1. Skip the '*' and its preceding element (move pi by 2)
                stack.push(new int[]{ti, pi + 2});

                // 2. If there is a match, continue with ti + 1 to consume more text (keep pi in place)
                if (match) {
                    stack.push(new int[]{ti + 1, pi});
                }
            }
            // Otherwise, if there's a match, proceed to the next characters
            else if (match) {
                stack.push(new int[]{ti + 1, pi + 1});
            }
        }

        return false;  // If no solution was found, return false
    }
}

