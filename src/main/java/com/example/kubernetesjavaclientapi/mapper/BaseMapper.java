package com.example.kubernetesjavaclientapi.mapper;

import java.util.Collection;
import java.util.List;

public interface BaseMapper<S, T> {

    /**
     * Maps the specified source object to an object of type {@code T}.
     *
     * @param source the source object to be mapped
     * @return the resulting object of type {@code T}
     */
    T map(S source);

    /**
     * Maps the specified collection of source objects to a list of objects of type {@code T}.
     *
     * @param sources the collection of source objects to be mapped
     * @return the list of resulting objects of type {@code T}
     */
    List<T> map(Collection<S> sources);

}
