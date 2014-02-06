/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.security.common.AbstractPmcUser;

@ToStringFormat("{0}{1,choice,null#|!null#, on {1}}{2,choice,null#|!null#, by {2}}")
public interface MaintenanceRequestStatusRecord extends IEntity {

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    MaintenanceRequest request();

    MaintenanceRequestStatus oldStatus();

    @ToString(index = 0)
    MaintenanceRequestStatus newStatus();

    @ToString(index = 2)
    AbstractPmcUser updatedBy();

    @ToString(index = 1)
    @Caption(name = "Added")
    @Timestamp(Timestamp.Update.Created)
    IPrimitive<Date> created();
}
