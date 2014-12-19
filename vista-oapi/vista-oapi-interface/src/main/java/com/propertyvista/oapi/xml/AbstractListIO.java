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
 */
package com.propertyvista.oapi.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AbstractList")
public class AbstractListIO<E> implements ElementIO {

    private ArrayList<E> list;

    private Note note = Note.contentDetached;

    public AbstractListIO() {
        this(null);
    }

    public AbstractListIO(Note note) {
        this.note = note;
        list = new ArrayList<>();
    }

    public List<E> getList() {
        return list;
    }

    @XmlAttribute
    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public void setNote(Note note) {
        this.note = note;
    }

    public E get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public boolean add(E e) {
        return list.add(e);
    }
}
