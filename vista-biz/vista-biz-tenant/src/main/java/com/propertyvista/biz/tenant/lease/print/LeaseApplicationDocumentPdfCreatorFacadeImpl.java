/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-18
 * @author ArtyomB
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

import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataDTO;
import com.propertyvista.dto.leaseapplicationdocument.LeaseApplicationDocumentDataSectionsDTO;

public class LeaseApplicationDocumentPdfCreatorFacadeImpl implements LeaseApplicationDocumentPdfCreatorFacade {

    @Override
    public byte[] createPdf(LeaseApplicationDocumentDataDTO data) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("landlordName", data.landlordName().getValue());
        params.put("landlordAddress", data.landlordAddress().getValue());
        params.put("submissionDate", data.submissionDate().getValue());
        params.put("applicationId", data.applicationId().getValue());

        if (data.landlordLogo().getValue() != null) {
            params.put("landlordLogo", new ByteArrayInputStream(data.landlordLogo().getValue()));
        }
        if (data.background().getValue() != null) {
            params.put("backgroundImage", new ByteArrayInputStream(data.background().getValue()));
        }
        params.put("applicants", data.applicants());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JasperReportProcessor.createReport(new JasperReportModel(LeaseTermAgreementPdfCreatorFacadeImpl.class.getPackage().getName() + ".RentalApplication",
                new LinkedList<LeaseApplicationDocumentDataSectionsDTO>(data.sections()), params), JasperFileFormat.PDF, bos);

        return bos.toByteArray();
    }
}
