package pg.works.junit.spec.runner.examples.sut;

public class FizzBuzz {

    public String[] solution(int number) {
        String[] objects = new String[number];
        for (int i = 1; i <= number; i++) {
            String fizzOrBuzz = "";
            if (i % 3 == 0) {
                fizzOrBuzz += "Fizz";
            }
            if (i % 5 == 0) {
                if (!fizzOrBuzz.isEmpty()) {
                    fizzOrBuzz += " ";
                }
                fizzOrBuzz += "Buzz";
            }
            objects[i-1] = fizzOrBuzz.isEmpty() ? ""+i : fizzOrBuzz;
        }
        return objects;
    }
}
