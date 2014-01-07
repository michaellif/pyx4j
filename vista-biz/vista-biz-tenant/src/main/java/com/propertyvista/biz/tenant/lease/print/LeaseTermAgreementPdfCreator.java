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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.JasperReportProcessor;

public class LeaseTermAgreementPdfCreator {

    public static byte[] createPdf(List<AgreementLegalTerm4Print> agreementTerms, byte[] logo) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("landlordName", "SuperLandlord");
        params.put("landlordAddress", "5935 Airport Road Suite 600, Mississauga, ON. L4V 1W5");
        if (logo != null) {
            params.put("landlordLogo", new ByteInputStream(logo, logo.length));
        }

        ByteOutputStream bos = new ByteOutputStream();
        JasperReportProcessor.createReport(new JasperReportModel(LeaseTermAgreementPdfCreator.class.getPackage().getName() + ".LeaseTermAgreement",
                agreementTerms, params), JasperFileFormat.PDF, bos);
        return bos.getBytes();
    }
}
