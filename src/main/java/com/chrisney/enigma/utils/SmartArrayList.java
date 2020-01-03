package com.chrisney.enigma.utils;

import java.util.ArrayList;

/**
 * Smartest Array List
 * @param <T> Type of the Array List
 */
public class SmartArrayList<T> extends ArrayList<T> {

    /**
     * Return the first element
     * @return First element
     * @throws IndexOutOfBoundsException If array is empty
     */
    public T first() throws IndexOutOfBoundsException {
        return this.get(0);
    }

    /**
     * Return the last element
     * @return Last element
     * @throws IndexOutOfBoundsException If array is empty
     */
    public T last() throws IndexOutOfBoundsException {
        return this.get(this.size() - 1);
    }

    /**
     * Return the previous element, or null if index is 0
     * @param i Index
     * @return Previous element
     * @throws IndexOutOfBoundsException If index is greater than array size
     */
    public T previous(int i) throws IndexOutOfBoundsException {
        return (i > 0) ? this.get(i - 1) : null;
    }

    /**
     * Return the next element if exits otherwise null
     * @param i Index
     * @return Next element if exits otherwise null
     */
    public T next(int i) {
        return (this.size() > i + 1) ? this.get(i + 1) : null;
    }

    /**
     * Return the value of the last index, or -1 if array is empty
     * @return Value of the last index
     */
    public int lastIndex() {
        return this.size() - 1;
    }
}
