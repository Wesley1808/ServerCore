package me.wesley1808.servercore.common.collections;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ChunkArrayList<T> extends ObjectArrayList<T> {
    public ChunkArrayList() {
        super(1024);
    }

    public void addChunk(T value) {
        super.add(value);
    }

    public void removeChunk(Predicate<T> predicate) {
        for (int i = 0; i < this.size; i++) {
            if (predicate.test(a[i])) {
                super.remove(i);
                return;
            }
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object k) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T chunkAndHolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectListIterator<T> listIterator(final int index) {
        ensureIndex(index);
        return new ObjectIterators.AbstractIndexBasedListIterator<>(0, index) {
            @Override
            protected T get(int i) {
                return ChunkArrayList.this.get(i);
            }

            @Override
            protected int getMaxPos() {
                return ChunkArrayList.this.size();
            }

            @Override
            protected void add(int i, T value) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void set(int i, T value) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void remove(int i) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
