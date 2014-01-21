/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
public interface OnlineApplicationStatus extends IEntity {

    IPrimitive<OnlineApplication.Status> status();

    Customer customer();

    /**
     * Applicant, Co-Applicant or Guarantor
     */
    IPrimitive<LeaseTermParticipant.Role> role();

    /**
     * Completed steps/total steps in %
     */
    @Format("#0.00")
    @Caption(name = "Progress (%)")
    IPrimitive<BigDecimal> progress();

}
