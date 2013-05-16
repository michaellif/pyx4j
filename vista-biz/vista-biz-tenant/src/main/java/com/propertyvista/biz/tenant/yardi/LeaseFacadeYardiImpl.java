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
package com.propertyvista.biz.tenant.yardi;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.policy.framework.PolicyNode;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Lease init(Lease lease) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Lease setUnit(Lease lease, AptUnit unitId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Lease setService(Lease lease, ProductItem serviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Lease persist(Lease lease) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Lease finalize(Lease lease) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Lease load(Lease leaseId, boolean forEdit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LeaseTerm persist(LeaseTerm leaseTerm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LeaseTerm finalize(LeaseTerm leaseTerm) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createMasterOnlineApplication(Lease leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void approve(Lease leaseId, Employee decidedBy, String decisionReason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void activate(Lease leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void renew(Lease leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void complete(Lease leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close(Lease leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, Type type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void acceptOffer(Lease leaseId, LeaseTerm leaseTermId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDate, LogicalDate leaseEndDate) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveOut(Lease leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public BillableItem createBillableItem(Lease lease, ProductItem itemId, PolicyNode node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateLeaseDates(Lease lease) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLeaseAgreedPrice(Lease lease, BigDecimal price) {
        // TODO Auto-generated method stub

    }

    @Override
    public void simpleLeaseRenew(Lease leaseId, LogicalDate leaseEndDate) {
        // TODO Auto-generated method stub

    }

}
