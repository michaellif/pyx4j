/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.yardi;

import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.tenant.lease.LeaseAbstractManager;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;

public class LeaseFacadeYardiImpl implements LeaseFacade {

    @Override
    public Lease create(Status status) {
        if (status == Status.Application) {
            return new LeaseYardiApplicationManager().create(status);
        } else {
            return new LeaseYardiImportManager().create(status);
        }
    }

    @Override
    public Lease init(Lease lease) {
        return getLeaseYardiManager(lease).init(lease);
    }

    @Override
    public Lease setUnit(Lease lease, AptUnit unitId) {
        return getLeaseYardiManager(lease).setUnit(lease, unitId);
    }

    @Override
    public Lease setService(Lease lease, ProductItem serviceId) {
        return getLeaseYardiManager(lease).setService(lease, serviceId);
    }

    @Override
    public Lease persist(Lease lease) {
        return getLeaseYardiManager(lease).persist(lease);
    }

    @Override
    public Lease finalize(Lease lease) {
        return getLeaseYardiManager(lease).finalize(lease);
    }

    @Override
    public Lease load(Lease leaseId, boolean forEdit) {
        return getLeaseYardiManager(retrieveLeseStatus(leaseId)).load(leaseId, forEdit);
    }

    @Override
    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        return getLeaseYardiManager(retrieve(leaseTerm)).setUnit(leaseTerm, unitId);
    }

    @Override
    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        return getLeaseYardiManager(retrieve(leaseTerm)).setService(leaseTerm, serviceId);
    }

    @Override
    public LeaseTerm setPackage(LeaseTerm leaseTerm, AptUnit unitId, BillableItem serviceItem, List<BillableItem> featureItems) {
        return getLeaseYardiManager(retrieve(leaseTerm)).setPackage(leaseTerm, unitId, serviceItem, featureItems);
    }

    @Override
    public LeaseTerm persist(LeaseTerm leaseTerm) {
        return getLeaseYardiManager(retrieve(leaseTerm)).persist(leaseTerm);
    }

    @Override
    public LeaseTerm finalize(LeaseTerm leaseTerm) {
        return getLeaseYardiManager(retrieve(leaseTerm)).finalize(leaseTerm);
    }

    @Override
    public void createMasterOnlineApplication(Lease leaseId, Building building, Floorplan floorplan) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).createMasterOnlineApplication(leaseId, building, floorplan);
    }

    @Override
    public void cancelMasterOnlineApplication(Lease leaseId) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).cancelMasterOnlineApplication(leaseId);
    }

    @Override
    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).declineApplication(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).cancelApplication(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void approve(Lease leaseId, Employee decidedBy, String decisionReason) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).approve(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void activate(Lease leaseId) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).activate(leaseId);
    }

    @Override
    public void renew(Lease leaseId) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).renew(leaseId);
    }

    @Override
    public void complete(Lease leaseId) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).complete(leaseId);
    }

    @Override
    public void close(Lease leaseId) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).close(leaseId);
    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, Type type) {
        return getLeaseYardiManager(retrieveLeseStatus(leaseId)).createOffer(leaseId, null, type);
    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, AptUnit unitId, Type type) {
        return getLeaseYardiManager(retrieveLeseStatus(leaseId)).createOffer(leaseId, unitId, type);
    }

    @Override
    public void acceptOffer(Lease leaseId, LeaseTerm leaseTermId) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).acceptOffer(leaseId, leaseTermId);
    }

    @Override
    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDate, LogicalDate leaseEndDate) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).createCompletionEvent(leaseId, completionType, eventDate, moveOutDate, leaseEndDate);
    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).cancelCompletionEvent(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).cancelLease(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void moveOut(Lease leaseId, LogicalDate actualMoveOut) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).moveOut(leaseId, actualMoveOut);
    }

    @Override
    public BillableItem createBillableItem(Lease lease, ProductItem itemId) {
        return getLeaseYardiManager(lease).createBillableItem(lease, itemId);
    }

    @Override
    public void updateLeaseDates(Lease lease) {
        getLeaseYardiManager(lease).updateLeaseDates(lease);
    }

    @Override
    public boolean isMoveOutWithinNextBillingCycle(Lease leaseId) {
        return getLeaseYardiManager(retrieveLeseStatus(leaseId)).isMoveOutWithinNextBillingCycle(leaseId);
    }

    @Override
    public void simpleLeaseRenew(Lease leaseId, LogicalDate leaseEndDate) {
        getLeaseYardiManager(retrieveLeseStatus(leaseId)).simpleLeaseRenew(leaseId, leaseEndDate);
    }

    @Override
    public Building getLeasePolicyNode(Lease leaseId) {
        return getLeaseYardiManager(retrieveLeseStatus(leaseId)).getLeasePolicyNode(leaseId);
    }

    // internals:

    private Lease retrieveLeseStatus(Lease leaseId) {
        if (leaseId.isValueDetached()) {
            return Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        } else {
            return leaseId;
        }
    }

    private Lease retrieve(LeaseTerm leaseTerm) {
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);
        return leaseTerm.lease();
    }

    private LeaseAbstractManager getLeaseYardiManager(Lease lease) {
        if (lease.status().getValue() == Status.Application) {
            return new LeaseYardiApplicationManager();
        } else {
            return new LeaseYardiImportManager();
        }
    }
}
