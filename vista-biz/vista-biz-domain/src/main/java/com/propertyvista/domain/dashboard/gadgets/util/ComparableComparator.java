/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.util;

import java.util.Comparator;

/**
 * Compares instances of <code>Comparable</code>
 * 
 * @author ArtyomB
 * 
 */
// TODO move to more convenient package
public class ComparableComparator<T extends Comparable> implements Comparator<T> {

    @Override
    public int compare(T param1, T param2) {
        if (param1 == null) {
            if (param2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (param2 == null) {
            return 1;
        } else {
            // TODO consider removing this check in order to increase performance (also remove the try catch part) 
            if (param1 instanceof Comparable & param2 instanceof Comparable) {
                try {
                    return param1.compareTo(param2);
                } catch (Throwable e) {
                    return 0;
                }
            } else {
                return 0;
            }
        }
    }
}
