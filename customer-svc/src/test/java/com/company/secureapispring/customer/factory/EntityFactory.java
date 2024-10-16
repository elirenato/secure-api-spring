package com.company.secureapispring.customer.factory;

import com.company.secureapispring.common.factory.EntityBuilder;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.Customer;
import com.company.secureapispring.customer.entities.StateProvince;
import com.github.javafaker.Faker;
import lombok.Getter;

public final class EntityFactory {
    @Getter
    private static final Faker faker = Faker.instance();
    private static int counter;

    private EntityFactory() {
    }

    public static EntityBuilder<Country> country() {
        return EntityBuilder
                .of(Country::new)
                .with(Country::setAbbreviation, faker.country().countryCode3())
                .with(Country::setName, faker.country().name());
    }

    public static EntityBuilder<StateProvince> stateProvince() {
        return EntityBuilder
                .of(StateProvince::new)
                .with(StateProvince::setAbbreviation, faker.address().stateAbbr())
                .with(StateProvince::setName, faker.address().state());
    }

    // Safe generate unique email during tests
    public static String generateUniqueEmail() {
        return faker.internet().emailAddress().replace("@", "+" + (counter++) + "@");
    }

    public static EntityBuilder<Customer> customer() {
        return EntityBuilder
                .of(Customer::new)
                .with(Customer::setFirstName, faker.name().firstName())
                .with(Customer::setLastName, faker.name().lastName())
                .with(Customer::setEmail, generateUniqueEmail())
                .with(Customer::setAddress, faker.address().streetAddressNumber())
                .with(Customer::setAddress2, faker.address().secondaryAddress())
                .with(Customer::setPostalCode, faker.address().zipCode());
    }
}
