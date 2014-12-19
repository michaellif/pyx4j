/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2014
 * @author vlads
 */
package com.propertyvista.integration.insurance;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.tenant.insurance.TenantSureFacade;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.integration.TenantSureTransactionTester;

@Category({ FunctionalTests.class })
public class TenantSureBuyFlowTest extends InsuranceTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();

        createLease("2010-01-01", "2012-09-01");
    }

    public void testBuyTenantSureNow() throws Exception {
        setSysDate("2011-01-05");

        TenantSureCoverageDTO quotationRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-05")));

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, getLease()._applicant());

        InsurancePaymentMethod paymentMethod = createInsurancePaymentMethod(getLease()._applicant());
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(paymentMethod, getLease()._applicant());
        Persistence.service().commit();

        TenantSureInsurancePolicy tenantSurePolicy = Persistence.service().retrieve(TenantSureInsurancePolicy.class, //
                ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, getLease()._applicant(), EntityFactory.create(CustomerSignature.class)));

        assertEquals("paymentDay", 5, tenantSurePolicy.paymentDay().getValue().intValue());

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-01-05") //
                .lastRecordTransactionDate("2011-01-05");

        advanceSysDate("2011-02-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(2) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-02-05") //
                .lastRecordTransactionDate("2011-02-06");

        advanceSysDate("2011-03-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(3) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-03-05") //
                .lastRecordTransactionDate("2011-03-06");
    }

    public void testBuyTenantSureFutureDate() throws Exception {
        setSysDate("2011-01-05");

        TenantSureCoverageDTO quotationRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-20")));

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, getLease()._applicant());

        InsurancePaymentMethod paymentMethod = createInsurancePaymentMethod(getLease()._applicant());
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(paymentMethod, getLease()._applicant());
        Persistence.service().commit();

        TenantSureInsurancePolicy tenantSurePolicy = Persistence.service().retrieve(TenantSureInsurancePolicy.class, //
                ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, getLease()._applicant(), EntityFactory.create(CustomerSignature.class)));

        assertEquals("paymentDay", 20, tenantSurePolicy.paymentDay().getValue().intValue());

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-01-05") //
                .lastRecordTransactionDate("2011-01-05");

//        //---
//
//        setSysDate("2011-01-20");
//        advanceSysDate("2011-01-21");
//        new TenantSureTransactionTester(tenantSurePolicy)//
//                .lastRecordTransactionDate("2011-01-21") //
//                .count(1);
//
//        //-----------

        advanceSysDate("2011-02-07");

        // Now new transaction
        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordTransactionDate("2011-01-05");

        advanceSysDate("2011-02-22");

        // Payment in advance
        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(2) //    
                .lastRecordTransactionDate("2011-02-21");

        advanceSysDate("2011-03-22");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(3) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-03-21");
    }

    public void testTenantSureNoRenewal() throws Exception {
        setSysDate("2011-01-05");

        TenantSureCoverageDTO quotationRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.detectDateformat("2011-01-05")));

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, getLease()._applicant());

        InsurancePaymentMethod paymentMethod = createInsurancePaymentMethod(getLease()._applicant());
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(paymentMethod, getLease()._applicant());
        Persistence.service().commit();

        TenantSureInsurancePolicy tenantSurePolicy = Persistence.service().retrieve(TenantSureInsurancePolicy.class, //
                ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, getLease()._applicant(), EntityFactory.create(CustomerSignature.class)));

        assertEquals("paymentDay", 5, tenantSurePolicy.paymentDay().getValue().intValue());

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-01-05") //
                .lastRecordTransactionDate("2011-01-05");

        advanceSysDate("2011-02-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(2) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-02-05") //
                .lastRecordTransactionDate("2011-02-06");

        advanceSysDate("2011-03-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(3) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordPaymentDue("2011-03-05") //
                .lastRecordTransactionDate("2011-03-06");

        advanceSysDate("2011-04-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(4) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-04-06");

        advanceSysDate("2011-05-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(5) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-05-06");

        advanceSysDate("2011-06-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(6) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-06-06");

        advanceSysDate("2011-07-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(7) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-07-06");

        advanceSysDate("2011-08-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(8) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-08-06");

        advanceSysDate("2011-09-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(9) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-09-06");

        advanceSysDate("2011-10-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(10) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-10-06");

        advanceSysDate("2011-11-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(11) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-11-06");

        advanceSysDate("2011-12-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(12) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-12-06");

        advanceSysDate("2012-02-07");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(12) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordTransactionDate("2011-12-06");
    }
}
