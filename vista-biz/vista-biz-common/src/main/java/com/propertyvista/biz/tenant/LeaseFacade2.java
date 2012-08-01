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
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public interface LeaseFacade2 {

    // in-memory Lease2 object state interfaces:

    Lease2 init(Lease2 lease);

    Lease2 setUnit(Lease2 lease, AptUnit unitId);

    Lease2 persist(Lease2 lease);

    // Lease term operations:

    LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId);

    BillableItem createBillableItem(ProductItem itemId, PolicyNode node);

    LeaseTerm persist(LeaseTerm leaseTerm);

    LeaseTerm finalize(LeaseTerm leaseTerm);

    // DB-data Lease2 object state interfaces:

    void setCurrentTerm(Lease2 leaseId, LeaseTerm leaseTermId);

    void createMasterOnlineApplication(Key leaseId);

    void approveApplication(Lease2 leaseId, Employee decidedBy, String decisionReason);

    void declineApplication(Lease2 leaseId, Employee decidedBy, String decisionReason);

    void cancelApplication(Lease2 leaseId, Employee decidedBy, String decisionReason);

    void approveExistingLease(Lease2 leaseId);

    void activate(Key leaseId);

    /**
     * Start notice/evict...
     */
    void createCompletionEvent(Key leaseId, Lease2.CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay);

    void cancelCompletionEvent(Key leaseId);

    void complete(Key leaseId);

    void close(Key leaseId);
}