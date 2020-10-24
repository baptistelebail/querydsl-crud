package com.blebail.querydsl.crud.sync.repository;

import com.blebail.querydsl.crud.commons.page.Page;
import com.blebail.querydsl.crud.commons.page.PageRequest;
import com.blebail.querydsl.crud.commons.resource.QDSLResource;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class QDSLBaseRepository<T extends RelationalPathBase<R>, R> implements BaseRepository<R> {

    private final QDSLResource<T, R> qdslResource;

    protected final SQLQueryFactory queryFactory;

    public QDSLBaseRepository(QDSLResource<T, R> qdslResource, SQLQueryFactory queryFactory) {
        this.qdslResource = Objects.requireNonNull(qdslResource);
        this.queryFactory = Objects.requireNonNull(queryFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<R> findOne(Predicate predicate) {
        Objects.requireNonNull(predicate, "Predicate should not be null when finding a single resource");

        return Optional.ofNullable(queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath())
                .where(predicate)
                .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<R> find(Predicate predicate) {
        SQLQuery<R> query = queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath());

        if (predicate != null) {
            query = query.where(predicate);
        }

        return query.fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<R> find(PageRequest pageRequest) {
        return find(null, pageRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<R> find(Predicate predicate, PageRequest pageRequest) {
        long totalItems = count(predicate);
        long totalPages = Page.totalPages(totalItems, pageRequest.size());
        OrderSpecifier[] orders = qdslResource.orders(pageRequest);

        SQLQuery<R> query = queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath())
                .limit(pageRequest.size())
                .offset(pageRequest.offset())
                .orderBy(orders);

        if (predicate != null) {
            query = query.where(predicate);
        }

        return new Page<>(query.fetch(), totalItems, totalPages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<R> findAll() {
        return queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath())
                .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(Predicate predicate) {
        SQLQuery<R> query = queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath());

        if (predicate != null) {
            query = query.where(predicate);
        }

        return query.fetchCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath())
                .fetchCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Predicate predicate) {
        Objects.requireNonNull(predicate, "Predicate should not be null on a delete, otherwise whole table will be deleted, " +
                "call deleteAll() instead if it's the desired operation");

        return queryFactory.delete(qdslResource.rowPath()).where(predicate).execute() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteAll() {
        return queryFactory.delete(qdslResource.rowPath()).execute() > 0;
    }
}
