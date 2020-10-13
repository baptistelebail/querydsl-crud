package com.blebail.querydsl.crud.commons.page;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PageTest {

    @Test
    public void shouldThrowAnException_whenItemsIsNull() {
        assertThrows(Exception.class, () -> new Page<>(null, 0, 0));
    }

    @Test
    public void shouldHaveADefaultTotalItemsOf0() {
        assertThat(new Page<>(new ArrayList<>(), -1, 10).totalItems()).isEqualTo(0);
    }

    @Test
    public void shouldAssignTotalItems() {
        assertThat(new Page<>(new ArrayList<>(), 15, 10).totalItems()).isEqualTo(15);
    }

    @Test
    public void shouldHaveADefaultTotalPagesOf0() {
        assertThat(new Page<>(new ArrayList<>(), 10, -1).totalPages()).isEqualTo(1);
    }

    @Test
    public void shouldAssignTotalPages() {
        assertThat(new Page<>(new ArrayList<>(), 10, 15).totalPages()).isEqualTo(15);
    }

    @Test
    public void shouldComputeNbItems() {
        List<String> items = Arrays.asList("John", "Jane");
        Page<String> page = new Page<>(items, 4, 2);

        assertThat(page.size()).isEqualTo(items.size());
    }

    @Test
    public void shouldBeEqual_whenSameItemsSameTotalItemsAndSameTotalPages() {
        List<String> items = Arrays.asList("John", "Jane");
        Page<String> page = new Page<>(items, 4, 2);
        Page<String> samePage = new Page<>(items, 4, 2);

        assertThat(page).isEqualTo(samePage);
    }

    @Test
    public void shouldPrintsItsItemsNbItemsTotalItemsAndtotalPages() {
        Page<String> page = new Page<>(Arrays.asList("John", "Jane"), 4, 2);

        assertThat(page.toString()).contains(
                page.items().toString(),
                String.valueOf(page.size()),
                String.valueOf(page.totalItems()),
                String.valueOf(page.totalPages())
        );
    }
}