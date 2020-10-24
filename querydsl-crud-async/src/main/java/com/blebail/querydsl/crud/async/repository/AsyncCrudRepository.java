package com.blebail.querydsl.crud.async.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @param <R>  row type
 * @param <ID> id type
 */
public interface AsyncCrudRepository<R, ID> extends AsyncBaseRepository<R> {

    CompletableFuture<R> save(R resource);

    CompletableFuture<Collection<R>> save(Iterable<R> resources);

    CompletableFuture<Optional<R>> findOne(ID resourceId);

    CompletableFuture<Collection<R>> find(Iterable<ID> resourceIds);

    CompletableFuture<Boolean> exists(ID resourceId);

    CompletableFuture<Boolean> delete(ID resourceId);

    CompletableFuture<Boolean> delete(Iterable<ID> resourceIds);
}
