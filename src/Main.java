import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);
        final String VALID_PROPERTIES_STRING = Arrays.stream(Property.values())
                .map(Property::name).collect(Collectors.joining(", "));
        final Pattern SPACES_PATTERN = Pattern.compile("\\s+");
        final String INVALID_FIRST_PARAM = "The first parameter should be a natural number or zero.";
        final String INVALID_SECOND_PARAM = "The second parameter should be a natural number.";
        final String HELLO_STRING = new StringBuilder("Welcome to Amazing Numbers!\n")
                .append("Supported requests:\n")
                .append("- enter a natural number to know its properties;\n")
                .append("- enter two natural numbers to obtain the properties of the list:\n")
                .append("  * the first parameter represents a starting number;\n")
                .append("  * the second parameter shows how many consecutive numbers are to be processed;\n")
                .append("- two natural numbers and properties to search for;\n")
                .append("- a property preceded by minus must not be present in numbers;")
                .append("- separate the parameters with one space;\n")
                .append("- enter 0 to exit.\n")
                .toString();

        System.out.println(HELLO_STRING);

        Number prevNumber = new Number(0L, 1L, 1L);

        while (true) {
            System.out.println("Enter a request:");
            String[] inputs = SPACES_PATTERN.split(scanner.nextLine());
            if (inputs.length == 1) {
                if (Number.isWholeNumber(inputs[0])) {
                    Number number = new Number(inputs[0], prevNumber.getSquareEstimate(), prevNumber.getRootOfSquareEstimate());
                    if (number.getValue() == 0L) {
                        System.out.println("Goodbye!");
                        scanner.close();
                        break;
                    } else {
                        number.displayNumberAsSingleton();
                        prevNumber = number;
                    }
                } else {
                    System.out.println(INVALID_FIRST_PARAM);
                }
            } else if (inputs.length == 2) {
                boolean isValidFirstParam = Number.isWholeNumber(inputs[0]);
                if (!isValidFirstParam) {
                    System.out.println(INVALID_FIRST_PARAM);
                }
                boolean isValidSecondParam = Number.isNaturalNumber(inputs[1]);
                if (!isValidSecondParam) {
                    System.out.println(INVALID_SECOND_PARAM);
                }
                boolean areValidParams = isValidFirstParam && isValidSecondParam;
                if (areValidParams) {
                    long start = Long.parseLong(inputs[0]);
                    long size = Long.parseLong(inputs[1]);
                    long stop = start + size;
                    for (long value = start; value < stop; ++value) {
                        Number number = new Number(value, prevNumber.getSquareEstimate(), prevNumber.getRootOfSquareEstimate());
                        number.displayNumberAsMember();
                        prevNumber = number;
                    }
                }
            } else if (inputs.length >= 3) {
                boolean areValidParams = true;
                boolean isValidFirstParam = Number.isWholeNumber(inputs[0]);
                if (!isValidFirstParam) {
                    System.out.println(INVALID_FIRST_PARAM);
                    areValidParams = false;
                }
                boolean isValidSecondParam = Number.isNaturalNumber(inputs[1]);
                if (!isValidSecondParam) {
                    System.out.println(INVALID_SECOND_PARAM);
                    areValidParams = false;
                }
                Set<String> invalidProperties = new HashSet<>();
                Set<Property> includedProperties = new HashSet<>();
                Set<Property> excludedProperties = new HashSet<>();
                for (int ind = 2; ind < inputs.length; ++ind) {
                    boolean shouldExcludeProperty = inputs[ind].charAt(0) == '-';
                    Property property = shouldExcludeProperty ?
                            Property.getPropertyFromString(inputs[ind].substring(1))
                            : Property.getPropertyFromString(inputs[ind]);
                    if (property == null) {
                        invalidProperties.add(inputs[ind].toUpperCase());
                    } else if (shouldExcludeProperty) {
                        excludedProperties.add(property);
                    } else {
                        includedProperties.add(property);
                    }
                }
                if (invalidProperties.size() > 0) {
                    System.out.println(new StringBuilder(invalidProperties.size() > 1 ? "The properties [" : "The property [")
                            .append(String.join(", ", invalidProperties))
                            .append(invalidProperties.size() > 1 ? "] are wrong.\n" : "] is wrong.\n")
                            .append("Available properties: [")
                            .append(VALID_PROPERTIES_STRING)
                            .append("]").toString());
                    areValidParams = false;
                } else {
                    // check no mutually exclusive properties in included properties
                    Set<Property> mutuallyExclusiveProperties = new HashSet<>();
                    for (Property property : includedProperties) {
                        if (includedProperties.contains(property.getMutuallyExclusiveProperty())) {
                            if (!mutuallyExclusiveProperties.contains(property.getMutuallyExclusiveProperty())) {
                                mutuallyExclusiveProperties.add(property);
                            }
                        }
                    }
                    if (mutuallyExclusiveProperties.size() > 0) {
                        StringBuilder printSb = new StringBuilder();
                        for (Property property : mutuallyExclusiveProperties) {
                            printSb.append("The request contains mutually exclusive properties: [")
                                    .append(property.name()).append(", ")
                                    .append(property.getMutuallyExclusiveProperty().name())
                                    .append("]\nThere are no numbers with these properties.\n");
                        }
                        System.out.println(printSb);
                        areValidParams = false;
                    }
                    // check no "complete" mutually exclusive properties in excluded properties
                    mutuallyExclusiveProperties.clear();
                    for (Property property : excludedProperties) {
                        if (property.isComplete() && excludedProperties.contains(property.getMutuallyExclusiveProperty())) {
                            if (!mutuallyExclusiveProperties.contains(property.getMutuallyExclusiveProperty())) {
                                mutuallyExclusiveProperties.add(property);
                            }
                        }
                    }
                    if (mutuallyExclusiveProperties.size() > 0) {
                        StringBuilder printSb = new StringBuilder();
                        for (Property property : mutuallyExclusiveProperties) {
                            printSb.append("The request contains mutually exclusive properties: [-")
                                    .append(property.name()).append(", -")
                                    .append(property.getMutuallyExclusiveProperty().name())
                                    .append("]\nThere are no numbers with these properties.\n");
                        }
                        System.out.println(printSb);
                        areValidParams = false;
                    }
                    // check that included and excluded properties have nothing in common
                    mutuallyExclusiveProperties.clear();
                    mutuallyExclusiveProperties.addAll(includedProperties);
                    mutuallyExclusiveProperties.retainAll(excludedProperties);
                    if (mutuallyExclusiveProperties.size() > 0) {
                        StringBuilder printSb = new StringBuilder();
                        for (Property property : mutuallyExclusiveProperties) {
                            printSb.append("The request contains mutually exclusive properties: [-")
                                    .append(property.name()).append(", ")
                                    .append(property.name())
                                    .append("]\nThere are no numbers with these properties.\n");
                        }
                        System.out.println(printSb);
                        areValidParams = false;
                    }
                }
                if (areValidParams) {
                    long start = Long.parseLong(inputs[0]);
                    long howMany = Long.parseLong(inputs[1]);
                    long count = 0L;
                    long value = start;
                    while (count < howMany) {
                        Number number = new Number(value++, prevNumber.getSquareEstimate(), prevNumber.getRootOfSquareEstimate());
                        boolean result = true;
                        for (Property property : includedProperties) {
                            result = number.getPropertyValue(property);
                            if (!result) {
                                break;
                            }
                        }
                        if (result) {
                            for (Property property : excludedProperties) {
                                result = !number.getPropertyValue(property);
                                if (!result) {
                                    break;
                                }
                            }
                            if (result) {
                                number.displayNumberAsMember();
                                ++count;
                            }
                        }
                        prevNumber = number;
                    }
                }
            }
        }
    }
}

