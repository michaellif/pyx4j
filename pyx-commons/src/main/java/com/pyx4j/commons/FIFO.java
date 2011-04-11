/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-04-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class FIFO<E> implements Iterable<E> {

    private E tail;

    private Queue<E> queue;

    private int fifoSize;

    public FIFO(int size) {
        fifoSize = size;
        queue = new LinkedList<E>();
    }

    public E push(E o) {
        E rc;
        if (queue.size() >= fifoSize) {
            rc = queue.poll();
        } else {
            rc = null;
        }
        tail = o;
        queue.add(o);
        return rc;
    }

    public boolean contains(E o) {
        return queue.contains(o);
    }

    public void clear() {
        tail = null;
        queue.clear();
    }

    public int size() {
        return queue.size();
    }

    public void removeFirst(int size) {
        for (int i = 0; i < size; i++) {
            E e = queue.poll();
            if (e == null) {
                tail = null;
                break;
            }
        }
    }

    public void setSize(int size) {
        if (fifoSize != size) {
            fifoSize = size;
            Queue<E> queue2 = new LinkedList<E>();
            while (queue.size() > fifoSize) {
                queue.poll();
            }
            queue2.addAll(queue);
            queue = queue2;
        }
    }

    /**
     * Retrieves, but does not remove, the head of this queue.
     * 
     * @return the head of this queue, or null if this queue is empty.
     */
    public E peek() {
        return queue.peek();
    }

    /**
     * Retrieves, but does not remove, the head of this queue.
     * 
     * @return the head of this queue, or null if this queue is empty.
     */
    public E head() {
        return queue.peek();
    }

    /**
     * 
     * Retrieves the tail of this queue.
     * 
     * 
     * @return the last inserted element
     */
    public E tail() {
        return tail;
    }

    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }
}
