/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.paypad.interfaces;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;

import com.propertyvista.interfaces.payment.CreditCardInfo;
import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.TransactionRequest;

public class ValidationsTest {

    private static <T> void assertViolations(int violationsSize, T r) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(r);
        Assert.assertEquals(violationsSize, constraintViolations.size());
    }

    @Test
    public void testRequestMessage() throws JAXBException {
        RequestMessage r = new RequestMessage();
        r.setInterfaceEntity("PaymentProcessor1");
        //r.setInterfaceEntityPassword(password); missing
        r.setMerchantId("123456789"); //too long
        assertViolations(2, r);

        r.setMerchantPassword("12345678901234567"); // too long
        assertViolations(3, r);

        r.setInterfaceEntityPassword("x");
        r.setMerchantId("12345678");
        r.setMerchantPassword(null);
        assertViolations(0, r);
        // Test Pattern
        r.setMerchantId("1234 678");
        assertViolations(1, r);
    }

    @Test
    public void testTransactionRequest() throws JAXBException {
        TransactionRequest tr = new TransactionRequest();
        assertViolations(3, tr);

        tr.setTxnType(TransactionRequest.TransactionType.Sale);
        tr.setReference("12345678");
        CreditCardInfo cc = new CreditCardInfo();
        tr.setPaymentInstrument(cc);
        assertViolations(0, tr); // CC files are invalid but beans a are validated individually

        tr.setEcho("ee ff");
        assertViolations(1, tr);

        tr.setEcho("ee-ff");
        assertViolations(0, tr);

        tr.setReference("Bob,Doe");
        assertViolations(1, tr);

        tr.setReference("Bob/Doe");
        assertViolations(0, tr);

        cc.setCardNumber("6011111111111117");

        assertViolations(1, cc); // Date is missing

        cc.setExpiryDate(new Date());
        assertViolations(0, cc);

        // Test Pattern
        cc.setCardNumber("AMEX1234");
        assertViolations(1, cc);
    }
}
