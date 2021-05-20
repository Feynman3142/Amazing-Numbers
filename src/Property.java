import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public enum Property {
    EVEN(true, number -> number.getValue() % 2 == 0),
    ODD(true, number -> !EVEN.getTest().test(number)),
    BUZZ(false, number -> {
        long value = number.getValue();
        boolean isLastDigit7 = number.getString().charAt(number.getString().length() - 1) == '7';
        boolean isDivisibleBy7 = value % 7L == 0L;
        return isLastDigit7 || isDivisibleBy7;
    }),
    DUCK(false, number -> number.getString().indexOf('0') != -1),
    PALINDROMIC(false, number -> {
        String numStr = number.getString();
        boolean isPalindromic = true;
        for (int ind = 0; ind < numStr.length() / 2; ++ind) {
            if (numStr.charAt(ind) != numStr.charAt(numStr.length() - 1 - ind)) {
                isPalindromic = false;
                break;
            }
        }
        return isPalindromic;
    }),
    GAPFUL(false, number -> {
        String numStr = number.getString();
        if (numStr.length() < 3) {
            return false;
        } else {
            long firstDigit = numStr.charAt(0) - '0';
            long lastDigit = numStr.charAt(numStr.length() - 1) - '0';
            long divisor = firstDigit * 10L + lastDigit;
            return number.getValue() % divisor == 0L;
        }
    }),
    SPY(false, number -> {
        long productDigits = 1L;
        long sumDigits = 0L;
        long value = number.getValue();
        while (value > 0L) {
            long lastDigit = value % 10L;
            sumDigits += lastDigit;
            productDigits *= lastDigit;
            value /= 10L;
        }
        return productDigits == sumDigits;
    }),
    SQUARE(false, number -> {
        long value = number.getValue();
        long root = number.getRootOfSquareEstimate();
        long square = number.getSquareEstimate();
        boolean isSquare = false;
        if (square > value) {
            while (square > value) {
                --root;
                square = root * root;
                if (square == value) {
                    isSquare = true;
                    break;
                }
            }
        } else {
            while (square <= value) {
                if (square == value) {
                    isSquare = true;
                    break;
                }
                ++root;
                square = root * root;
            }
        }
        number.setSquareEstimate(square);
        number.setRootOfSquareEstimate(root);
        return isSquare;
    }),
    SUNNY(false, number -> {
        Number nextNumber = new Number(
                number.getValue() + 1L,
                number.getSquareEstimate(),
                number.getRootOfSquareEstimate());
        return nextNumber.getPropertyValue(SQUARE);
    }),
    JUMPING(false, number -> {
        String numStr = number.getString();
        for (int ind = 0; ind < numStr.length() - 1; ++ind) {
            if (Math.abs(numStr.charAt(ind) - numStr.charAt(ind + 1)) != 1) {
                return false;
            }
        }
        return true;
    }),
    HAPPY(true, number -> {
        Set<Long> previousNumbers = new HashSet<>();
        boolean isHappy = false;
        long value = number.getValue();
        while (!isHappy && !previousNumbers.contains(value)) {
            previousNumbers.add(value);
            long sum = 0L;
            while (value > 0) {
                long lastDigit = value % 10L;
                sum += lastDigit * lastDigit;
                value /= 10;
            }
            isHappy = sum == 1L;
            value = sum;
        }
        return isHappy;
    }),
    SAD(true, number -> !HAPPY.getTest().test(number));

    private final String nameInLowerCase;
    // whether this property is true for all numbers
    // or if this property and its mutually exclusive property *that is listed here*
    // is true for all numbers eg: all natural numbers are > 1, all numbers are either odd or even
    // some numbers may neither be square nor sunny -> together they don't complete
    // the set of natural numbers, square and "not square" would complete the set of natural numbers
    // but "not square" is not a listed property in this enum
    private final boolean isComplete;
    private Property mutuallyExclusiveProperty;
    private final Predicate<Number> test;

    static {
        EVEN.mutuallyExclusiveProperty = ODD;
        ODD.mutuallyExclusiveProperty = EVEN;
        BUZZ.mutuallyExclusiveProperty = null;
        DUCK.mutuallyExclusiveProperty = SPY;
        PALINDROMIC.mutuallyExclusiveProperty = null;
        GAPFUL.mutuallyExclusiveProperty = null;
        SPY.mutuallyExclusiveProperty = DUCK;
        SQUARE.mutuallyExclusiveProperty = SUNNY;
        SUNNY.mutuallyExclusiveProperty = SQUARE;
        JUMPING.mutuallyExclusiveProperty = null;
        HAPPY.mutuallyExclusiveProperty = SAD;
        SAD.mutuallyExclusiveProperty = HAPPY;
    }

    Property(boolean isComplete, Predicate<Number> test) {
        this.isComplete = isComplete;
        this.test = test;
        this.nameInLowerCase = this.name().toLowerCase();
    }

    public String getNameInLowerCase() {
        return this.nameInLowerCase;
    }

    public Property getMutuallyExclusiveProperty() {
        return this.mutuallyExclusiveProperty;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public Predicate<Number> getTest() {
        return this.test;
    }

    public static Property getPropertyFromString(String propStr) {
        try {
            return Property.valueOf(propStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
