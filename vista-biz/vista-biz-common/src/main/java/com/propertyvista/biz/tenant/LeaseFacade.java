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

import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;

public interface LeaseFacade {

    // in-memory Lease object state interfaces:

    Lease init(Lease lease);

    Lease setUnit(Lease lease, AptUnit unitId);

    Lease setService(Lease lease, ProductItem serviceId);

    BillableItem createBillableItem(ProductItem itemId);

    List<Deposit> createBillableItemDeposits(BillableItem item, PolicyNode node);

    Lease persist(Lease lease);

    Lease saveAsFinal(Lease lease);

    // DB-data Lease object state interfaces:

    void createMasterOnlineApplication(Key leaseId);

    void approveApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void approveExistingLease(Lease leaseId);

    void activate(Key leaseId);

    /**
     * Start notice/evict...
     */
    void createCompletionEvent(Key leaseId, Lease.CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay);

    void cancelCompletionEvent(Key leaseId);

    void complete(Key leaseId);

    void close(Key leaseId);
}