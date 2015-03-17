/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 */
package com.propertyvista.biz.system;

import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.security.AuditRecordEventType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

public interface EquifaxFacade {

    public boolean isLimitReached(final AuditRecordEventType eventType);

    public void validateRequiredData(Customer customer, CustomerCreditCheck ccc);

    public CustomerCreditCheck runCreditCheck(PmcEquifaxInfo equifaxInfo, Customer customer, CustomerCreditCheck pcc, int strategyNumber,
    /* simulation data parameters */
    Lease lease, LeaseTermParticipant<?> leaseTermParticipant);

}
