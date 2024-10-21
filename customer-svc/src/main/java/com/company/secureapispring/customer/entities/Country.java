package com.company.secureapispring.customer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "countries")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Cache(region = "countries", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
