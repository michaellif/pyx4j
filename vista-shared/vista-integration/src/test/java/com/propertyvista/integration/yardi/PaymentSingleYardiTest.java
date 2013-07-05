/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.yardi;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.PaymentException;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class PaymentSingleYardiTest extends PaymentYardiTestBase {

    private Lease lease;

    private LeasePaymentMethod paymentMethod;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setSysDate("2011-01-01");
        YardiResidentTransactionsService.getInstance().updateAll(getYardiCredential("prop123"), new ExecutionMonitor());

        loadBuildingToModel("prop123");

        lease = loadLeaseToModel("t000111");
        Tenant tenant = lease.leaseParticipants().iterator().next().cast();
        paymentMethod = getDataModel(CustomerDataModel.class).addPaymentMethod(tenant.customer(), lease.unit().building(), PaymentType.Echeck);
        Persistence.service().commit();
    }

    public void testPadSuccessful() throws Exception {
        // Make a payment
        final PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(lease, paymentMethod, "100");
        Persistence.service().commit();

        if (true) {
            //TODO this test do not work on HSQL
            return;
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, PaymentException>() {
            @Override
            public Void execute() throws PaymentException {
                ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
                return null;
            }
        });

//        ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
//        Persistence.service().commit();

        new PaymentRecordTester(lease.billingAccount()).lastRecordStatus(PaymentStatus.Queued);
    }
}
