/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.models.LeaseDataModel;

@Category(FunctionalTests.class)
public class PaymentPostingCheckYardiTest extends PaymentYardiTestBase {

    private Lease lease;

    private Tenant tenant;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        createYardiBuilding("prop1");
        createYardiLease("prop1", "t000111");
        setSysDate("2011-01-01");
        yardiImportAll(getYardiCredential("prop1"));
        loadBuildingToModel("prop1");
        lease = loadLeaseToModel("t000111");
        tenant = lease.leaseParticipants().iterator().next().cast();
    }

    protected Lease getLease() {
        return lease;
    }

    private LeasePaymentMethod createCheckPaymentMethod(Customer customer) {
        LeasePaymentMethod paymentMethod = EntityFactory.create(LeasePaymentMethod.class);
        paymentMethod.customer().set(customer);
        paymentMethod.type().setValue(PaymentType.Check);
        paymentMethod.isProfiledMethod().setValue(Boolean.TRUE);
        CheckInfo details = EntityFactory.create(CheckInfo.class);
        paymentMethod.details().set(details);
        return paymentMethod;
    }

    public void testSuccessfulPaymentPosting() throws Exception {
        PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), createCheckPaymentMethod(tenant.customer()), "100");
        Persistence.service().commit();

        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
        Persistence.service().commit();

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);

        // test Reject
        ServerSideFactory.create(PaymentFacade.class).reject(paymentRecord, true);
        Persistence.service().commit();

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Rejected);
    }

}
