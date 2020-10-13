package com.blebail.querydsl.crud.sync.repository;

import java.util.Collection;
import java.util.Optional;

/**
 * @param <R>  row type
 * @param <ID> id type
 */
public interface CrudRepository<R, ID> extends BaseRepository<R> {

    R save(R resource);

    Collection<R> save(Iterable<R> resources);

    Optional<R> findOne(ID resourceId);

    Collection<R> find(Iterable<ID> resourceIds);

    boolean exists(ID resourceId);

    boolean delete(ID resourceId);

    boolean delete(Iterable<ID> resourceIds);
}
