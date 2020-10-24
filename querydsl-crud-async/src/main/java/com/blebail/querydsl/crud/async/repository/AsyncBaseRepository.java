package com.blebail.querydsl.crud.async.repository;

import com.blebail.querydsl.crud.commons.page.Page;
import com.blebail.querydsl.crud.commons.page.PageRequest;
import com.querydsl.core.types.Predicate;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @param <R> row type
 */
public interface AsyncBaseRepository<R> {

    CompletableFuture<Optional<R>> findOne(Predicate predicate);

    CompletableFuture<Collection<R>> find(Predicate predicate);

    CompletableFuture<Page<R>> find(PageRequest pageRequest);

    CompletableFuture<Page<R>> find(Predicate predicate, PageRequest pageRequest);

    CompletableFuture<Collection<R>> findAll();

    CompletableFuture<Long> count(Predicate predicate);

    CompletableFuture<Long> count();

    CompletableFuture<Boolean> delete(Predicate predicate);

    CompletableFuture<Boolean> deleteAll();
}
