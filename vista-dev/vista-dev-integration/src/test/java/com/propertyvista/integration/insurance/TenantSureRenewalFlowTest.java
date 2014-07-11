/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.integration.insurance;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy.TenantSureStatus;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.TenantSureTransactionTester;

@Category({ FunctionalTests.class })
public class TenantSureRenewalFlowTest extends InsuranceTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        createLease("2010-01-01", "2012-09-01");
        schedulePmcProcess(PmcProcessType.tenantSureRenewal);
    }

    public void testMonthlyTenantSureRenewal() throws Exception {
        setSysDate("2011-01-20");

        // buy
        TenantSureCoverageDTO quotationRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-20")));

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, getLease()._applicant());

        InsurancePaymentMethod paymentMethod = createInsurancePaymentMethod(getLease()._applicant());
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(paymentMethod, getLease()._applicant());
        Persistence.service().commit();

        TenantSureInsurancePolicy tenantSurePolicy = Persistence.service().retrieve(TenantSureInsurancePolicy.class, //
                ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, getLease()._applicant(), EntityFactory.create(CustomerSignature.class)));

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-01-20") //
                .lastRecordTransactionDate("2011-01-20");

        advanceSysDate("2011-12-21");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(12) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-12-21");

        advanceSysDate("2012-01-21");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(12) //    
                .lastRecordTransactionDate("2011-12-21");

        TenantSureInsurancePolicy tenantSurePolicyNew = Persistence.service().retrieve(TenantSureInsurancePolicy.class,
                ServerSideFactory.create(TenantSureFacade.class).getStatus(getLease()._applicant()).getPrimaryKey());
        assertTrue("Renewal and Original", !tenantSurePolicy.id().equals(tenantSurePolicyNew.id()));

        Persistence.ensureRetrieve(tenantSurePolicyNew.renewalOf(), AttachLevel.Attached);
        assertEquals("Renewal.renewalOf and Original", tenantSurePolicy.id().getValue(), tenantSurePolicyNew.renewalOf().id().getValue());

        tenantSurePolicy = Persistence.service().retrieve(TenantSureInsurancePolicy.class, tenantSurePolicy.getPrimaryKey());
        assertEquals("Original Cancelled", TenantSureStatus.Cancelled, tenantSurePolicy.status().getValue());
        Persistence.ensureRetrieve(tenantSurePolicy.renewal(), AttachLevel.IdOnly);
        assertEquals("Original renewal", tenantSurePolicy.renewal().getPrimaryKey(), tenantSurePolicyNew.getPrimaryKey());

        new TenantSureTransactionTester(tenantSurePolicyNew)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2012-01-20") //
                .lastRecordTransactionDate("2012-01-20");

        advanceSysDate("2013-01-22");

        new TenantSureTransactionTester(tenantSurePolicyNew)//
                .count(12) //    
                .lastRecordTransactionDate("2012-12-21");
    }

    public void testMonthlyTenantSureNoRenewalAfterCancel() throws Exception {
        setSysDate("2011-01-20");

        // buy
        TenantSureCoverageDTO quotationRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-20")));

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, getLease()._applicant());

        InsurancePaymentMethod paymentMethod = createInsurancePaymentMethod(getLease()._applicant());
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(paymentMethod, getLease()._applicant());
        Persistence.service().commit();

        TenantSureInsurancePolicy tenantSurePolicy = Persistence.service().retrieve(TenantSureInsurancePolicy.class, //
                ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, getLease()._applicant(), EntityFactory.create(CustomerSignature.class)));

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-01-20") //
                .lastRecordTransactionDate("2011-01-20");

        advanceSysDate("2011-12-21");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(12) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-12-21");

        advanceSysDate("2011-12-28");

        ServerSideFactory.create(TenantSureFacade.class).scheduleCancelByTenant(getLease()._applicant());

        advanceSysDate("2012-01-21");

        // Old no payments
        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(12) //    
                .lastRecordTransactionDate("2011-12-21");

        TenantSureInsurancePolicy tenantSurePolicyNew = Persistence.service().retrieve(TenantSureInsurancePolicy.class,
                ServerSideFactory.create(TenantSureFacade.class).getStatus(getLease()._applicant()).getPrimaryKey());

        assertEquals("Current and Original", tenantSurePolicy.id().getValue(), tenantSurePolicyNew.id().getValue());

    }
}
