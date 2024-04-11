package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.annotation.FieldsValueMatch;
import com.ecommerce.site.admin.annotation.PasswordValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_SERVICE_ACTIVE;
import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_BASE_URI;


@Entity
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}, name = "uq_user_email")})
@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "password",
                fieldMatch = "confirmPassword",
                message = "User password do not match"
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"photos", "password"})
@Builder(toBuilder = true)
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "users_gen")
    @TableGenerator(name = "users_gen",
            table = "sequencers",
            pkColumnName = "seq_name",
            valueColumnName = "seq_count",
            pkColumnValue = "users_seq_next_val",
            allocationSize = 1)
    private Integer id;

    @Column(nullable = false, length = 128)
    @NotBlank(message = "User email is required")
    @Email(message = "User email must be a valid email address")
    private String email;

    @Column(nullable = false, length = 64)
    @NotBlank(message = "User password is required")
    @Size(min = 6, max = 64, message = "User password must be between 6 and 64 characters")
    @PasswordValidator
    @JsonIgnore
    private String password;

    @Transient
    @JsonIgnore
    @NotBlank(message = "Confirm password is required")
    @Size(min = 6, max = 64, message = "Confirm password must be between 6 and 64 characters")
    private String confirmPassword;

    @Column(name = "first_name", nullable = false, length = 64)
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 64, message = "First name must be between 3 and 64 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 64)
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 64, message = "Last name must be between 3 and 64 characters")
    private String lastName;

    @Transient
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    @Column(length = 64)
    private String photos;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_users_roles_users"),
            inverseForeignKey = @ForeignKey(name = "fk_users_roles_roles"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})})
    private Set<Role> roles = new HashSet<>();

    @Column(name = "account_non_expired", nullable = false, insertable = false)
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false, insertable = false)
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false, insertable = false)
    private boolean credentialsNonExpired = true;

    @Column(name = "failed_attempt", nullable = false, insertable = false)
    private int failedAttempt = 0;

    @Column(name = "last_modified")
    private Date lastModified;

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || photos == null) {
            return "/images/default-user.png";
        }
        if (S3_SERVICE_ACTIVE) {
            return S3_BASE_URI +  "/user-photos/" + this.id + "/" + this.photos;
        }
        return "/user-photos/" + this.id + "/" + this.photos;
    }
}