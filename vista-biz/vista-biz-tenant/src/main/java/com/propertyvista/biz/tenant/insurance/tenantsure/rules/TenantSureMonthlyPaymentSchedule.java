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
import java.math.RoundingMode;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters.TenantSureCfcMoneyAdapter;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.domain.tenant.insurance.TenantSureInsurancePolicy;
import com.propertyvista.domain.tenant.insurance.TenantSurePaymentSchedule;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureQuoteDTO;

public class TenantSureMonthlyPaymentSchedule implements ITenantSurePaymentSchedule {

    @Override
    public void prepareQuote(TenantSureQuoteDTO quote, BigDecimal annualPremium, BigDecimal underwritingFee, BigDecimal totalAnnualTax) {
        quote.paymentSchedule().setValue(TenantSurePaymentSchedule.Monthly);

        RoundingMode rm = TenantSureCfcMoneyAdapter.getRoundingMode();

        BigDecimal annualGross = annualPremium.add(totalAnnualTax);

        // Make  the total of the monthly payments to be equal the annual total.
        BigDecimal monthlyPayment = annualGross.divide(new BigDecimal("12.00"), rm);
        BigDecimal firstPayment = annualGross.subtract(monthlyPayment.multiply(new BigDecimal("11.00")));
        quote.totalAnniversaryFirstMonthPayable().setValue(firstPayment);

        firstPayment = firstPayment.add(underwritingFee);

        quote.annualPremium().setValue(annualPremium);
        quote.underwriterFee().setValue(underwritingFee);
        quote.totalAnnualTax().setValue(totalAnnualTax);
        quote.totalAnnualPayable().setValue(annualPremium.add(underwritingFee).add(totalAnnualTax));
        quote.totalMonthlyPayable().setValue(monthlyPayment);
        quote.totalFirstPayable().setValue(firstPayment);
    }

    @Override
    public TenantSureTransaction initFirstTransaction(TenantSureInsurancePolicy insuranceTenantSure, InsurancePaymentMethod paymentMethod) {
        TenantSureTransaction transaction = EntityFactory.create(TenantSureTransaction.class);
        transaction.insurance().set(insuranceTenantSure);
        transaction.paymentMethod().set(paymentMethod);
        transaction.status().setValue(TenantSureTransaction.TransactionStatus.Draft);
        transaction.amount().setValue(insuranceTenantSure.totalFirstPayable().getValue());
        transaction.paymentDue().setValue(insuranceTenantSure.certificate().inceptionDate().getValue());
        return transaction;
    }

}
