package hr.tvz.productpricemonitoringtool.model;

import hr.tvz.productpricemonitoringtool.enumeration.Role;

import java.util.Set;

public class User extends Entity {

    private String surname;
    private String email;
    private String password;
    private Role role;
    private Set<Company> companies;

    public User(Builder builder) {
        super(builder.id, builder.name);
        this.surname = builder.surname;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.companies = builder.companies;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public static class Builder {
        private final Long id;
        private final String name;
        private String surname;
        private String email;
        private String password;
        private Role role;
        private Set<Company> companies;

        public Builder(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder companies(Set<Company> companies) {
            this.companies = companies;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
