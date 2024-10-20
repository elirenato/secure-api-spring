package com.company.secureapispring.customer.services;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.services.AuthService;
import com.company.secureapispring.customer.entities.Customer;
import com.company.secureapispring.customer.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final AuthService authService;
    private final CustomerRepository customerRepository;

    public Customer create(Customer input) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(input, customer);
        authService.setOrganization(customer);
        return customerRepository.save(customer);
    }

    public Customer update(Customer input) {
        Customer customer = customerRepository.findById(input.getId()).orElseThrow(EntityNotFoundException::new);
        authService.validateOwnership(customer);
        BeanUtils.copyProperties(input, customer);
        return customerRepository.save(customer);
    }

    public Customer delete(Long id) {
        Customer customer = customerRepository.findByIdWithStateProvince(id).orElseThrow(EntityNotFoundException::new);
        authService.validateOwnership(customer);
        customerRepository.delete(customer);
        return customer;
    }

    public Customer get(Long id) {
        Customer customer = customerRepository.findByIdWithStateProvince(id)
                .orElseThrow(EntityNotFoundException::new);
        authService.validateOwnership(customer);
        return customer;
    }

    public List<Customer> listAll() {
        Organization organization = authService.getAuthenticatedOrganization();
        return customerRepository.findAllByOrganizationId(organization.getId(), Sort.by("lastName", "firstName"));
    }
}
