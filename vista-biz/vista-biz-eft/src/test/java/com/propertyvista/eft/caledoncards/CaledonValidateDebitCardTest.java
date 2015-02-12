/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 9, 2015
 * @author ernestog
 */
package com.propertyvista.eft.caledoncards;

import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.PaymentResponse;

public class CaledonValidateDebitCardTest extends CaledonTestBase {

    // Create some predefined EXP-DATE / RESPONSE-CODE based on CALEDON API TEST DOCUMENT
    enum ResponseCode {

        SUCCESS("2010-10", "0000"),

        CARD_NO_INVLD("2018-12", "1214"),

        DATE_INVLD("2019-03", "1280"),

        CARD_OK("2018-01", "1285"),

        DECLINE("2019-04", "1205"),

        EXPIRED_CARD("2019-11", "1254");

        private final String returnCode;

        private final String expiryDate;

        private ResponseCode(String date, String code) {
            this.expiryDate = date;
            this.returnCode = code;
        }

        public String getReturnCode() {
            return returnCode;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public boolean equals(String code) {
            return returnCode.equals(code);
        }
    }

    public void testExternalValidation() {
        validateRequest(ResponseCode.SUCCESS);

        validateRequest(ResponseCode.CARD_NO_INVLD);

        validateRequest(ResponseCode.DATE_INVLD);

        validateRequest(ResponseCode.CARD_OK);

        validateRequest(ResponseCode.DECLINE);

        validateRequest(ResponseCode.EXPIRED_CARD);
    }

    static void validateRequest(ResponseCode expectedResponseCode) {
        CreditCardPaymentInstrument ccInfo = createCCInformation(expectedResponseCode.getExpiryDate());
        PaymentResponse response = new CaledonPaymentProcessor().externalVisaDebitValidation(ccInfo, TestData.TEST_TERMID3);
        assertValidationRequestResponse(response, expectedResponseCode);
    }

    private static void assertValidationRequestResponse(PaymentResponse response, ResponseCode expectedResponseCode) {
        assertTrue(createErrorMessage(response, expectedResponseCode), expectedResponseCode.equals(response.code().getValue()));
    }

    private static CreditCardPaymentInstrument createCCInformation(String expirationDate) {
        return createCCInformation(TestData.CARD_VISA_DEBIT, expirationDate, "123");
    }

    private static String createErrorMessage(PaymentResponse response, ResponseCode expectedResponseCode) {
        StringBuffer responseMssg = new StringBuffer("Expected result code is: ");
        responseMssg.append(expectedResponseCode.getReturnCode());
        responseMssg.append(" and result was: ");
        responseMssg.append(response.code().getValue());
        return responseMssg.toString();
    }

}
