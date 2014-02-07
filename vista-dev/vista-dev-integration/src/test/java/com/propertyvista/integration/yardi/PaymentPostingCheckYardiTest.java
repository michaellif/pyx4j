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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.DateUtils;

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
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.test.mock.models.LeaseDataModel;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;

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
    }

    public void testPaymentPostingWithTriggeredLeaseUpdate() throws Exception {
        final PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), createCheckPaymentMethod(tenant.customer()),
                "100");
        Persistence.service().commit();

        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("initial lease version", Integer.valueOf(2), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        // Make change in Yardi before posting payment.
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop1", "t000111"). //
                    set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2015-12-31"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }
        {
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "rent"). //
                    set(LeaseChargeUpdater.Name.Amount, "1500.00");
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Exception>() {
            @Override
            public Void execute() throws Exception {
                ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
                return null;
            }
        });

        // TODO remove 
        int WRONG = 1;
        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("update lease version", Integer.valueOf(4 + WRONG), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);

        // test Reject while there are pending updates
        {
            RtCustomerUpdater updater = new RtCustomerUpdater("prop1", "t000111"). //
                    set(RtCustomerUpdater.YLEASE.LeaseToDate, DateUtils.detectDateformat("2016-12-31"));
            MockEventBus.fireEvent(new RtCustomerUpdateEvent(updater));
        }
        {
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "rent"). //
                    set(LeaseChargeUpdater.Name.Amount, "1400.00");
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Exception>() {
            @Override
            public Void execute() throws Exception {
                ServerSideFactory.create(PaymentFacade.class).reject(paymentRecord, true);
                return null;
            }
        });

        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("update lease version", Integer.valueOf(6 + WRONG), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

    }

    public void testPaymentPostingWithTriggeredLeaseChargeRemove() throws Exception {
        final PaymentRecord paymentRecord = getDataModel(LeaseDataModel.class).createPaymentRecord(getLease(), createCheckPaymentMethod(tenant.customer()),
                "100");
        Persistence.service().commit();

        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("initial lease version", Integer.valueOf(2), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        // Make change in Yardi before posting payment.
        {
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "rent"). //
                    set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2010-12-31"));
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }
        {
            LeaseChargeUpdater updater = new LeaseChargeUpdater("prop1", "t000111", "park"). //
                    set(LeaseChargeUpdater.Name.ServiceToDate, DateUtils.detectDateformat("2010-12-31"));
            MockEventBus.fireEvent(new LeaseChargeUpdateEvent(updater));
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Exception>() {
            @Override
            public Void execute() throws Exception {
                ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord, null);
                return null;
            }
        });

        // TODO remove 
        int WRONG = 1;
        {
            Lease leaseCurrent = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
            assertEquals("update lease version", Integer.valueOf(3 + WRONG + WRONG), leaseCurrent.currentTerm().version().versionNumber().getValue());
        }

        new PaymentRecordTester(getLease().billingAccount()).lastRecordStatus(PaymentStatus.Received);
    }
}
