package hr.tvz.productpricemonitoringtool.enumeration;

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
