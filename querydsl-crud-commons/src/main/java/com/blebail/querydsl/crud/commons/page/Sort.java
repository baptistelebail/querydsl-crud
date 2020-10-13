package com.blebail.querydsl.crud.commons.page;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * A direction for a property.
 */
public final class Sort implements Comparable<Sort> {

    /**
     * Direction of sort, either ascending or descending.
     */
    public enum Direction {
        ASC, DESC
    }

    private final String property;

    private final Direction direction;

    public Sort(String property) {
        this(property, Direction.ASC);
    }

    public Sort(String property, Direction direction) {
        this.property = Objects.requireNonNull(property);
        this.direction = Objects.requireNonNull(direction);
    }

    public String property() {
        return property;
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sort sort = (Sort) o;
        return Objects.equals(property, sort.property) &&
                direction == sort.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, direction);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("property", property)
                .add("direction", direction)
                .toString();
    }

    @Override
    public int compareTo(Sort other) {
        if (this.equals(other)) {
            return 0;
        }

        int propertyComparison = property.compareTo(other.property);

        if (propertyComparison != 0) {
            return propertyComparison;
        }

        return direction.compareTo(other.direction);
    }
}
