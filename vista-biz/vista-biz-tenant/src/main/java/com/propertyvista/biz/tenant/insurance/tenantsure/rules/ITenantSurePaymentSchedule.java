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

import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSure;
import com.propertyvista.domain.tenant.insurance.InsuranceTenantSureTransaction;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureQuoteDTO;

public interface ITenantSurePaymentSchedule {

    void prepareQuote(TenantSureQuoteDTO quote, BigDecimal annualPremium, BigDecimal underwritingFee, BigDecimal totalAnnualTax);

    InsuranceTenantSureTransaction initFirstTransaction(InsuranceTenantSure insuranceTenantSure, InsurancePaymentMethod paymentMethod);

}
