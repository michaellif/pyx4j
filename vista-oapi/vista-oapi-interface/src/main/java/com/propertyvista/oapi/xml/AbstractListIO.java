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

import javax.xml.bind.annotation.XmlAttribute;

public class AbstractListIO<E> implements ElementIO {

    private ArrayList<E> list;

    private Action action = Action.notAttached;

    public AbstractListIO() {
        this(null);
    }

    public AbstractListIO(Action action) {
        this.action = action;
        list = new ArrayList<>();
    }

    public ArrayList<E> getList() {
        return list;
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
