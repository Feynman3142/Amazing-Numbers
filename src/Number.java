import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Number {

    static final Pattern WHOLE_NUMBER_PATTERN = Pattern.compile("\\+?\\d+");
    static final Pattern NATURAL_NUMBER_PATTERN = Pattern.compile("\\+?[1-9]\\d*");
    private final long value;
    private final String string;
    private final Map<Property, Boolean> propertyValues = new HashMap<>();
    private final Map<Property, Boolean> alreadyCalculated = new HashMap<>();
    private long squareEstimate;
    private long rootOfSquareEstimate;

    private Number(long value, String string, long squareEstimate, long rootOfSquareEstimate) {
        this.value = value;
        this.string = string;
        boolean selectEstimate = Math.abs(squareEstimate - value) < value;
        this.squareEstimate = selectEstimate ? squareEstimate : 1L;
        this.rootOfSquareEstimate = selectEstimate ? rootOfSquareEstimate : 1L;
        for (Property property : Property.values()) {
            this.propertyValues.put(property, false);
            this.alreadyCalculated.put(property, false);
        }
    }

    public Number(String string, long squareEstimate, long rootOfSquareEstimate) {
        this(Long.parseLong(string), string, squareEstimate, rootOfSquareEstimate);
    }

    public Number(long value, long squareEstimate, long rootOfSquareEstimate) {
        this(value, String.valueOf(value), squareEstimate, rootOfSquareEstimate);
    }

    public long getValue() {
        return value;
    }

    public String getString() {
        return string;
    }

    public long getSquareEstimate() {
        return squareEstimate;
    }

    public long getRootOfSquareEstimate() {
        return rootOfSquareEstimate;
    }

    public void setSquareEstimate(long squareEstimate) {
        this.squareEstimate = squareEstimate;
    }

    public void setRootOfSquareEstimate(long rootOfSquareEstimate) {
        this.rootOfSquareEstimate = rootOfSquareEstimate;
    }

    public boolean getPropertyValue(Property property) {
        if (this.alreadyCalculated.get(property)) {
            return this.propertyValues.get(property);
        } else {
            boolean result = property.getTest().test(this);
            this.propertyValues.put(property, result);
            this.alreadyCalculated.put(property, true);
            if (result && property.getMutuallyExclusiveProperty() != null) {
                this.propertyValues.put(property.getMutuallyExclusiveProperty(), false);
                this.alreadyCalculated.put(property.getMutuallyExclusiveProperty(), true);
            }
            return result;
        }
    }

    public void displayNumberAsSingleton() {
        StringBuilder printSb = new StringBuilder("Properties of ").append(this.string).append("\n");
        for (Property property : Property.values()) {
            printSb.append(property.getNameInLowerCase()).append(": ").append(this.getPropertyValue(property)).append("\n");
        }
        System.out.println(printSb.toString());
    }

    public void displayNumberAsMember() {
        StringBuilder propertySb = new StringBuilder(this.string).append(" is ");
        for (Property property : Property.values()) {
            if (this.getPropertyValue(property)) {
                propertySb.append(property.getNameInLowerCase()).append(", ");
            }
        }
        // delete the trailing ", "
        propertySb.delete(propertySb.length() - 2, propertySb.length());
        System.out.println(propertySb.toString());
    }

    public static boolean isWholeNumber(String numStr) {
        return WHOLE_NUMBER_PATTERN.matcher(numStr).matches();
    }

    public static boolean isNaturalNumber(String numStr) {
        return NATURAL_NUMBER_PATTERN.matcher(numStr).matches();
    }
}
