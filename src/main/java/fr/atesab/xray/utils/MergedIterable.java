package fr.atesab.xray.utils;

import java.util.Iterator;
import java.util.Objects;

/**
 * merge 2 iterables into one
 */
public class MergedIterable<E> implements Iterable<E> {
    private static class MergedIterator<E> implements Iterator<E> {
        private boolean lastLeft = true;
        private Iterator<? extends E> left;
        private Iterator<? extends E> right;

        MergedIterator(Iterator<? extends E> left, Iterator<? extends E> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean hasNext() {
            return left.hasNext() || right.hasNext();
        }

        @Override
        public E next() {
            if (left.hasNext()) {
                lastLeft = true;
                return left.next();
            } else {
                lastLeft = false;
                return right.next();
            }
        }

        @Override
        public void remove() {
            if (lastLeft) {
                left.remove();
            } else {
                right.remove();
            }
        }

    }

    private Iterable<? extends E> left;
    private Iterable<? extends E> right;

    public MergedIterable(Iterable<? extends E> left, Iterable<? extends E> right) {
        this.left = Objects.requireNonNull(left, "left iterable can't be null!");
        this.right = Objects.requireNonNull(right, "right iterable can't be null!");
    }

    /**
     * append an iterable to this iterable
     * 
     * @param iterable the iterable to append
     * @return this
     */
    public MergedIterable<E> append(Iterable<E> iterable) {
        right = new MergedIterable<>(right, iterable);
        return this;
    }

    @Override
    public Iterator<E> iterator() {
        return new MergedIterator<E>(left.iterator(), right.iterator());
    }
}
