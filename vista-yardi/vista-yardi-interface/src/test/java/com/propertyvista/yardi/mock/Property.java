/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

public class Property<E> {

    private E value;

    public void set(E value) {
        this.value = value;
    }

    public E get() {
        return this.value;
    }

    public static <T> Property<T> create(T value) {
        Property<T> ref = new Property<T>();
        ref.set(value);
        return ref;
    }
}
