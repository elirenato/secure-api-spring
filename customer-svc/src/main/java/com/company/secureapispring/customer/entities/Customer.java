package com.company.secureapispring.customer.entities;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.interfaces.HasOrganization;
import com.company.secureapispring.common.audit.AbstractAuditingEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Data
@Entity
@Table(name = "customers")
public class Customer extends AbstractAuditingEntity implements HasOrganization {

    @JsonView(Customer.ListJsonView.class)
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Customer.ListJsonView.class)
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @JsonView(Customer.ListJsonView.class)
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JsonView(Customer.ListJsonView.class)
    @NotBlank
    @Email
    @Size(min = 5, max = 255)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address2")
    private String address2;

    @Size(min = 1, max = 30)
    @NotBlank
    @Column(name = "postal_code", nullable = false, length = 30)
    private String postalCode;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "state_province_id")
    private StateProvince stateProvince;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false)
    private Organization organization;

    public static class ListJsonView {
        private ListJsonView() {
        }
    }
}
