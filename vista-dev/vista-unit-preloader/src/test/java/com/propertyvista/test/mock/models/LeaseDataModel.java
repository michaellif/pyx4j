/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseDataModel extends MockDataModel<Lease> {

    public LeaseDataModel() {

    }

    @Override
    protected void generate() {
    }

    protected void addLease(String leaseDateFrom, String leaseDateTo) {
        addLease(leaseDateFrom, leaseDateTo, null, null);
    }

    public Lease addLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance) {
        return this.addLease(leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance,
                Arrays.asList(new Customer[] { getDataModel(CustomerDataModel.class).addCustomer() }));
    }

    public Lease addLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance, Customer customer) {
        return this.addLease(leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance, Arrays.asList(new Customer[] { customer }));
    }

    public Lease addLease(String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance, List<Customer> customers) {

        ProductItem serviceItem = getDataModel(BuildingDataModel.class).addResidentialUnitServiceItem(new BigDecimal("930.30"));

        Lease lease;
        if (carryforwardBalance != null) {
            lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.ExistingLease);
        } else {
            lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.Application);
            ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(serviceItem.element().cast().getPrimaryKey());
        }

        lease.currentTerm().termFrom().setValue(parseDate(leaseDateFrom));
        lease.currentTerm().termTo().setValue(parseDate(leaseDateTo));

        ServerSideFactory.create(LeaseFacade.class).updateLeaseDates(lease);

        lease = ServerSideFactory.create(LeaseFacade.class).setUnit(lease, (AptUnit) serviceItem.element().cast());

        if (agreedPrice != null) {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(agreedPrice);
        } else if (serviceItem.price().getValue().compareTo(BigDecimal.ZERO) != 0) {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(serviceItem.price().getValue());
        } else {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(new BigDecimal(500 + DataGenerator.randomInt(500)));
        }

        for (int i = 0; i < customers.size(); i++) {
            LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
            tenantInLease.leaseParticipant().customer().set(customers.get(i));
            if (i == 0) {
                tenantInLease.role().setValue(LeaseTermParticipant.Role.Applicant);
            } else {
                tenantInLease.role().setValue(LeaseTermParticipant.Role.CoApplicant);
            }
            lease.currentTerm().version().tenants().add(tenantInLease);
        }

        lease.billingAccount().<InternalBillingAccount> cast().carryforwardBalance().setValue(carryforwardBalance);

        lease.creationDate().setValue(new LogicalDate(getSysDate()));

        ServerSideFactory.create(LeaseFacade.class).persist(lease);

        addItem(lease);
        return lease;
    }

    public PaymentRecord addPaymentRecord(LeasePaymentMethod paymentMethod, String amount) {
        // Just use the first tenant
        LeaseTermParticipant<?> leaseParticipant = getCurrentItem().currentTerm().version().tenants().iterator().next();

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        paymentRecord.billingAccount().set(getCurrentItem().billingAccount());
        paymentRecord.leaseTermParticipant().set(leaseParticipant);

        paymentRecord.paymentMethod().set(paymentMethod);

        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        return paymentRecord;
    }

}
