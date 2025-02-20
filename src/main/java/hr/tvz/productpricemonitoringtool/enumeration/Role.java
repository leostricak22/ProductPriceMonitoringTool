package hr.tvz.productpricemonitoringtool.enumeration;

/**
 * Enumeration for the role.
 * Contains the admin, merchant and customer.
 */
public enum Role {
    ADMIN,
    MERCHANT,
    CUSTOMER;

    public static Role getRole(String name) {
        for (Role role : Role.values()) {
            if (role.name().equals(name)) {
                return role;
            }
        }

        return null;
    }
}
