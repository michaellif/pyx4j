/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 3, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ListIO<E> implements ElementIO, Collection<E> {

    private ArrayList<E> value;

    private Action action = Action.notAttached;

    public ListIO() {
        this(null);
    }

    public ListIO(Action action) {
        this.action = action;
        value = new ArrayList<>();
    }

    @XmlElement
    public ArrayList<E> getValue() {
        return value;
    }

    @XmlAttribute
    @Override
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public E get(int index) {
        return value.get(index);
    }

    @Override
    public int size() {
        return value.size();
    }

    public int indexOf(Object o) {
        return value.indexOf(o);
    }

    @Override
    public Iterator<E> iterator() {
        return value.iterator();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return value.contains(o);
    }

    @Override
    public Object[] toArray() {
        return value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return value.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return value.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return value.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return value.retainAll(c);
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public boolean add(E e) {
        return value.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return value.addAll(c);
    }

}
