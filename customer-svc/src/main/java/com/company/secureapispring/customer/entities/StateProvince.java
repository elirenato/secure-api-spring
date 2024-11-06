package com.company.secureapispring.customer.entities;

import jakarta.persistence.*;
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

    @Column
    private String abbreviation;

    @Column
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id")
    private Country country;
}