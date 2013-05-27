/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 11, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Inheritance.InheritanceStrategy;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@Table(prefix = "billing")
@AbstractEntity
@Inheritance(strategy = InheritanceStrategy.SINGLE_TABLE)
public interface ArrearsSnapshot<BUCKETS extends AgingBuckets<?>> extends IEntity {

    /**
     * a date when this snapshot was taken
     */
    IPrimitive<LogicalDate> fromDate();

    /**
     * a last day when this snapshot was relevant (equals to OccupancyFacade.MAX_DATE for the most recent snapshot)
     */
    IPrimitive<LogicalDate> toDate();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<BUCKETS> agingBuckets();

}
