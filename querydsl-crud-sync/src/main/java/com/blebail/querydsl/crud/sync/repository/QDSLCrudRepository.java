package com.blebail.querydsl.crud.sync.repository;

import com.blebail.querydsl.crud.commons.resource.IdentifiableQDSLResource;
import com.blebail.querydsl.crud.commons.utils.Iterables;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
public class QDSLCrudRepository<T extends RelationalPathBase<R>, R, ID> extends QDSLBaseRepository<T, R> implements CrudRepository<R, ID> {

    private final IdentifiableQDSLResource<T, R, ID> qdslResource;

    public QDSLCrudRepository(IdentifiableQDSLResource<T, R, ID> qdslResource, SQLQueryFactory queryFactory) {
        super(qdslResource, queryFactory);
        this.qdslResource = qdslResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R save(R resource) {
        Objects.requireNonNull(resource);

        ID resourceId = qdslResource.idMapping().apply(resource);

        if (exists(resourceId)) {
            queryFactory.update(qdslResource.rowPath())
                    .populate(resource)
                    .where(qdslResource.idPath().eq(resourceId))
                    .execute();
        } else {
            queryFactory.insert(qdslResource.rowPath())
                    .populate(resource)
                    .execute();
        }

        return findOne(resourceId)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<R> save(Iterable<R> resources) {
        Objects.requireNonNull(resources);

        Collection<R> resourcesAsCollection = Iterables.asCollection(resources);
        Collection<ID> resourceIds = resourcesAsCollection.stream()
                .map(qdslResource.idMapping())
                .collect(Collectors.toList());
        Collection<ID> existingResourceIds = findIds(resources);
        boolean insertBatchShouldBeExecuted = resourceIds.size() > existingResourceIds.size();
        boolean updateBatchShouldBeExecuted = existingResourceIds.size() > 0;

        SQLInsertClause insertBatch = queryFactory.insert(qdslResource.rowPath());
        SQLUpdateClause updateBatch = queryFactory.update(qdslResource.rowPath());

        resources.forEach(resource -> addToBatch(resource, existingResourceIds, insertBatch, updateBatch));

        if (insertBatchShouldBeExecuted) {
            insertBatch.execute();
        }

        if (updateBatchShouldBeExecuted) {
            updateBatch.execute();
        }

        return find(resourceIds);
    }

    private void addToBatch(R resource, Collection<ID> existingResourceIds, SQLInsertClause insertBatch, SQLUpdateClause updateBatch) {
        ID resourceId = qdslResource.idMapping().apply(resource);

        if (existingResourceIds.contains(resourceId)) {
            updateBatch.populate(resource)
                    .where(qdslResource.idPath().eq(resourceId))
                    .addBatch();
        } else {
            insertBatch.populate(resource)
                    .addBatch();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<R> findOne(ID resourceId) {
        Objects.requireNonNull(resourceId);

        return Optional.ofNullable(queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath())
                .where(qdslResource.idPath().eq(resourceId))
                .fetchOne());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<R> find(Iterable<ID> resourceIds) {
        Objects.requireNonNull(resourceIds);

        return queryFactory.select(qdslResource.rowPath())
                .from(qdslResource.rowPath())
                .where(qdslResource.idPath().in(Iterables.asCollection(resourceIds)))
                .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(ID resourceId) {
        Objects.requireNonNull(resourceId);

        return queryFactory.select(qdslResource.idPath())
                .from(qdslResource.rowPath())
                .where(qdslResource.idPath().eq(resourceId))
                .fetchOne() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(ID resourceId) {
        Objects.requireNonNull(resourceId);

        return queryFactory.delete(qdslResource.rowPath())
                .where(qdslResource.idPath().eq(resourceId))
                .execute() == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Iterable<ID> resourceIds) {
        Objects.requireNonNull(resourceIds);

        return queryFactory.delete(qdslResource.rowPath())
                .where(qdslResource.idPath().in(Iterables.asCollection(resourceIds)))
                .execute() == Iterables.size(resourceIds);
    }

    protected Collection<ID> findIds(Iterable<R> resources) {
        Collection<ID> resourceIds = Iterables.asCollection(resources)
                .stream()
                .map(qdslResource.idMapping())
                .collect(Collectors.toList());

        return queryFactory.select(qdslResource.idPath())
                .from(qdslResource.rowPath())
                .where(qdslResource.idPath().in(resourceIds))
                .fetch();
    }
}
