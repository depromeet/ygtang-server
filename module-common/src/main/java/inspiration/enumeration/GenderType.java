package inspiration.enumeration;

public enum GenderType {

    FEMALE("F"),
    MALE("E"),
    ETC("N");

    private final String message;

    GenderType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
