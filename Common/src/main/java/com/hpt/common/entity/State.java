package com.hpt.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "states")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Override
    public String toString() {
        return "State [id=" + id + ", name=" + name + "]";
    }
}
