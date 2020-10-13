package com.blebail.querydsl.crud.commons.page;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public final class PageRequestTest {

    @Test
    public void shouldHaveADefaultIndexOf0() {
        assertThat(new PageRequest().index()).isEqualTo(0);
        assertThat(new PageRequest(-1).index()).isEqualTo(0);
    }

    @Test
    public void shouldAssignAnIndex() {
        assertThat(new PageRequest(5).index()).isEqualTo(5);
        assertThat(new PageRequest(5, 20).index()).isEqualTo(5);
    }

    @Test
    public void shouldHaveADefaultSizeOf25() {
        assertThat(new PageRequest().size()).isEqualTo(25);
        assertThat(new PageRequest(0).size()).isEqualTo(25);
        assertThat(new PageRequest(0, -1).size()).isEqualTo(25);
    }

    @Test
    public void shouldAssignASize() {
        assertThat(new PageRequest(0, 50).size()).isEqualTo(50);
    }

    @Test
    public void shouldHaveNoDefaultSorts() {
        assertThat(new PageRequest().sorts()).isEmpty();
        assertThat(new PageRequest(0).sorts()).isEmpty();
        assertThat(new PageRequest(2, 25).sorts()).isEmpty();
        assertThat(new PageRequest(2, 25, List.of()).sorts()).isEmpty();
    }

    @Test
    public void shouldAssignSorts() {
        Sort idAsc = new Sort("id", Sort.Direction.ASC);
        Sort idDesc = new Sort("id", Sort.Direction.DESC);
        Sort nameDesc = new Sort("name", Sort.Direction.DESC);

        List<Sort> sorts = new LinkedList<>();
        sorts.add(idAsc);
        sorts.add(idDesc);
        sorts.add(nameDesc);

        Set<Sort> pageSorts = new PageRequest(2, 25, sorts).sorts();

        Assertions.assertThat(pageSorts).containsExactly(idAsc, nameDesc);
    }

    @Test
    public void shouldComputeOffset() {
        assertThat(new PageRequest().offset()).isEqualTo(0);
        assertThat(new PageRequest(0).offset()).isEqualTo(0);
        assertThat(new PageRequest(2, 25).offset()).isEqualTo(50);
    }

    @Test
    public void shouldBeEqual_whenSameIndexSameSizeAndSameSorts() {
        int index = 5;
        int size = 10;
        List<Sort> sorts = List.of(new Sort("id", Sort.Direction.ASC), new Sort("name", Sort.Direction.DESC));

        PageRequest pageRequest = new PageRequest(index, size, sorts);
        PageRequest samePageRequest = new PageRequest(index, size, sorts);

        assertThat(pageRequest).isEqualTo(samePageRequest);
    }

    @Test
    public void shouldPrintsItsIndexSizeAndSorts() {
        PageRequest pageRequest = new PageRequest();

        assertThat(pageRequest.toString()).contains(String.valueOf(pageRequest.index()), String.valueOf(pageRequest.size()), pageRequest.sorts().toString());
    }

    @Test
    public void shouldBeComparedOnIndex() {
        PageRequest pageRequest1 = new PageRequest(1);
        PageRequest pageRequest2 = new PageRequest(5);

        assertThat(pageRequest1.compareTo(pageRequest2)).isNegative();
        assertThat(pageRequest2.compareTo(pageRequest1)).isPositive();
    }
}