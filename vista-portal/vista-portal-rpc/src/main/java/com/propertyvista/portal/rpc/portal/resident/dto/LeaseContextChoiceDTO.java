/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 24, 2012
 * @author ArtyomB
 */
package com.propertyvista.portal.rpc.portal.resident.dto;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;

@Transient
public interface LeaseContextChoiceDTO extends IEntity {

    /** reference to lease */
    Lease leaseId();

    @Caption(name = "Unit Address")
    IPrimitive<String> leasedUnitAddress();

    IPrimitive<LogicalDate> leaseFrom();

    IPrimitive<LogicalDate> leaseTo();

    IPrimitive<Status> status();
}
