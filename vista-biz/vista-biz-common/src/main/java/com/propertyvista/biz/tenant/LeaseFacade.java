/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;

public interface LeaseFacade {

    Lease initLease(Lease lease);

    Lease setUnit(Lease leaseId, AptUnit unitId);

    void persistLease(Lease lease);

    void saveAsFinal(Lease lease);

    void createMasterOnlineApplication(Key leaseId);

    void approveApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void activate(Key leaseId);

    /**
     * Start notice/evict...
     */
    void createCompletionEvent(Key leaseId, Lease.CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay);

    void cancelCompletionEvent(Key leaseId);

    void complete(Key leaseId);

    void close(Key leaseId);

    void verifyExistingLease(Lease leaseId);

}