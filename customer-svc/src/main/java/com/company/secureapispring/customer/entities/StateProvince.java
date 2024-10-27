package com.company.secureapispring.customer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Entity
@Table(name = "state_provinces")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class StateProvince implements Serializable {
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

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id")
    private Country country;
}