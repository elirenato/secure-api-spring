package com.company.secureapispring.core.services;

import com.company.secureapispring.core.entities.Customer;
import com.company.secureapispring.core.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CustomerService {
    private CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer input) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(input, customer);
        return customerRepository.save(customer);
    }

    public Customer update(Customer input) {
        Customer customer = customerRepository.findById(input.getId()).orElseThrow(EntityNotFoundException::new);
        BeanUtils.copyProperties(input, customer);
        return customerRepository.save(customer);
    }

    public Customer delete(Long id) {
        Customer customer = customerRepository.findByIdWithStateProvince(id).orElseThrow(EntityNotFoundException::new);
        customerRepository.delete(customer);
        return customer;
    }

    public Customer get(Long id) {
        return customerRepository.findByIdWithStateProvince(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Customer> listAll() {
        return customerRepository.findAll(Sort.by("lastName", "firstName"));
    }
}
