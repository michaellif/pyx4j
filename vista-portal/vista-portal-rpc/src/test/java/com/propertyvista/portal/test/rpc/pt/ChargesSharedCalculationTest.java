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

import junit.framework.Assert;
import junit.framework.TestCase;

import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.shared.EntityFactory;

public class ChargesSharedCalculationTest extends TestCase {

    public void testSplitChargesRunding() {
        Charges charges = EntityFactory.create(Charges.class);

        charges.monthlyCharges().total().amount().setValue(10.6d);

        charges.paymentSplitCharges().charges().add(DomainUtil.createTenantCharge(0, 0));
        int splitPrc = 31;
        charges.paymentSplitCharges().charges().add(DomainUtil.createTenantCharge(splitPrc, 0));
        charges.paymentSplitCharges().charges().add(DomainUtil.createTenantCharge(splitPrc, 0));

        ChargesSharedCalculation.calculatePaymentSplitCharges(charges);

        Assert.assertEquals("prc", 100 - 2 * splitPrc, charges.paymentSplitCharges().charges().get(0).percentage().getValue().intValue());
    }

}
