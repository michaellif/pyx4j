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
 * @version $Id$
 */
package com.propertyvista.integration.insurance;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
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

        createLease("01-01-2010", "01-09-2012");
    }

    public void testBuyTenantSure() throws Exception {
        setSysDate("05-01-2011");

        TenantSureCoverageDTO quotationRequest = EntityFactory.create(TenantSureCoverageDTO.class);
        quotationRequest.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);
        quotationRequest.inceptionDate().setValue(new LogicalDate(DateUtils.detectDateformat("05-01-2011")));

        TenantSureQuoteDTO quote = ServerSideFactory.create(TenantSureFacade.class).getQuote(quotationRequest, getLease()._applicant());

        InsurancePaymentMethod paymentMethod = createInsurancePaymentMethod(getLease()._applicant());
        ServerSideFactory.create(TenantSureFacade.class).savePaymentMethod(paymentMethod, getLease()._applicant());

        TenantSureInsurancePolicy tenantSurePolicy = EntityFactory.create(TenantSureInsurancePolicy.class);

        tenantSurePolicy.setPrimaryKey(ServerSideFactory.create(TenantSureFacade.class).buyInsurance(quote, getLease()._applicant(), "Bob", "1234567",
                EntityFactory.create(CustomerSignature.class)));

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(1) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared);

        advanceSysDate("06-02-2011");

        new TenantSureTransactionTester(tenantSurePolicy)//
                .count(2) //    
                .lastRecordStatus(TenantSureTransaction.TransactionStatus.Cleared) //
                .lastRecordDate("06-02-2011");
    }
}
