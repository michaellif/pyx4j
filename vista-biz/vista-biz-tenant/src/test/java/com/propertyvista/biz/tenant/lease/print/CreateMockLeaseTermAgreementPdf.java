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
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.dto.LeaseAgreementDocumentDataDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;

public class CreateMockLeaseTermAgreementPdf {

    private static int currentTermNo = 0;

    public static void main(String[] args) throws IOException {
        LeaseAgreementDocumentDataDTO leaseAgreementData = EntityFactory.create(LeaseAgreementDocumentDataDTO.class);
        leaseAgreementData.landlordName().setValue("SuperLandlord");
        leaseAgreementData.landlordAddress().setValue("5935 Airport Road Suite 600, Mississauga, ON. L4V 1W5");
        leaseAgreementData.landlordLogo().setValue(
                org.apache.poi.util.IOUtils.toByteArray(CreateMockLeaseTermAgreementPdf.class.getResourceAsStream("logo.png")));

        leaseAgreementData.applicants().add(makeMockApplicant("Vasya Petechkin"));
        leaseAgreementData.applicants().add(makeMockApplicant("Petya Vasechkin"));

        leaseAgreementData.terms().add(makeMockAgreementTerm(5, makeMockSignatures(SignatureFormat.FullName, 2), makeMockSignaturePlaceholdedrs(2)));
        leaseAgreementData.terms().add(makeMockAgreementTerm(15, makeMockSignatures(SignatureFormat.Initials, 2), makeMockSignaturePlaceholdedrs(4)));
        leaseAgreementData.terms().add(makeMockAgreementTerm(10, makeMockSignatures(SignatureFormat.AgreeBoxAndFullName, 2), null));
        leaseAgreementData.terms().add(makeMockAgreementTerm(10, null, makeMockSignaturePlaceholdedrs(3)));

        byte[] bytes = ServerSideFactory.create(LeaseTermAgreementPdfCreatorFacade.class).createPdf(leaseAgreementData, true);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("mock-lease-term-agreement.pdf");
            fos.write(bytes);
            fos.close();
            System.out.println("DONE!");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private static LeaseAgreementDocumentLegalTermTenantDTO makeMockApplicant(String fullName) {
        LeaseAgreementDocumentLegalTermTenantDTO tenant = EntityFactory.create(LeaseAgreementDocumentLegalTermTenantDTO.class);
        tenant.fullName().setValue(fullName);
        return tenant;
    }

    private static LeaseAgreementDocumentLegalTerm4PrintDTO makeMockAgreementTerm(int randomLines, List<ISignature> signatures,
            List<LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO> signaturePlaceholders) {
        currentTermNo += 1;
        LeaseAgreementDocumentLegalTerm4PrintDTO term = EntityFactory.create(LeaseAgreementDocumentLegalTerm4PrintDTO.class);
        term.title().setValue("Term number + " + currentTermNo);
        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder
                .append("<div style=\"text-align: center;\">hello world</div><div style=\"text-align: right;\">some text here <span style=\"font-weight: bold; font-style: italic; color: red;\">bold italic red text here</span><br>another line of text</div>");
        for (int i = 0; i < randomLines; ++i) {
            bodyBuilder.append("<br>afsdfasdflsdh aslfkjdshf aslfkjsdaf ;slfkj sdfa;lsdfkjsd f;asdlfkjsdf");
        }
        term.body().setValue(bodyBuilder.toString());
        if (signatures != null) {
            term.signatures().addAll(signatures);
        }
        if (signaturePlaceholders != null) {
            term.signaturePlaceholders().addAll(signaturePlaceholders);
        }
        return term;
    }

    private static List<ISignature> makeMockSignatures(SignatureFormat format, int singaturesCount) {
        List<ISignature> signatures = new LinkedList<ISignature>();
        for (int i = 0; i < singaturesCount; ++i) {
            ISignature signature = EntityFactory.create(ISignature.class);
            signature.signatureFormat().setValue(format);
            signature.signDate().setValue(new Date());
            signature.ipAddress().setValue("192.168.0.100");
            signature.fullName().setValue("Tenant Tenantovic " + i);
            signature.initials().setValue("T T");
            signature.agree().setValue(true);
            signatures.add(signature);
        }
        return signatures;
    }

    private static List<LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO> makeMockSignaturePlaceholdedrs(int singaturesCount) {
        List<LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO> placeholders = new LinkedList<LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO>();
        for (int i = 0; i < singaturesCount; ++i) {
            LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO placeholder = EntityFactory
                    .create(LeaseAgreementDocumentLegalTermSignaturePlaceholderDTO.class);
            placeholder.tenantName().setValue("Vasya Pupkin #" + i);
            placeholders.add(placeholder);
        }
        return placeholders;
    }
}
