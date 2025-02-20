package hr.tvz.productpricemonitoringtool.model.dbo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User company database object.
 * Represents the user company in the database.
 */
public class UserCompanyDBO implements Serializable {

    private Long id;
    private Long userId;
    private Long companyId;
    private LocalDateTime createdAt;

    public UserCompanyDBO(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.companyId = builder.companyId;
        this.createdAt = builder.createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long companyId;
        private LocalDateTime createdAt;

        public Builder(Long id) {
            this.id = id;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder companyId(Long companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserCompanyDBO build() {
            return new UserCompanyDBO(this);
        }
    }
}
