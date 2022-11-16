package com.wingedtech.common.util.iterators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AbstractInfinitePageIteratorTest {

    @Test
    public void test() {
        Iterator<Integer> iterator = new AbstractInfinitePageIterator<Integer>(5) {
            @Override
            protected Collection<Integer> retrievePage(int pageIndex, int pageSize) {
                if (pageIndex == 0) {
                    return Lists.newArrayList(1, 2, 3, 4, 5);
                }
                if (pageIndex == 1) {
                    return Lists.newArrayList(6, 7, 8, 9, 10);
                }
                if (pageIndex == 2) {
                    return Lists.newArrayList(11, 12);
                }
                return ImmutableList.of();
            }
        };
        assertThat(iterator).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }
}
