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
package com.propertyvista.domain.dashboard.gadgets.common;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

/**
 * Entity for persisting state of table columns in lister based gadgets or for transferring state of columns from client to the server.
 * 
 * @author artyom
 * 
 */
public interface ColumnDescriptorEntity extends IEntity {

    /** Determines which property of the embodied member to hold. Supposed to be the same thing as <code>ColumnDescriptor.columnName() </code> */
    IPrimitive<String> propertyPath();

    IPrimitive<Boolean> isSortable();

    /** Sets title to override the default title of the column. <code>null</code> means use the default one */
    IPrimitive<String> title();

    IPrimitive<String> width();

    IPrimitive<Boolean> wrapWords();

    /** Determines if the column is visible */
    IPrimitive<Boolean> isVisible();
}
