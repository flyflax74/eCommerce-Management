package com.ecommerce.site.shop.model.entity;

import com.ecommerce.site.shop.model.AbstractAddressWithCountry;
import com.ecommerce.site.shop.model.enums.AuthenticationType;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "customers",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "uq_customers_email")})
@Getter
@Setter
@NoArgsConstructor
public class Customer extends AbstractAddressWithCountry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "customers_gen")
    @TableGenerator(name = "customers_gen",
            table = "sequencers",
            pkColumnName = "seq_name",
            valueColumnName = "seq_count",
            pkColumnValue = "customers_seq_next_val",
            allocationSize = 1)
    private Integer id;

    @Column(nullable = false, length = 128)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(nullable = false, columnDefinition = "number(1, 0) default 1")
    private boolean enabled;

    @Column(name = "created_time")
    private Date createdTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type", length = 16)
    private AuthenticationType authenticationType;

    @Column(name = "reset_password_token", length = 32)
    private String resetPasswordToken;

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
