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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;

import com.propertyvista.dto.LeaseAgreementDocumentDataDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;

public class LeaseTermAgreementPdfCreatorFacadeImpl implements LeaseTermAgreementPdfCreatorFacade {

    @Override
    public byte[] createPdf(LeaseAgreementDocumentDataDTO agreementData, boolean createDraft) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("landlordName", agreementData.landlordName().getValue());
        params.put("landlordAddress", agreementData.landlordAddress().getValue());
        if (agreementData.landlordLogo().getValue() != null) {
            params.put("landlordLogo", new ByteArrayInputStream(agreementData.landlordLogo().getValue()));
        }
        if (createDraft) {
            InputStream watermakStream = LeaseTermAgreementDocumentDataCreatorFacadeImpl.class.getResourceAsStream("draft-watermark.png");
            byte[] watermarkBytes;
            try {
                watermarkBytes = IOUtils.toByteArray(watermakStream);
                params.put("backgroundImage", new ByteArrayInputStream(watermarkBytes));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                com.pyx4j.gwt.server.IOUtils.closeQuietly(watermakStream);
            }

        }

        params.put("applicants", agreementData.applicants());

        ByteOutputStream bos = new ByteOutputStream();
        JasperReportProcessor.createReport(new JasperReportModel(LeaseTermAgreementPdfCreatorFacadeImpl.class.getPackage().getName() + ".LeaseTermAgreement",
                new LinkedList<LeaseAgreementDocumentLegalTerm4PrintDTO>(agreementData.terms()), params), JasperFileFormat.PDF, bos);
        return bos.getBytes();
    }
}
