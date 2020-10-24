package com.blebail.querydsl.crud.commons.resource;

import com.blebail.querydsl.crud.commons.page.PageRequest;
import com.blebail.querydsl.crud.commons.page.Sort;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.sql.RelationalPathBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class QDSLResource<T extends RelationalPathBase<R>, R> {

    protected final T rowPath;

    public QDSLResource(T rowPath) {
        this.rowPath = Objects.requireNonNull(rowPath);
    }

    public T rowPath() {
        return rowPath;
    }

    public OrderSpecifier[] orders(PageRequest pageRequest) {
        List<OrderSpecifier> orders = computeOrderSpecifiers(pageRequest);

        return orders.toArray(new OrderSpecifier[orders.size()]);
    }

    private List<Path> getSortablePaths() {
        return rowPath.getColumns()
                .stream()
                .filter(path -> path instanceof ComparableExpressionBase)
                .collect(toList());
    }

    private Map<String, ComparableExpressionBase> getSortablePathByColumnNames() {
        Map<String, ComparableExpressionBase> sortablePathByColumnNames = new HashMap<>();

        for (Path sortablePath : getSortablePaths()) {
            sortablePathByColumnNames.put(sortablePath.getMetadata().getName().toLowerCase(), (ComparableExpressionBase) sortablePath);
        }

        return sortablePathByColumnNames;
    }

    private OrderSpecifier order(ComparableExpressionBase base, Sort.Direction direction) {
        return direction == Sort.Direction.DESC ?
                base.desc() :
                base.asc();
    }

    private List<OrderSpecifier> computeOrderSpecifiers(PageRequest pageRequest) {
        List<OrderSpecifier> orders = new ArrayList<>();

        Map<String, ComparableExpressionBase> sortablePathByColumnNames = getSortablePathByColumnNames();

        for (Sort sort : pageRequest.sorts()) {
            String sortColumnName = sort.property().toLowerCase();

            if (sortablePathByColumnNames.containsKey(sortColumnName)) {
                orders.add(order(sortablePathByColumnNames.get(sortColumnName), sort.direction()));
            }
        }

        return orders;
    }
}
