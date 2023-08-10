package com.hpt.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 64)
    private String addressLine1;

    @Column(length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    private boolean enabled;

    @Column(name = "created_time")
    private Date createdTime;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Override
    public String toString() {
        return "Customer [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }
}
