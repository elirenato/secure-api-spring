package com.company.secureapispring.customer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "countries")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @NotEmpty
    @Column
    private String abbreviation;

    @NotEmpty
    @Column
    private String name;
}
