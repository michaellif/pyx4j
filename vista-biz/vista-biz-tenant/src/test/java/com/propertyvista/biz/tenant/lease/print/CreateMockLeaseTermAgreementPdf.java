/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.lease.print;

import java.io.FileOutputStream;
import java.util.LinkedList;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.policy.policies.domain.AgreementLegalTerm;

public class CreateMockLeaseTermAgreementPdf {

    private static int currentTermNo = 0;

    public static void main(String[] args) {
        LinkedList<AgreementLegalTerm> terms = new LinkedList<AgreementLegalTerm>();
        terms.add(makeMockAgreementTerm(5));
        terms.add(makeMockAgreementTerm(15));
        terms.add(makeMockAgreementTerm(10));

        byte[] bytes = LeaseTermAgreementPdfCreator.createPdf(terms);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("mock-lease-term-agreement.pdf");
            fos.write(bytes);
            fos.close();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
        System.out.println("DONE!");
    }

    private static AgreementLegalTerm makeMockAgreementTerm(int randomLines) {
        currentTermNo += 1;
        AgreementLegalTerm term = EntityFactory.create(AgreementLegalTerm.class);
        term.orderId().setValue(currentTermNo);
        term.title().setValue("Term number + " + currentTermNo);
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder
                .append("<div style=\"text-align: center;\">hello world</div><div style=\"text-align: right;\">some text here <span style=\"font-weight: bold; font-style: italic; color: red;\">bold italic red text here</span><br>another line of text</div>");
        for (int i = 0; i < randomLines; ++i) {
            bodyBuilder.append("<br>afsdfasdflsdh aslfkjdshf aslfkjsdaf ;slfkj sdfa;lsdfkjsd f;asdlfkjsdf");
        }
        term.body().setValue(bodyBuilder.toString());
        term.signatureFormat().setValue(SignatureFormat.AgreeBox);
        return term;
    }
}
