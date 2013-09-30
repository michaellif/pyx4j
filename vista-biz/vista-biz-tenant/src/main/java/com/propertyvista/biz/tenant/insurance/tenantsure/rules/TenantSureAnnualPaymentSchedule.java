/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.rules;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;

public class TenantSureAnnualPaymentSchedule implements ITenantSurePaymentSchedule {

    @Override
    public void prepareQuote(TenantSureQuoteDTO quote, BigDecimal annualPremium, BigDecimal underwritingFee, BigDecimal totalAnnualTax) {
        quote.paymentSchedule().setValue(TenantSurePaymentSchedule.Annual);
        quote.annualPremium().setValue(annualPremium);
        quote.underwriterFee().setValue(underwritingFee);
        quote.totalAnnualTax().setValue(totalAnnualTax);

        quote.totalAnnualPayable().setValue(annualPremium.add(underwritingFee).add(totalAnnualTax));
    }

    @Override
    public TenantSureTransaction initFirstTransaction(TenantSureInsurancePolicy insuranceTenantSure, InsurancePaymentMethod paymentMethod) {
        TenantSureTransaction transaction = EntityFactory.create(TenantSureTransaction.class);
        transaction.insurance().set(insuranceTenantSure);
        transaction.paymentMethod().set(paymentMethod);
        transaction.status().setValue(TenantSureTransaction.TransactionStatus.Draft);
        transaction.amount().setValue(insuranceTenantSure.totalAnnualPayable().getValue());
        transaction.paymentDue().setValue(insuranceTenantSure.certificate().inceptionDate().getValue());
        return transaction;
    }

}
