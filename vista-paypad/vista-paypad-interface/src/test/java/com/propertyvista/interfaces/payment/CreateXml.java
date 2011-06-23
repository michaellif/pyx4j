/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.payment;

public class CreateXml {

    public static void main(String[] args) throws Exception {
        requestExamples();
        responseExamples();
    }

    public static void requestExamples() throws Exception {
        System.out.println("\n\n-- Sale transaction --");
        RequestMessageCreator.makeSaleTransaction();

        System.out.println("\n\n-- Token Add transaction --");
        RequestMessageCreator.makeTokenAddTransction();

        System.out.println("\n\n-- Sale using a token --");
        RequestMessageCreator.makeSaleUsingToken();

        System.out.println("\n\n-- RequestMessage Schema --");
        MarshallUtil.printSchema(RequestMessage.class, System.out, false);
    }

    private static void responseExamples() throws Exception {
        System.out.println("\n\n-- Sale/Token transaction Response(s) --");
        ResponseMessageCreator.makeSaleTransactionResponse();

        System.out.println("\n\n-- System Not Avalable --");
        ResponseMessageCreator.makeSystemDown();

        System.out.println("\n\n-- ResponseMessage Schema --");
        MarshallUtil.printSchema(ResponseMessage.class, System.out, false);
    }
}
