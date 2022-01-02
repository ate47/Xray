package fr.atesab.xray.utils;

import java.util.ListIterator;

public class TagOnWriteListIterator<E> implements ListIterator<E> {

    private ListIterator<E> it;
    private boolean updated = false;

    public TagOnWriteListIterator(ListIterator<E> it) {
        this.it = it;
    }

    protected void onUpdate() {

    }

    public boolean isUpdated() {
        return updated;
    }

    public void removeUpdated() {
        this.updated = false;
    }

    public void setUpdated() {
        onUpdate();
        this.updated = true;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public E next() {
        return it.next();
    }

    @Override
    public boolean hasPrevious() {
        return it.hasPrevious();
    }

    @Override
    public E previous() {
        return it.previous();
    }

    @Override
    public int nextIndex() {
        return it.nextIndex();
    }

    @Override
    public int previousIndex() {
        return it.previousIndex();
    }

    @Override
    public void remove() {
        setUpdated();
        it.remove();
    }

    @Override
    public void set(E e) {
        setUpdated();
        it.set(e);
    }

    @Override
    public void add(E e) {
        setUpdated();
        it.add(e);
    }

}
