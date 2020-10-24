package com.blebail.querydsl.crud.commons.page;

import com.google.common.base.MoreObjects;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The request for a page, with a zero based index.
 * Page index, size, and sorts to be applied.
 */
public final class PageRequest implements Comparable<PageRequest> {

    private static final int DEFAULT_INDEX = 0;

    private static final int DEFAULT_SIZE = 25;

    private final int index;

    private final int size;

    private final int offset;

    private final Set<Sort> sorts;

    public PageRequest() {
        this(DEFAULT_INDEX);
    }

    public PageRequest(int index) {
        this(index, DEFAULT_SIZE);
    }

    public PageRequest(int index, int size) {
        this(index, size, List.of());
    }

    public PageRequest(int index, int size, List<Sort> sorts) {
        this.index = index >= 0 ? index : DEFAULT_INDEX;
        this.size = size >= 0 ? size : DEFAULT_SIZE;
        this.offset = index * size;
        this.sorts = Objects.requireNonNull(sorts).isEmpty() ? new HashSet<>() : buildSorts(sorts);
    }

    public int index() {
        return index;
    }

    public int size() {
        return size;
    }

    public int offset() {
        return offset;
    }

    public Set<Sort> sorts() {
        return sorts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageRequest that = (PageRequest) o;
        return index == that.index &&
                size == that.size &&
                Objects.equals(sorts, that.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, size, sorts);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("index", index)
                .add("size", size)
                .add("sorts", sorts)
                .toString();
    }

    @Override
    public int compareTo(PageRequest other) {
        if (this.equals(other)) {
            return 0;
        }

        return Integer.compare(this.index, other.index);
    }

    private Set<Sort> buildSorts(List<Sort> sorts) {
        Set<String> sortProperties = new HashSet<>();
        LinkedHashSet<Sort> onlyOneSortForProperty = new LinkedHashSet<>();

        for (Sort sort : sorts) {
            String sortProperty = sort.property().toLowerCase();

            if (!sortProperties.contains(sortProperty)) {
                sortProperties.add(sortProperty);
                onlyOneSortForProperty.add(sort);
            }
        }

        return onlyOneSortForProperty;
    }
}
