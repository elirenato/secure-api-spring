package com.company.secureapispring.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "state_provinces")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class StateProvince {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column
    private String abbreviation;

    @Column
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id")
    private Country country;
}