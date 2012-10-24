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

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public interface LeaseFacade {

    // in-memory Lease state interfaces:

    Lease create(Status status);

    Lease init(Lease lease);

    Lease setUnit(Lease lease, AptUnit unitId);

    Lease setService(Lease lease, ProductItem serviceId);

    Lease persist(Lease lease);

    Lease finalize(Lease lease);

    // in-memory Lease Term operations:

    LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId);

    LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId);

    LeaseTerm persist(LeaseTerm leaseTerm);

    LeaseTerm finalize(LeaseTerm leaseTerm);

    // DB-data Lease state interfaces:

    void createMasterOnlineApplication(Lease leaseId);

    void approveApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason);

    void approveExistingLease(Lease leaseId);

    void activate(Lease leaseId);

    void renew(Lease leaseId);

    void complete(Lease leaseId);

    void complete(Lease leaseId, LogicalDate from);

    void close(Lease leaseId);

    // DB-data LeaseTerm state interfaces:

    LeaseTerm createOffer(Lease leaseId, LeaseTerm.Type type);

    void acceptOffer(Lease leaseId, LeaseTerm leaseTermId);

    // Start notice/evict...

    void createCompletionEvent(Lease leaseId, Lease.CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay);

    void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason);

    void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason);

    // Utils:

    Lease load(Lease leaseId, boolean forEdit);

    BillableItem createBillableItem(ProductItem itemId, PolicyNode node);

    boolean isProductAvailable(Lease lease, Product<? extends Product.ProductV<?>> product);
}