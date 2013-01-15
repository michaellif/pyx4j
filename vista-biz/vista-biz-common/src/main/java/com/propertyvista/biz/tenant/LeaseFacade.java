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

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public interface LeaseFacade {

    /*
     * Some conventions:
     * 
     * Input parameter like: Lease lease - means in-memory object (already loaded by caller).
     * 
     * Input parameter like: Lease leaseId - means DB-object (should be loaded from DB by callee).
     */

    // in-memory Lease state interface:

    Lease create(Status status);

    Lease init(Lease lease);

    Lease setUnit(Lease lease, AptUnit unitId);

    Lease setService(Lease lease, ProductItem serviceId);

    Lease persist(Lease lease);

    Lease finalize(Lease lease);

    Lease load(Lease leaseId, boolean forEdit);

    // in-memory Lease Term interface:

    LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId);

    LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId);

    LeaseTerm persist(LeaseTerm leaseTerm);

    LeaseTerm finalize(LeaseTerm leaseTerm);

    // DB-data Lease state interfaces:

    void createMasterOnlineApplication(Lease leaseId);

    void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void approve(Lease leaseId, Employee decidedBy, String decisionReason);

    void activate(Lease leaseId);

    void renew(Lease leaseId);

    void complete(Lease leaseId);

    void close(Lease leaseId);

    // DB-data LeaseTerm state interfaces:

    LeaseTerm createOffer(Lease leaseId, LeaseTerm.Type type);

    void acceptOffer(Lease leaseId, LeaseTerm leaseTermId);

    // Start notice/evict, cancel/terminate/etc...

    void createCompletionEvent(Lease leaseId, Lease.CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDate, LogicalDate leseEndDate);

    void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason);

    void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason);

    void moveOut(Lease leaseId);

    // Utils/internals:

    BillableItem createBillableItem(Lease lease, ProductItem itemId, PolicyNode node);

    void updateLeaseDates(Lease lease);

    void setLeaseAgreedPrice(Lease lease, BigDecimal price);

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    void simpleLeaseRenew(Lease leaseId, LogicalDate leaseEndDate);
}