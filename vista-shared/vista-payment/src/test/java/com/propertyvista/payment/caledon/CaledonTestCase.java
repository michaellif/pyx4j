/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2011
 * @author kostya
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.Merchant;

public abstract class CaledonTestCase extends TestCase {

    protected static Merchant testMerchant = createTestCaledonMerchant(TestData.TEST_TERMID);

    protected static Merchant testMerchantError = createTestCaledonMerchant(TestData.TEST_TERMID_ERROR);

    protected static CCInformation createCCInformation(String creditCardNumber, String exp) {

        CCInformation ccInfo = EntityFactory.create(CCInformation.class);

        ccInfo.creditCardNumber().setValue(creditCardNumber);

        try {
            ccInfo.creditCardExpiryDate().setValue(new LogicalDate(new SimpleDateFormat("yyyy-MM").parse(exp)));
        } catch (Throwable e) {
            throw new Error("Invalid data");
        }

        return ccInfo;
    }

    protected static Merchant createTestCaledonMerchant(String terminalID) {
        Merchant m = EntityFactory.create(Merchant.class);

        m.terminalID().setValue(terminalID);
        return m;
    }

}
