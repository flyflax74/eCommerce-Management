package com.ecommerce.site.shop.model;

import com.ecommerce.site.shop.model.entity.Country;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass
@Getter
@Setter
public class AbstractAddressWithCountry extends AbstractAddress {

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Country.class)
    @JoinColumn(name = "COUNTRY_ID")
    protected Country country;

    @Override
    public String toString() {
        String address = firstName;

        if (lastName != null && !lastName.isEmpty()) {
            address += " " + lastName;
        }

        if (!addressLine1.isEmpty()) {
            address += ", " + addressLine1;
        }

        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address += ", " + addressLine2;
        }

        if (!city.isEmpty()) {
            address += ", " + city;
        }

        if (state != null && !state.isEmpty()) {
            address += ", " + state;
        }

        address += ", " + country.getName();

        if (!postalCode.isEmpty()) {
            address += ". Postal Code: " + postalCode;
        }

        if (!phoneNumber.isEmpty()) {
            address += ". Phone Number: " + phoneNumber;
        }

        return address;
    }

}
