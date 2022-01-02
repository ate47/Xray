package fr.atesab.xray.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TagOnWriteList<E> implements List<E> {

    public static <E> TagOnWriteList<E> ofArrayList() {
        return new TagOnWriteList<>(new ArrayList<>());
    }

    private List<E> list;
    private boolean updated = false;
    private boolean tagEnabled = true;

    public TagOnWriteList(List<E> list) {
        this.list = list;
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
        if (tagEnabled) {
            this.updated = true;
            onUpdate();
        }
    }

    public void setTagEnabled(boolean tagEnabled) {
        this.tagEnabled = tagEnabled;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new TagOnWriteIterator<>(list.iterator()) {
            @Override
            protected void onUpdate() {
                TagOnWriteList.this.setUpdated();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        setUpdated();
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        setUpdated();
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        setUpdated();
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        setUpdated();
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        setUpdated();
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        setUpdated();
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        setUpdated();
        list.clear();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        setUpdated();
        return list.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        setUpdated();
        list.add(index, element);
    }

    @Override
    public E remove(int index) {
        setUpdated();
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return new TagOnWriteListIterator<>(list.listIterator()) {
            @Override
            protected void onUpdate() {
                TagOnWriteList.this.setUpdated();
            }
        };
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new TagOnWriteListIterator<>(list.listIterator(index)) {
            @Override
            protected void onUpdate() {
                TagOnWriteList.this.setUpdated();
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        TagOnWriteList<E> parent = this;
        return new TagOnWriteList<>(list.subList(fromIndex, toIndex)) {
            @Override
            protected void onUpdate() {
                parent.setUpdated();
            }
        };
    }

}
