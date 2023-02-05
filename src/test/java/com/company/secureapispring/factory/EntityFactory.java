package com.company.secureapispring.factory;

import com.company.secureapispring.entities.Country;
import com.company.secureapispring.entities.Customer;
import com.company.secureapispring.entities.StateProvince;
import com.github.javafaker.Faker;

public final class EntityFactory {
    private static Faker faker = Faker.instance().instance();

    public static Faker getFaker() {
        return faker;
    }

    private EntityFactory() {}

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

    public static EntityBuilder<Customer> customer() {
        return EntityBuilder
                .of(Customer::new)
                .with(Customer::setFirstName, faker.name().firstName())
                .with(Customer::setLastName, faker.name().lastName())
                .with(Customer::setEmail, faker.internet().emailAddress())
                .with(Customer::setAddress, faker.address().streetAddressNumber())
                .with(Customer::setAddress2, faker.address().secondaryAddress())
                .with(Customer::setPostalCode, faker.address().zipCode());
    }
}
