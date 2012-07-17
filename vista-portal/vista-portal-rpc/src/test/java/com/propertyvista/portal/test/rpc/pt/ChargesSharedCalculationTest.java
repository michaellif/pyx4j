/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.test.rpc.pt;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

public class ChargesSharedCalculationTest extends TestCase {

    public void testSplitChargesRunding() {
        Charges charges = EntityFactory.create(Charges.class);
        charges.monthlyCharges().total().setValue(new BigDecimal("10.6"));

        charges.paymentSplitCharges().charges().add(DomainUtil.createTenantCharge(Tenant.Role.Applicant, BigDecimal.ZERO, BigDecimal.ZERO));
        charges.paymentSplitCharges().charges().add(DomainUtil.createTenantCharge(Tenant.Role.CoApplicant, new BigDecimal(0.31), BigDecimal.ZERO));
        charges.paymentSplitCharges().charges().add(DomainUtil.createTenantCharge(Tenant.Role.CoApplicant, new BigDecimal(0.25), BigDecimal.ZERO));

        ChargesSharedCalculation.calculatePaymentSplitCharges(charges);

        // process result:
        TenantCharge mainApplicantCharge = null;
        BigDecimal totalSplitPrc = BigDecimal.ZERO; // sum %, paid by co-applicants
        BigDecimal totalSplitVal = BigDecimal.ZERO; // sum $, paid by co-applicants

        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            switch (charge.tenant().role().getValue()) {
            case Applicant:
                mainApplicantCharge = charge;
                break;
            case CoApplicant:
                totalSplitPrc = totalSplitPrc.add(charge.tenant().percentage().getValue());
                totalSplitVal = totalSplitVal.add(charge.amount().getValue());
                break;
            default:
                break;
            }
        }

        Assert.assertTrue("prc", new BigDecimal(1).compareTo(mainApplicantCharge.tenant().percentage().getValue().add(totalSplitPrc)) == 0);
        Assert.assertTrue("val", charges.monthlyCharges().total().getValue().compareTo(mainApplicantCharge.amount().getValue().add(totalSplitVal)) == 0);
    }
}
