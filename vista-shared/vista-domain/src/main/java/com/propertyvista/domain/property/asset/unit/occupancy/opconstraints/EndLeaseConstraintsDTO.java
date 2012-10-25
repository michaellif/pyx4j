/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit.occupancy.opconstraints;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface EndLeaseConstraintsDTO extends IEntity {

    /**
     * @return <code>null</code> if 'EndLease' operation cannot be performed
     */
    IPrimitive<LogicalDate> minleaseEndDate();

    /**
     * @return <code>null</code> if 'EndLease' operation cannot be performed
     */
    IPrimitive<LogicalDate> maxleaseEndDate();
}
