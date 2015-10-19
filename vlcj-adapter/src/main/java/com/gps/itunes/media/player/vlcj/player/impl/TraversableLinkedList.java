package com.gps.itunes.media.player.vlcj.player.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Double-ended List.
 * Does not provide any sorting whatsoever.
 *
 * Created by leogps on 10/17/15.
 */
public class TraversableLinkedList<T> {

    private Node<T> firstElement;
    private Node<T> lastElement;

    private ListTraverser<T> listTraverser = new ListTraverser<T>(this);

    private final ReentrantReadWriteLock firstElementReadWriteLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock lastElementReadWriteLock = new ReentrantReadWriteLock(true);

    private AtomicInteger size = new AtomicInteger(0);

    /**
     * Adds to the end of the list.
     *
     * @param t
     */
    public boolean add(T t) {
        acquireWriteLock(this);
        TraversableLinkedList<T>.Node<T> lastElementBeforeAdd = null;
        try {
            if ((firstElement == null && lastElement != null)
                    || (firstElement != null && lastElement == null)) {

                throw new IllegalStateException("The element states are invalid. Data may be corrupt.");

            } else if (firstElement == null && lastElement == null) {
                firstElement = lastElement = new Node<T>(t);
            } else {

                lastElementBeforeAdd = lastElement;
                // Establishing the chain at the end and re-assigning lastElement.
                Node<T> newNode = new Node<T>(t);
                newNode.previous = lastElement;
                lastElement.next = newNode;
                lastElement = newNode;
            }

            // This will update the traverser correctly if it reached the end.
            if(listTraverser.intermediateElementPointer.previous == lastElementBeforeAdd) {
                listTraverser.intermediateElementPointer.next = lastElement;
            }

            size.incrementAndGet();
        } finally {
            releaseWriteLock(this);
        }
        return true;
    }

    private void releaseWriteLock(TraversableLinkedList<T> list) {
        list.firstElementReadWriteLock.writeLock().unlock();
        list.lastElementReadWriteLock.writeLock().unlock();
    }

    private void acquireWriteLock(TraversableLinkedList list) {
        list.firstElementReadWriteLock.writeLock().lock();
        list.lastElementReadWriteLock.writeLock().lock();
    }

    public boolean isEmpty() {
        return firstElement == null;
    }

    /**
     * Finds and removes the element.
     *
     * @param t
     */
    public boolean remove(Object t) {
        if(t != null) {
            TraversableLinkedList<T>.Node<T> element = findElementNode(t, null);
            if(element != null) {
                try {
                    acquireWriteLock(this);
                    boolean removed = doRemove(element);
                    if(removed) {
                        size.decrementAndGet();
                    }
                    return removed;
                } finally {
                    releaseWriteLock(this);
                }
            }

        }
        return false;
    }

    private boolean doRemove(Node<T> element) {
        if(isEmpty()) {
            return false;
        }

        if(size() == 1) {
            // This is the only element.
            clear();
            return true;
        }

        if(element == firstElement) {
            // This is the first element.
            firstElement = firstElement.next;
            firstElement.previous = null;
            updateListTraverserOnRemove(element);
            return true;
        }

        if(element == lastElement) {
            // This is the last element.
            lastElement = lastElement.previous;
            lastElement.next = null;
            updateListTraverserOnRemove(element);
            return true;
        }

        // Re-establishing chain.
        TraversableLinkedList<T>.Node<T> previousElement =  element.previous;
        TraversableLinkedList<T>.Node<T> nextElement =  element.next;
        previousElement.next = nextElement;
        nextElement.previous = previousElement;

        updateListTraverserOnRemove(element);
        return true;
    }

    private void updateListTraverserOnRemove(Node<T> element) {
        if(listTraverser.intermediateElementPointer.previous == element) {
            listTraverser.intermediateElementPointer.previous = element.previous;
        } else if(listTraverser.intermediateElementPointer.next == element) {
            listTraverser.intermediateElementPointer.next = element.next;
        }
    }

    public int indexOf(Object t) {
        int initialIndexValue = -1;
        AtomicInteger index = new AtomicInteger(initialIndexValue);
        TraversableLinkedList<T>.Node<T> element = findElementNode(t, index);
        if(element != null) {
            return index.get();
        }
        return initialIndexValue;
    }

    private TraversableLinkedList<T>.Node<T> findElementNode(Object t, AtomicInteger index) {
        try {
            acquireReadLock(this);

            if(firstElement != null) {
                TraversableLinkedList<T>.Node<T> pointer = firstElement;

                if(index != null) {
                    index.incrementAndGet();
                }
                if(pointer.s != null && pointer.s.equals(t)) {
                    return pointer;
                }
                while(pointer.next != null) {
                    if(index != null) {
                        index.incrementAndGet();
                    }
                    pointer = pointer.next;
                    if(pointer.s != null && pointer.s.equals(t)) {
                        return pointer;
                    }
                }
            }
            return null;
        } finally {
            releaseReadLock(this);
        }
    }

    protected class Node<S> {

        private final S s;

        private Node(S s) {
            this.s = s;
        }

        private Node<S> previous;
        private Node<S> next;

        public Node<S> getPrevious() {
            return previous;
        }

        public Node<S> getNext() {
            return next;
        }
    }

    public T getFirstElement() {
        if(firstElement != null) {
            return firstElement.s;
        }
        return null;
    }

    public T getLastElement() {
        if(lastElement != null) {
            return lastElement.s;
        }
        return null;
    }

    public ListTraverser<T> getListTraverser() {
        return listTraverser;
    }

    /**
     * Gives you a thread-safe size of this list.
     *
     * @return
     */
    public int size() {
        return size.get();
    }

    public void clear() {
        try {
            acquireWriteLock(this);

            firstElement = lastElement = null;
            listTraverser.reinitialize();
            size = new AtomicInteger(0);

        } finally {
            releaseWriteLock(this);
        }
    }

    /**
     * This is for testing purposes only. Use {@link #size()} to get the size of the list.
     * Note: This will give wrong count if called when elements are being added --> Not Thread-safe.
     * @return
     */
    public int countElements() {
        return traveseForwardAndCount();
    }

    private int traveseForwardAndCount() {
        int count = 0;
        if(firstElement != null) {
            ++count;
            TraversableLinkedList<T>.Node<T> countPointer = firstElement;
            while(countPointer.next != null) {
                ++count;
                countPointer = countPointer.next;
            }
        }
        return count;
    }

    /**
     * Roughly equivalent to ListIterator but is a better fit for the NowPlayingList.
     *
     * @param <V>
     */
    public class ListTraverser<V> {

        private final TraversableLinkedList<V> list;

        private final TraversableLinkedList<V>.Node<V> intermediateElementPointer = new TraversableLinkedList<V>.Node<V>(null);

        public ListTraverser(TraversableLinkedList<V> list) {
            this.list = list;
            intermediateElementPointer.next = list.firstElement;
        }

        public boolean hasNext() {
            try {
                acquireReadLock(list);

                return intermediateElementPointer.next != null;

            } finally {
                releaseReadLock(list);
            }
        }

        public V next() {
            try {
                acquireReadLock(list);
                if (list.isEmpty()) {
                    throw new IndexOutOfBoundsException("Next element not available.");
                }

                if (intermediateElementPointer.next == null) {
                    throw new IndexOutOfBoundsException("Next element not available.");
                }

                V value = traverseForwardAndReturn();
                return value;
            } finally {
                releaseReadLock(list);
            }

        }

        private V traverseForwardAndReturn() {
            TraversableLinkedList<V>.Node<V> approachableElement = intermediateElementPointer.next;
            // approachableElement cannot be null, the calling method will handle it.

            intermediateElementPointer.previous = approachableElement;
            intermediateElementPointer.next = approachableElement.next;
            return approachableElement.s;
        }

        public boolean hasPrevious() {
            try {

                acquireReadLock(list);

                return intermediateElementPointer.previous != null;

            } finally {
                releaseReadLock(list);
            }
        }

        public V previous() {
            try {
                acquireReadLock(list);

                if (intermediateElementPointer.previous == null) {
                    throw new IndexOutOfBoundsException("Previous element not available. Either reached the first element or next is never called.");
                }
                V value = traverseBackwardAndReturn();
                return value;
            } finally {
                releaseReadLock(list);
            }
        }

        private V traverseBackwardAndReturn() {
            TraversableLinkedList<V>.Node<V> approachableElement = intermediateElementPointer.previous;
            // approachableElement element cannot be null, the calling method will handle it.

            intermediateElementPointer.previous = approachableElement.previous;
            intermediateElementPointer.next = approachableElement;
            return approachableElement.s;
        }

        private void reinitialize() {
            intermediateElementPointer.previous = null;
            intermediateElementPointer.next = list.firstElement;
        }
    }

    private void acquireReadLock(TraversableLinkedList list) {
        list.firstElementReadWriteLock.readLock().lock();
        list.lastElementReadWriteLock.readLock().lock();
    }
    private void releaseReadLock(TraversableLinkedList list) {
        list.firstElementReadWriteLock.readLock().unlock();
        list.lastElementReadWriteLock.readLock().unlock();
    }
}
