package com.blebail.querydsl.crud.async.repository;

import com.blebail.querydsl.crud.commons.page.Page;
import com.blebail.querydsl.crud.commons.page.PageRequest;
import com.blebail.querydsl.crud.commons.resource.QDSLResource;
import com.blebail.querydsl.crud.sync.repository.BaseRepository;
import com.blebail.querydsl.crud.sync.repository.QDSLBaseRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * {@inheritDoc}
 */
public class QDSLAsyncBaseRepository<T extends RelationalPathBase<R>, R> implements AsyncBaseRepository<R> {

    private final BaseRepository<R> repository;

    protected final Executor executor;

    public QDSLAsyncBaseRepository(QDSLResource<T, R> qdslResource, SQLQueryFactory queryFactory) {
        this(qdslResource, queryFactory, Executors.newFixedThreadPool(2));
    }

    public QDSLAsyncBaseRepository(QDSLResource<T, R> qdslResource, SQLQueryFactory queryFactory, Executor executor) {
        this.repository = new QDSLBaseRepository<>(qdslResource, queryFactory);
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Optional<R>> findOne(Predicate predicate) {
        return CompletableFuture.supplyAsync(() -> repository.findOne(predicate), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Collection<R>> find(Predicate predicate) {
        return CompletableFuture.supplyAsync(() -> repository.find(predicate), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Page<R>> find(PageRequest pageRequest) {
        return CompletableFuture.supplyAsync(() -> repository.find(pageRequest), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Page<R>> find(Predicate predicate, PageRequest pageRequest) {
        return CompletableFuture.supplyAsync(() -> repository.find(predicate, pageRequest), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Collection<R>> findAll() {
        return CompletableFuture.supplyAsync(repository::findAll, executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Long> count(Predicate predicate) {
        return CompletableFuture.supplyAsync(() -> repository.count(predicate), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(repository::count, executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Boolean> delete(Predicate predicate) {
        return CompletableFuture.supplyAsync(() -> repository.delete(predicate), executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Boolean> deleteAll() {
        return CompletableFuture.supplyAsync(repository::deleteAll, executor);
    }
}
