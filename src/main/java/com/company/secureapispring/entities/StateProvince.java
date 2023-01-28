package com.company.secureapispring.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name = "state_provinces")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
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