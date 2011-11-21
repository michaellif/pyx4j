/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

/**
 * Entity for persisting state of table columns in lister based gadgets.
 * 
 * @author artyom
 * 
 */
public interface ColumnDescriptorEntity extends IEntity {
    /** Determines which property of the embodied member to hold. */
    IPrimitive<String> propertyPath();

    /** Sets title to override the default title of the column. <code>null</code> means use the default one */
    IPrimitive<String> title();

    IPrimitive<Boolean> visible();

    /**
     * Determines precedence of this column in comparison of lexicographic order (the lower the number the higher the precedence).
     * 
     * <code>null</code> means its is undefined and it's values are not compared durning sorting.
     */
    IPrimitive<Integer> sortingPrecedence();

    /**
     * Determine if sorting results should be in descending order (the opposite of default ordering).
     * 
     * <code>true</code> means use descending order, <code>null</code> or <code>false</code> means use ascending (the default) order.
     */
    IPrimitive<Boolean> sortAscending();

    /** Column width */
    // TODO this is not yet clear (if it's % or absolute value)
    IPrimitive<Double> width();
}
