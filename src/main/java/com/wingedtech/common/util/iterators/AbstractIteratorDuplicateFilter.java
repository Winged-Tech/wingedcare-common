package com.wingedtech.common.util.iterators;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
public abstract class AbstractIteratorDuplicateFilter<T, I extends Comparable> extends UnmodifiableIterator<T> {

    private @Nullable T nextElement = null;

    private final Iterator<T> iterator;

    private final Set<I> idSet;

    private boolean firstElementComputed = false;

    public AbstractIteratorDuplicateFilter(@NotNull Iterator<T> iterator) {
        this.iterator = iterator;
        idSet = Sets.newTreeSet();
    }

    @Override
    public final boolean hasNext() {
        if (!firstElementComputed) {
            nextElement = computeNext();
            firstElementComputed = true;
        }
        return nextElement != null;
    }

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return nextElement;
        } finally {
            nextElement = computeNext();
        }
    }

    @Nullable
    protected T computeNext() {
        T element = null;
        while (iterator.hasNext()) {
            T current = iterator.next();
            I identifier = getElementIdentifier(current);
            if (isUnique(identifier)) {
                element = current;
                this.idSet.add(identifier);
                break;
            }
            else {
                log.debug("Ignore duplicated entry {}", identifier);
            }
        }
        return element;
    }

    protected boolean isUnique(I identifier) {
        return !idSet.contains(identifier);
    }

    protected abstract I getElementIdentifier(T element);
}
