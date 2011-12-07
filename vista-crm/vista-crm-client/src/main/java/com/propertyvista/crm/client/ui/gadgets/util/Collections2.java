/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections2 {
    public static <T> List<T> filter(Iterable<T> iterable, Predicate<? super T> predicate) {
        List<T> result = new ArrayList<T>();

        Iterator<T> i = iterable.iterator();
        while (i.hasNext()) {
            T item = i.next();
            if (predicate.apply(item)) {
                result.add(item);
            }
        }
        return result;
    }

}
