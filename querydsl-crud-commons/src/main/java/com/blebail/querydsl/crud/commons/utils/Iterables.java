package com.blebail.querydsl.crud.commons.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Because we don't want to pull the whole Guava dependency
 */
public final class Iterables {

    public static <T> int size(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<T>) iterable).size();
        }

        Iterator<T> iterator = iterable.iterator();

        int count;

        for (count = 0; iterator.hasNext(); ++count) {
            iterator.next();
        }

        return count;
    }

    public static <T> Collection<T> asCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection) {
            return (Collection<T>) iterable;
        }

        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }
}
