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
package com.propertyvista.yardi.mock.updater;


public class Property<E> {

    private final Name name;

    private final E value;

    public Property(Name name, E value) {
        this.value = value;
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    public E getValue() {
        return value;
    }

    public static <T> Property<T> create(Name name, T value) {
        Property<T> ref = new Property<T>(name, value);
        return ref;
    }
}
