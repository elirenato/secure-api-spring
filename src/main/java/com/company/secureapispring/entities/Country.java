package com.company.secureapispring.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @EqualsAndHashCode.Include
    private Integer id;

    private String abbreviation;

    private String name;
}
