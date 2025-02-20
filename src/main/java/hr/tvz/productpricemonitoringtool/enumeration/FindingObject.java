package hr.tvz.productpricemonitoringtool.enumeration;

/**
 * Enumeration for the finding object.
 * Contains the product and company.
 */
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
