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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.payment.PreauthorizedPayment.PreauthorizedPaymentCoveredItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseDataModel extends MockDataModel<Lease> {

    public LeaseDataModel() {

    }

    @Override
    protected void generate() {
    }

    protected Lease addLease(Building building, String leaseDateFrom, String leaseDateTo) {
        return addLease(building, leaseDateFrom, leaseDateTo, null, null);
    }

    public Lease addLease(Building building, String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance) {
        return addLease(building, leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance,
                Arrays.asList(new Customer[] { getDataModel(CustomerDataModel.class).addCustomer() }));
    }

    public Lease addLease(Building building, String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance, Customer customer) {
        return this.addLease(building, leaseDateFrom, leaseDateTo, agreedPrice, carryforwardBalance, Arrays.asList(new Customer[] { customer }));
    }

    public Lease addLease(Building building, String leaseDateFrom, String leaseDateTo, BigDecimal agreedPrice, BigDecimal carryforwardBalance,
            List<Customer> customers) {

        for (Customer customer : customers) {
            getDataModel(CustomerDataModel.class).addPaymentMethod(customer, building, PaymentType.Echeck);
        }

        ProductItem serviceItem = getDataModel(BuildingDataModel.class).addResidentialUnitServiceItem(building, new BigDecimal("930.30"));

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

        lease.billingAccount().billingPeriod().setValue(BillingPeriod.Monthly);

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

        lease.billingAccount().carryforwardBalance().setValue(carryforwardBalance);

        lease.creationDate().setValue(new LogicalDate(getSysDate()));

        ServerSideFactory.create(LeaseFacade.class).persist(lease);

        Persistence.service().commit();

        addItem(lease);
        return lease;
    }

    public LeasePaymentMethod getPaymentMethod(Lease lease, PaymentType type) {
        Persistence.service().retrieveMember(lease.leaseParticipants());
        // Get first tenant
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        List<LeasePaymentMethod> profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods(tenant.customer());

        for (LeasePaymentMethod paymentMethod : profileMethods) {
            if (paymentMethod.type().getValue() == type) {
                return paymentMethod;
            }
        }
        throw new Error("PaymentMethod not found");
    }

    public PaymentRecord createPaymentRecord(Lease lease, PaymentType type, String amount) {
        return createPaymentRecord(lease, getPaymentMethod(lease, type), amount);
    }

    public PaymentRecord createPaymentRecord(Lease lease, LeasePaymentMethod paymentMethod, String amount) {
        // Just use the first tenant
        LeaseTermParticipant<?> leaseParticipant = lease.currentTerm().version().tenants().get(0);

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.leaseTermParticipant().set(leaseParticipant);

        paymentRecord.paymentMethod().set(paymentMethod);

        ServerSideFactory.create(PaymentFacade.class).persistPayment(paymentRecord);
        return paymentRecord;
    }

    /**
     * 
     * Set Preauthorized Payment to first found tenant with eCheck payment method, otherwise returns false
     */
    public PreauthorizedPayment createPreauthorizedPayment(Lease lease, List<PreauthorizedPaymentCoveredItem> items) {
        Persistence.service().retrieveMember(lease.leaseParticipants());
        // Get first tenant
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        List<LeasePaymentMethod> profileMethods = getDataModel(CustomerDataModel.class).retrieveSerializableProfilePaymentMethods(tenant.customer());

        for (LeasePaymentMethod paymentMethod : profileMethods) {
            if (paymentMethod.type().getValue() == PaymentType.Echeck) {
                PreauthorizedPayment pap = EntityFactory.create(PreauthorizedPayment.class);
                pap.paymentMethod().set(paymentMethod);
                pap.coveredItems().addAll(items);
                pap.comments().setValue("Preauthorized Payment");
                ServerSideFactory.create(PaymentMethodFacade.class).persistPreauthorizedPayment(pap, tenant);
                return pap;
            }
        }
        return null;
    }

}
