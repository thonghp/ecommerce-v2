package com.hpt.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;
    @Column(name = "phone_number", length = 10, nullable = false)
    private String phoneNumber;
    @Column(name = "image_path", length = 64)
    private String imagePath;
    @Column(length = 128, nullable = false, unique = true)
    private String email;
    @Column(length = 64, nullable = false)
    private String password;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + '}';
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || imagePath == null) return "/images/default-user.png";

        return "/user-photos/" + this.id + "/" + this.imagePath;
    }

    @Transient
    public String getFullName() {
        return lastName + " " + firstName;
    }

    public boolean hasRole(String roleName) {
        Iterator<Role> iterator = roles.iterator();

        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
