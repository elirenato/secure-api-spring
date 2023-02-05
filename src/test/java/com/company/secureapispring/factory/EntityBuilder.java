package com.company.secureapispring.factory;

import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EntityBuilder<T> {
    private final Supplier<T> instantiation;

    private List<Consumer<T>> instanceModifiers = new ArrayList<>();

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

    /**
     * Create the entity with mock data but does not persist it
     *
     * @return
     */
    public T build() {
        T instance = instantiation.get();
        instanceModifiers.forEach(modifier -> modifier.accept(instance));
        instanceModifiers.clear();
        return instance;
    }

    /**
     * Create the entity with mock data and persist it
     *
     * @param em
     * @return
     */
    public T build(EntityManager em) {
        T value = build();
        em.persist(value);
        em.flush();
        em.clear();
        return value;
    }
}
