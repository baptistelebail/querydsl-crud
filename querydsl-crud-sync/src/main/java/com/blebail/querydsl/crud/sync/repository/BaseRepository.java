package com.blebail.querydsl.crud.sync.repository;

import com.blebail.querydsl.crud.commons.page.Page;
import com.blebail.querydsl.crud.commons.page.PageRequest;
import com.querydsl.core.types.Predicate;

import java.util.Collection;
import java.util.Optional;

/**
 * @param <R> row type
 */
public interface BaseRepository<R> {

    Optional<R> findOne(Predicate predicate);

    Collection<R> find(Predicate predicate);

    Page<R> find(PageRequest pageRequest);

    Page<R> find(Predicate predicate, PageRequest pageRequest);

    Collection<R> findAll();

    long count(Predicate predicate);

    long count();

    boolean delete(Predicate predicate);

    boolean deleteAll();
}
