package com.company.secureapispring.core.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity(name = "countries")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column
    private String abbreviation;

    @Column
    private String name;
}
