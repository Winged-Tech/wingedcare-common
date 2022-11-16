package com.wingedtech.common.util.iterators;

import com.google.common.collect.UnmodifiableIterator;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractInfinitePageIterator<T> extends UnmodifiableIterator<T> {

    private @Nullable T nextOrNull = null;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final int pageSize;

    private int currentPageIndex;

    private boolean hasNextPage = true;

    private Iterator<T> currentIterator;

    private boolean firstPageLoaded = false;

    protected AbstractInfinitePageIterator() {
        this(DEFAULT_PAGE_SIZE);
    }

    /**
     * Creates a new iterator with the given first element, or, if {@code firstOrNull} is null,
     * creates a new empty iterator.
     *
     */
    protected AbstractInfinitePageIterator(int pageSize) {
        this.pageSize = pageSize;
        this.currentPageIndex = 0;
    }

    @Override
    public final boolean hasNext() {
        if (!firstPageLoaded) {
            // 获取第一页数据
            nextPage();
            if (currentIterator != null) {
                if (!currentIterator.hasNext()) {
                    return false;
                }
                nextOrNull = currentIterator.next();
            }
        }
        return nextOrNull != null;
    }

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return nextOrNull;
        } finally {
            nextOrNull = computeNext(nextOrNull);
        }
    }

    @Nullable
    protected T computeNext(T previous) {
        if (currentIterator == null) {
            nextPage();
        }

        if (currentIterator != null) {
            if (!currentIterator.hasNext()) {
                nextPage();
            }
            if (currentIterator != null && currentIterator.hasNext()) {
                return currentIterator.next();
            }
        }
        return null;
    }

    protected void nextPage() {
        if (hasNextPage) {
            Collection<T> page = retrievePage(this.currentPageIndex, this.pageSize);
            currentIterator = page.iterator();
            if (page.size() < this.pageSize) {
                this.hasNextPage = false;
            }
            if (this.hasNextPage) {
                this.currentPageIndex += 1;
            }
        }
        else {
            currentIterator = null;
        }
        if (!firstPageLoaded) {
            firstPageLoaded = true;
        }
    }

    protected abstract Collection<T> retrievePage(int pageIndex, int pageSize);
}
