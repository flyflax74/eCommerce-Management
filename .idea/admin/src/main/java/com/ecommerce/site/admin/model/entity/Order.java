package com.ecommerce.site.admin.model.entity;

import com.ecommerce.site.admin.model.AbstractAddress;
import com.ecommerce.site.admin.model.enums.OrderStatus;
import com.ecommerce.site.admin.model.enums.PaymentMethod;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends AbstractAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "orders_gen")
    @TableGenerator(name = "orders_gen",
            table = "sequencers",
            pkColumnName = "seq_name",
            valueColumnName = "seq_count",
            pkColumnValue = "orders_seq_next_val",
            allocationSize = 1
    )
    private Integer id;

    @Column(nullable = false, length = 64)
    private String country;

    private Date orderTime;

    private float shippingCost;

    private float productCost;

    private float subtotal;

    private float tax;

    private float total;

    private int deliverDays;

    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_orders_customers"))
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private Set<OrderTrack> orderTracks = new HashSet<>();

    public Order(Integer id, Date orderTime, float productCost, float subtotal, float total) {
        this.id = id;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

    public void copyAddressFromCustomer() {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setCountry(customer.getCountry().getName());
        setPostalCode(customer.getPostalCode());
        setState(customer.getState());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", subtotal=" + subtotal +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", customer=" + customer.getFullName() +
                '}';
    }

    @Transient
    public String getDestination() {
        String destination =  city + ", ";
        if (state != null && !state.isEmpty()) {
            destination += state + ", ";
        }
        destination += country;

        return destination;
    }

    public void copyShippingAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setCountry(address.getCountry().getName());
        setPostalCode(address.getPostalCode());
        setState(address.getState());
    }

    @Transient
    public String getShippingAddress() {
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

        address += ", " + country;

        if (!postalCode.isEmpty()) {
            address += ". Postal Code: " + postalCode;
        }
        if (!phoneNumber.isEmpty()) {
            address += ". Phone Number: " + phoneNumber;
        }

        return address;
    }

    public Set<OrderTrack> getOrderTracks() {
        return orderTracks;
    }

    public void setOrderTracks(Set<OrderTrack> orderTracks) {
        this.orderTracks = orderTracks;
    }

    @Transient
    public String getDeliverDateOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormatter.format(this.deliverDate);
    }

    public void setDeliverDateOnForm(String dateString) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            this.deliverDate = dateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Transient
    public String getRecipientName() {
        String name = firstName;
        if (lastName != null && !lastName.isEmpty()) {
            name += " " + lastName;
        }
        return name;
    }

    @Transient
    public String getRecipientAddress() {
        String address = addressLine1;

        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address += ", " + addressLine2;
        }
        if (!city.isEmpty()) {
            address += ", " + city;
        }
        if (state != null && !state.isEmpty()) {
            address += ", " + state;
        }
        address += ", " + country;
        if (!postalCode.isEmpty()) {
            address += ". " + postalCode;
        }
        return address;
    }

    @Transient
    public boolean isCod() {
        return paymentMethod.equals(PaymentMethod.COD);
    }

    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESSING);
    }

    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.PICKED);
    }

    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.SHIPPING);
    }

    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }

    @Transient
    public boolean isReturnRequested() {
        return hasStatus(OrderStatus.RETURN_REQUESTED);
    }

    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNED);
    }

    public boolean hasStatus(OrderStatus status) {
        for (OrderTrack aTrack : orderTracks) {
            if (aTrack.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getProductNames() {
        StringBuilder productNames;
        productNames = new StringBuilder("<ul>");

        for (OrderDetail detail : orderDetails) {
            productNames.append("<li>").append(detail.getProduct().getShortName()).append("</li>");
        }
        productNames.append("</ul>");

        return productNames.toString();
    }
}
