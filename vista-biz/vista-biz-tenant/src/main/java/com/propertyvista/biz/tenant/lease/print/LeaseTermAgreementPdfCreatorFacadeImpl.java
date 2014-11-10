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
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;

import com.propertyvista.dto.LeaseAgreementDocumentDataDTO;
import com.propertyvista.dto.LeaseAgreementDocumentLegalTerm4PrintDTO;

public class LeaseTermAgreementPdfCreatorFacadeImpl implements LeaseTermAgreementPdfCreatorFacade {

    @Override
    public byte[] createPdf(LeaseAgreementDocumentDataDTO agreementData) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("landlordName", agreementData.landlordName().getValue());
        params.put("landlordAddress", agreementData.landlordAddress().getValue());
        if (agreementData.landlordLogo().getValue() != null) {
            params.put("landlordLogo", new ByteArrayInputStream(agreementData.landlordLogo().getValue()));
        }
        if (agreementData.leaseAgreementBackground().getValue() != null) {
            params.put("backgroundImage", new ByteArrayInputStream(agreementData.leaseAgreementBackground().getValue()));
        }

        params.put("applicants", agreementData.applicants());
        params.put("landlordAgentsSignatures", agreementData.landlordAgentsSignatures());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JasperReportProcessor.createReport(new JasperReportModel(LeaseTermAgreementPdfCreatorFacadeImpl.class.getPackage().getName() + ".LeaseTermAgreement",
                new LinkedList<LeaseAgreementDocumentLegalTerm4PrintDTO>(agreementData.terms()), params), JasperFileFormat.PDF, bos);
        return bos.toByteArray();
    }
}
