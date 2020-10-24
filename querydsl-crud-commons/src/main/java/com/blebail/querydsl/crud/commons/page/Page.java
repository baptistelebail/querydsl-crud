package com.blebail.querydsl.crud.commons.page;

import com.blebail.querydsl.crud.commons.utils.Iterables;
import com.google.common.base.MoreObjects;

import java.util.Collection;
import java.util.Objects;

/**
 * A page of items.
 * @param <T> type of paginated items
 */
public final class Page<T> {

    private final Collection<T> items;

    private final long size;

    private final long totalItems;

    private final long totalPages;

    public Page(Iterable<T> items, long totalItems, long totalPages) {
        this.items = Iterables.asCollection(Objects.requireNonNull(items));
        this.size = Iterables.size(items);
        this.totalItems = Math.max(totalItems, 0L);
        this.totalPages = Math.max(totalPages, 1L);
    }

    public Collection<T> items() {
        return items;
    }

    public long size() {
        return size;
    }

    public long totalItems() {
        return totalItems;
    }

    public long totalPages() {
        return totalPages;
    }

    public static int totalPages(long totalItems, long pageSize) {
        return Double.valueOf(Math.ceil((double) totalItems / (double) pageSize)).intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page<?> page = (Page<?>) o;
        return size == page.size &&
                totalItems == page.totalItems &&
                totalPages == page.totalPages &&
                Objects.equals(items, page.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, size, totalItems, totalPages);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("items", items)
                .add("size", size)
                .add("totalItems", totalItems)
                .add("totalPages", totalPages)
                .toString();
    }
}
