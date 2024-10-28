package com.company.secureapispring.customer.factory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EntityBuilder<T> {
    private final Supplier<T> instantiation;

    private final List<Consumer<T>> instanceModifiers = new ArrayList<>();

    private EntityBuilder(Supplier<T> instantiation) {
        this.instantiation = instantiation;
    }

    public static <T> EntityBuilder<T> of(Supplier<T> instantiator) {
        return new EntityBuilder<T>(instantiator);
    }

    public <U> EntityBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> modifier = instance -> consumer.accept(instance, value);
        instanceModifiers.add(modifier);
        return this;
    }

    public T make() {
        T instance = instantiation.get();
        instanceModifiers.forEach(modifier -> modifier.accept(instance));
        instanceModifiers.clear();
        return instance;
    }

    public <ID> T persit(JpaRepository<T, ID> repo) {
        T value = make();
        repo.save(value);
        return value;
    }
}
