/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicyClient;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureCoverageDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;

public class CfcApiAdapterFacadeMockupImpl implements CfcApiAdapterFacade {

    @Override
    public String createClient(Tenant tenant, String name, String phone) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return "MockupTSClient-" + RandomStringUtils.randomAlphanumeric(10);
    }

    @Override
    public TenantSureQuoteDTO getQuote(TenantSureInsurancePolicyClient client, TenantSureCoverageDTO coverageRequest) {
        TenantSureQuoteDTO quote = EntityFactory.create(TenantSureQuoteDTO.class);
        quote.annualPremium().setValue(new BigDecimal(10 + Math.abs(new Random().nextInt() % 50)));
        quote.underwriterFee().setValue(new BigDecimal(10 + Math.abs(new Random().nextInt() % 50)));
        quote.totalAnnualTax().setValue(new BigDecimal(Math.abs(new Random().nextInt() % 50)));
        quote.totalAnnualPayable().setValue(new BigDecimal(10 + Math.abs(new Random().nextInt() % 50)));
        quote.totalFirstPayable().setValue(new BigDecimal(10 + Math.abs(new Random().nextInt() % 50)));
        quote.totalAnniversaryFirstMonthPayable().setValue(new BigDecimal(10 + Math.abs(new Random().nextInt() % 50)));
        quote.totalMonthlyPayable().setValue(new BigDecimal(10 + Math.abs(new Random().nextInt() % 50)));
        quote.quoteId().setValue("MockupTSQuote-" + RandomStringUtils.randomAlphanumeric(10));
        quote.coverage().set(coverageRequest.duplicate(TenantSureCoverageDTO.class));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return quote;

    }

    @Override
    public String bindQuote(String quoteId) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new Error(e);
        }
        return "MockupTSCert-" + RandomStringUtils.randomAlphanumeric(10);
    }

    @Override
    public void requestDocument(String quoteId, List<String> emails) {

    }

    @Override
    public LogicalDate cancel(String policyId, CancellationType cancellationType, String toAddress) {
        return new LogicalDate();
    }

    @Override
    public void reinstate(String policyId, ReinstatementType reinstatementType, String toAddress) {
        // TODO Auto-generated method stub
    }

}
