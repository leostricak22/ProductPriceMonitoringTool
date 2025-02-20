package hr.tvz.productpricemonitoringtool.enumeration;

public enum FindingObject {
    PRODUCT("product"),
    COMPANY("company");

    private final String value;

    FindingObject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
