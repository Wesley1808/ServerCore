package me.wesley1808.servercore.common.collections;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A list that filters out elements during iteration.
 * Based on FilteringIterable from C2ME.
 */
public class FilteredList<T> extends ObjectArrayList<T> {
    private final Predicate<T> filter;

    public FilteredList(int capacity, Predicate<T> filter) {
        super(capacity);
        this.filter = filter;
    }

    @Override
    @NotNull
    public ObjectListIterator<T> listIterator(int index) {
        var iterator = super.listIterator(index);
        return new FilteringIterator(iterator);
    }

    private class FilteringIterator implements ObjectListIterator<T> {
        private final Iterator<T> iterator;
        private T next;

        private FilteringIterator(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.calculateNext();
        }

        @Override
        public T next() {
            if (this.calculateNext()) {
                T object = this.next;
                this.next = null;
                return object;
            }
            throw new NoSuchElementException();
        }

        private boolean calculateNext() {
            if (this.next != null) {
                return true;
            }

            while (this.iterator.hasNext()) {
                T object = this.iterator.next();
                if (filter.test(object)) {
                    this.next = object;
                    return true;
                }
            }
            return false;
        }

        @Override
        public T previous() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            throw new UnsupportedOperationException();
        }
    }
}
