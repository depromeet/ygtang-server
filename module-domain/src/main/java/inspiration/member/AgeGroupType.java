package inspiration.member;

public enum AgeGroupType {

    UNDER_20S("20대 미만"),
    EARLY_20S("20~24세"),
    LATE_20S("25~29세"),
    EARLY_30S("30~34세"),
    OLDER_35("35세 이상");

    private final String message;

    AgeGroupType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
