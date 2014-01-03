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

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.AgreementLegalTermSignature;

public class LeaseFacadeYardiImpl implements LeaseFacade {

    @Override
    public Lease create(Status status) {
        return new LeaseYardiManager().create(status);
    }

    @Override
    public Lease init(Lease lease) {
        return new LeaseYardiManager().init(lease);
    }

    @Override
    public Lease setUnit(Lease lease, AptUnit unitId) {
        return new LeaseYardiManager().setUnit(lease, unitId);
    }

    @Override
    public Lease setService(Lease lease, ProductItem serviceId) {
        return new LeaseYardiManager().setService(lease, serviceId);
    }

    @Override
    public Lease persist(Lease lease) {
        return new LeaseYardiManager().persist(lease);
    }

    @Override
    public Lease finalize(Lease lease) {
        return new LeaseYardiManager().finalize(lease);
    }

    @Override
    public Lease load(Lease leaseId, boolean forEdit) {
        return new LeaseYardiManager().load(leaseId, forEdit);
    }

    @Override
    public Lease persist(Lease lease, boolean reserve) {
        return new LeaseYardiManager().persist(lease, reserve);
    }

    @Override
    public Lease finalize(Lease lease, boolean reserve) {
        return new LeaseYardiManager().finalize(lease, reserve);
    }

    @Override
    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        return new LeaseYardiManager().setUnit(leaseTerm, unitId);
    }

    @Override
    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        return new LeaseYardiManager().setService(leaseTerm, serviceId);
    }

    @Override
    public LeaseTerm setPackage(LeaseTerm leaseTerm, AptUnit unitId, BillableItem serviceItem, List<BillableItem> featureItems) {
        return new LeaseYardiManager().setPackage(leaseTerm, unitId, serviceItem, featureItems);
    }

    @Override
    public LeaseTerm persist(LeaseTerm leaseTerm) {
        return new LeaseYardiManager().persist(leaseTerm);
    }

    @Override
    public LeaseTerm finalize(LeaseTerm leaseTerm) {
        return new LeaseYardiManager().finalize(leaseTerm);
    }

    @Override
    public void createMasterOnlineApplication(Lease leaseId, Building building, Floorplan floorplan) {
        new LeaseYardiManager().createMasterOnlineApplication(leaseId, building, floorplan);
    }

    @Override
    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        new LeaseYardiManager().declineApplication(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        new LeaseYardiManager().cancelApplication(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void approve(Lease leaseId, Employee decidedBy, String decisionReason) {
        new LeaseYardiManager().approve(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void activate(Lease leaseId) {
        new LeaseYardiManager().activate(leaseId);
    }

    @Override
    public void renew(Lease leaseId) {
        new LeaseYardiManager().renew(leaseId);
    }

    @Override
    public void complete(Lease leaseId) {
        new LeaseYardiManager().complete(leaseId);
    }

    @Override
    public void close(Lease leaseId) {
        new LeaseYardiManager().close(leaseId);
    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, Type type) {
        return new LeaseYardiManager().createOffer(leaseId, null, type);
    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, AptUnit unitId, Type type) {
        return new LeaseYardiManager().createOffer(leaseId, unitId, type);
    }

    @Override
    public void acceptOffer(Lease leaseId, LeaseTerm leaseTermId) {
        new LeaseYardiManager().acceptOffer(leaseId, leaseTermId);
    }

    @Override
    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDate, LogicalDate leaseEndDate) {
        new LeaseYardiManager().createCompletionEvent(leaseId, completionType, eventDate, moveOutDate, leaseEndDate);
    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        new LeaseYardiManager().cancelCompletionEvent(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        new LeaseYardiManager().cancelLease(leaseId, decidedBy, decisionReason);
    }

    @Override
    public void moveOut(Lease leaseId, LogicalDate actualMoveOut) {
        new LeaseYardiManager().moveOut(leaseId, actualMoveOut);
    }

    @Override
    public BillableItem createBillableItem(Lease lease, ProductItem itemId, PolicyNode node) {
        return new LeaseYardiManager().createBillableItem(lease, itemId, node);
    }

    @Override
    public void updateLeaseDates(Lease lease) {
        new LeaseYardiManager().updateLeaseDates(lease);
    }

    @Override
    public void setLeaseAgreedPrice(Lease lease, BigDecimal price) {
        new LeaseYardiManager().setLeaseAgreedPrice(lease, price);
    }

    @Override
    public boolean isMoveOutWithinNextBillingCycle(Lease leaseId) {
        return new LeaseYardiManager().isMoveOutWithinNextBillingCycle(leaseId);
    }

    @Override
    public void simpleLeaseRenew(Lease leaseId, LogicalDate leaseEndDate) {
        new LeaseYardiManager().simpleLeaseRenew(leaseId, leaseEndDate);
    }

}
