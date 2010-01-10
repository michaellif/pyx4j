/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on May 7, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Collection;

/**
 * Used for keeping track of where you are in a collection/array being iterated over. Use
 * by initializing with the collection/array before the loop and embedding a switch on the
 * next() Mode value into the loop.
 * 
 * @author David Stark, http://www.zarkonnen.com
 */
public class LoopCounter {

    public enum Where {

        /** The only element of an array/collection of size 1. */
        SINGLE,

        /** The first element. */
        FIRST,

        /** Any other element somewhere in the middle. */
        ITEM,

        /** The last element of the array/collection. */
        LAST

    }

    private int size;

    private int nextIndex = 0;

    /**
     * @param c
     *            A collection to keep track of. If its size changes between now and the
     *            iteration, strange things will happen.
     */
    public LoopCounter(Collection<?> c) {
        size = c.size();
    }

    /**
     * @param a
     *            An array to keep track of. If its size changes between now and the
     *            iteration, strange things will happen.
     */
    public LoopCounter(Object[] a) {
        size = a.length;
    }

    /**
     * @return A Where enum value for where in the array/collection we now are:
     *         <ul>
     *         <li><strong>single</strong> at the only element of an array/collection of
     *         size 1</li>
     *         <li><strong>first</strong> at the first element</li>
     *         <li><strong>last</strong> at the last element</li>
     *         <li><strong>item</strong> at any other element</li>
     *         </ul>
     */
    public Where next() {
        return size == 1 ? Where.SINGLE : nextIndex++ == 0 ? Where.FIRST : nextIndex == size ? Where.LAST : Where.ITEM;
    }

    /**
     * @return Which index of the array/collection we're currently at.
     */
    public int index() {
        return nextIndex == 0 ? 0 : nextIndex - 1;
    }

}
