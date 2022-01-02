package fr.atesab.xray.utils;

import java.util.Iterator;

public class TagOnWriteIterator<E> implements Iterator<E> {

    private Iterator<E> it;
    private boolean updated = false;

    public TagOnWriteIterator(Iterator<E> it) {
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
    public void remove() {
        setUpdated();
        it.remove();
    }

}
