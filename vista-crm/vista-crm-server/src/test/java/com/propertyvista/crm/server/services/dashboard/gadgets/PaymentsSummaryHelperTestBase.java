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
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.test.helper.LightWeightLeaseManagement;

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
            IdAssignmentItem leaseAssignmentItem = idAssignmentPolicy.items().$();
            leaseAssignmentItem.target().setValue(IdTarget.lease);
            leaseAssignmentItem.type().setValue(IdAssignmentType.generatedNumber);
            idAssignmentPolicy.items().add(leaseAssignmentItem);
        }
        {
            IdAssignmentItem tenantAssignmentItem = idAssignmentPolicy.items().$();
            tenantAssignmentItem.target().setValue(IdTarget.tenant);
            tenantAssignmentItem.type().setValue(IdAssignmentType.generatedNumber);
            idAssignmentPolicy.items().add(tenantAssignmentItem);
        }
        {
            IdAssignmentItem tenantAssignmentItem = idAssignmentPolicy.items().$();
            tenantAssignmentItem.target().setValue(IdTarget.customer);
            tenantAssignmentItem.type().setValue(IdAssignmentType.generatedNumber);
            idAssignmentPolicy.items().add(tenantAssignmentItem);
        }

        Persistence.service().persist(idAssignmentPolicy);

        lease = LightWeightLeaseManagement.create(Lease.Status.Application);

        LeaseTermTenant tenant = lease.currentTerm().version().tenants().$();
        tenant.leaseParticipant().customer().person().name().firstName().setValue("Foo");
        tenant.leaseParticipant().customer().person().name().lastName().setValue("Foo");
        lease.currentTerm().version().tenants().add(tenant);
        lease.status().setValue(Lease.Status.ExistingLease);
        if (lease.unit().getPrimaryKey() != null) {
            LightWeightLeaseManagement.setUnit(lease, lease.unit());
        }
        lease = LightWeightLeaseManagement.persist(lease, false);

        merchantAccountA = makeMerchantAccount("A");
        merchantAccountB = makeMerchantAccount("B");

        Persistence.service().startTransaction();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            super.tearDown();
            SystemDateManager.resetDate();
        } finally {
            Persistence.service().commit();
            Persistence.service().endTransaction();
            TestLifecycle.tearDown();
        }
    }

    protected PaymentRecord makePaymentRecord(MerchantAccount merchantAccount, String lastStatusChangeDate, String amount, PaymentType paymentType,
            PaymentRecord.PaymentStatus paymentStatus) {
        LeasePaymentMethod paymentMethod = EntityFactory.create(LeasePaymentMethod.class);
        paymentMethod.type().setValue(paymentType);
        LeaseTermTenant tenant = lease.currentTerm().version().tenants().get(0);
        if (tenant.isValueDetached()) {
            Persistence.service().retrieve(tenant);
        }
        paymentMethod.customer().set(tenant.leaseParticipant().customer());
        Persistence.service().persist(paymentMethod);

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(detectDateformat(lastStatusChangeDate)));
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentMethod().set(paymentMethod);
        paymentRecord.paymentStatus().setValue(paymentStatus);
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.merchantAccount().set(merchantAccount);
        paymentRecord.leaseTermParticipant().set(lease.currentTerm().version().tenants().get(0));

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
