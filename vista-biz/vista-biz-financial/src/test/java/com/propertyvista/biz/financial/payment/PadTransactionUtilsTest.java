/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import junit.framework.Assert;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.PaymentRecord;

@Category({ RegressionTests.class })
public class PadTransactionUtilsTest extends VistaDBTestBase {

    public void testCaledonTransactionEncodning() {
        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.setPrimaryKey(new Key(100));

        PadDebitRecord debitRecord = EntityFactory.create(PadDebitRecord.class);
        debitRecord.transactionId().setValue(PadTransactionUtils.toCaldeonTransactionId(paymentRecord.id()));

        Key paymentRecordKey = PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId());
        Assert.assertEquals("Record conversion", paymentRecord.getPrimaryKey(), paymentRecordKey);
    }
}
