package com.blebail.querydsl.crud.commons.resource;

import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPathBase;

import java.util.Objects;
import java.util.function.Function;

public final class IdentifiableQDSLResource<T extends RelationalPathBase<R>, R, ID> extends QDSLResource<T, R> {

    private final SimpleExpression<ID> idPath;

    private final Function<R, ID> idMapping;

    public IdentifiableQDSLResource(T rowPath, SimpleExpression<ID> idPath, Function<R, ID> idMapping) {
        super(rowPath);
        this.idPath = Objects.requireNonNull(idPath);
        this.idMapping = Objects.requireNonNull(idMapping);
    }

    public SimpleExpression<ID> idPath() {
        return idPath;
    }

    public Function<R, ID> idMapping() {
        return idMapping;
    }
}
