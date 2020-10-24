package com.blebail.querydsl.crud.commons.page;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SortTest {

    @Test
    public void shouldThrowException_whenPropertyIsNull() {
        assertThrows(NullPointerException.class, () -> new Sort(null, Sort.Direction.ASC));
    }

    @Test
    public void shouldThrowException_whenDirectionIsNull() {
        assertThrows(NullPointerException.class, () -> new Sort("id", null));
    }

    @Test
    public void shouldAssignAProperty() {
        Sort sort = new Sort("id", Sort.Direction.ASC);

        assertThat(sort.property()).isEqualTo("id");
    }

    @Test
    public void shouldAssignADirection() {
        Sort sort = new Sort("id", Sort.Direction.ASC);

        assertThat(sort.direction()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    public void shouldHaveADefaultAscDirection() {
        assertThat(new Sort("id").direction()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    public void shouldBeEqual_whenSamePropertyAndSameDirection() {
        String property = "id";
        Sort.Direction direction = Sort.Direction.ASC;

        Sort sort = new Sort(property, direction);
        Sort sameSort = new Sort(property, direction);

        assertThat(sort).isEqualTo(sameSort);
    }

    @Test
    public void shouldPrintsItsIndexSizeAndSorts() {
        Sort sort = new Sort("id", Sort.Direction.ASC);

        assertThat(sort.toString()).contains("id", Sort.Direction.ASC.name());
    }

    @Test
    public void shouldBeComparedOnProperty() {
        Sort sort1 = new Sort("id", Sort.Direction.ASC);
        Sort sort2 = new Sort("name", Sort.Direction.DESC);

        assertThat(sort1.compareTo(sort2)).isNegative();
        assertThat(sort2.compareTo(sort1)).isPositive();
    }

    @Test
    public void shouldBeComparedOnDirection_whenPropertyAreEqual() {
        Sort sort1 = new Sort("id", Sort.Direction.ASC);
        Sort sort2 = new Sort("id", Sort.Direction.DESC);

        assertThat(sort1.compareTo(sort2)).isNegative();
        assertThat(sort2.compareTo(sort1)).isPositive();
    }
}