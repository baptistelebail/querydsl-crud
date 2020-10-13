package com.blebail.querydsl.crud.async.repository;

import com.blebail.querydsl.crud.commons.resource.IdentifiableQDSLResource;
import com.blebail.querydsl.crud.sync.repository.CrudRepository;
import com.blebail.querydsl.crud.sync.repository.QDSLCrudRepository;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * {@inheritDoc}
 */
public final class QDSLAsyncCrudRepository<T extends RelationalPathBase<R>, R, ID> extends QDSLAsyncBaseRepository<T, R> implements AsyncCrudRepository<R, ID> {

    private final CrudRepository<R, ID> repository;

    public QDSLAsyncCrudRepository(IdentifiableQDSLResource<T, R, ID> qdslResource, SQLQueryFactory queryFactory) {
        super(qdslResource, queryFactory);
        this.repository = new QDSLCrudRepository<>(qdslResource, queryFactory);
    }

    public QDSLAsyncCrudRepository(IdentifiableQDSLResource<T, R, ID> qdslResource, SQLQueryFactory queryFactory, Executor executor) {
        super(qdslResource, queryFactory, executor);
        this.repository = new QDSLCrudRepository<>(qdslResource, queryFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<R> save(R resource) {
        return CompletableFuture.supplyAsync(() -> repository.save(resource), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Collection<R>> save(Iterable<R> resources) {
        return CompletableFuture.supplyAsync(() -> repository.save(resources), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Optional<R>> findOne(ID resourceId) {
        return CompletableFuture.supplyAsync(() -> repository.findOne(resourceId), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Collection<R>> find(Iterable<ID> resourceIds) {
        return CompletableFuture.supplyAsync(() -> repository.find(resourceIds), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Boolean> exists(ID resourceId) {
        return CompletableFuture.supplyAsync(() -> repository.exists(resourceId), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Boolean> delete(ID resourceId) {
        return CompletableFuture.supplyAsync(() -> repository.delete(resourceId), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Boolean> delete(Iterable<ID> resourceIds) {
        return CompletableFuture.supplyAsync(() -> repository.delete(resourceIds), executor);
    }
}
