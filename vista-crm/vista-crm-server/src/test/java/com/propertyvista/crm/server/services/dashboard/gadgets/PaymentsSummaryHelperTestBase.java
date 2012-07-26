/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import static com.pyx4j.gwt.server.DateUtils.detectDateformat;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;

public class PaymentsSummaryHelperTestBase extends VistaDBTestBase {

    protected Lease lease;

    protected MerchantAccount merchantAccountA;

    protected MerchantAccount merchantAccountB;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestLifecycle.testSession(new UserVisit(new Key(-101), "Neo The Accountant"), VistaCrmBehavior.Occupancy, VistaBasicBehavior.CRM);
        TestLifecycle.beginRequest();

        OrganizationPoliciesNode policyNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(policyNode);
        IdAssignmentPolicy idAssignmentPolicy = EntityFactory.create(IdAssignmentPolicy.class);
        idAssignmentPolicy.node().set(policyNode);
        {
            IdAssignmentItem leaseAssignmentItem = idAssignmentPolicy.itmes().$();
            leaseAssignmentItem.target().setValue(IdTarget.lease);
            leaseAssignmentItem.type().setValue(IdAssignmentType.generatedNumber);
            idAssignmentPolicy.itmes().add(leaseAssignmentItem);
        }
        {
            IdAssignmentItem tenantAssignmentItem = idAssignmentPolicy.itmes().$();
            tenantAssignmentItem.target().setValue(IdTarget.tenant);
            tenantAssignmentItem.type().setValue(IdAssignmentType.generatedNumber);
            idAssignmentPolicy.itmes().add(tenantAssignmentItem);
        }
        {
            IdAssignmentItem tenantAssignmentItem = idAssignmentPolicy.itmes().$();
            tenantAssignmentItem.target().setValue(IdTarget.customer);
            tenantAssignmentItem.type().setValue(IdAssignmentType.generatedNumber);
            idAssignmentPolicy.itmes().add(tenantAssignmentItem);
        }

        Persistence.service().persist(idAssignmentPolicy);

        lease = EntityFactory.create(Lease.class);

        Tenant tenant = lease.version().tenants().$();
        tenant.customer().person().name().firstName().setValue("Foo");
        tenant.customer().person().name().lastName().setValue("Foo");
        lease.version().tenants().add(tenant);
        lease.status().setValue(Lease.Status.Created);
        lease = ServerSideFactory.create(LeaseFacade.class).init(lease);
        if (lease.unit().getPrimaryKey() != null) {
            ServerSideFactory.create(LeaseFacade.class).setUnit(lease, lease.unit());
        }
        lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);

        merchantAccountA = makeMerchantAccount("A");
        merchantAccountB = makeMerchantAccount("B");

        Persistence.service().startTransaction();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            super.tearDown();
            Persistence.service().setTransactionSystemTime(null);
        } finally {
            Persistence.service().commit();
            Persistence.service().endTransaction();
            TestLifecycle.tearDown();
        }
    }

    protected PaymentRecord makePaymentRecord(MerchantAccount merchantAccount, String lastStatusChangeDate, String amount, PaymentType paymentType,
            PaymentRecord.PaymentStatus paymentStatus) {
        PaymentMethod paymentMethod = EntityFactory.create(PaymentMethod.class);
        paymentMethod.type().setValue(paymentType);
        Tenant tenant = lease.version().tenants().get(0);
        if (tenant.isValueDetached()) {
            Persistence.service().retrieve(tenant);
        }
        paymentMethod.customer().set(tenant.customer());
        Persistence.service().persist(paymentMethod);

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(detectDateformat(lastStatusChangeDate)));
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentMethod().set(paymentMethod);
        paymentRecord.paymentStatus().setValue(paymentStatus);
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.merchantAccount().set(merchantAccount);
        paymentRecord.leaseParticipant().set(lease.version().tenants().get(0));

        Persistence.service().persist(paymentRecord);

        return paymentRecord;
    }

    private MerchantAccount makeMerchantAccount(String accountNumber) {
        MerchantAccount merchantAccount = EntityFactory.create(MerchantAccount.class);
        merchantAccount.accountNumber().setValue(accountNumber);
        Persistence.service().persist(merchantAccount);
        return merchantAccount;
    }

}
