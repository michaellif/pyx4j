/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.system.PmcFacade;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.pmc.CreditCheckReportType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.pmc.info.PmcBusinessInfoDocument;
import com.propertyvista.domain.pmc.info.PmcBusinessInfoDocument.Type;
import com.propertyvista.domain.pmc.info.PmcDocumentFile;
import com.propertyvista.server.config.DevYardiCredentials;
import com.propertyvista.server.domain.PmcDocumentBlob;
import com.propertyvista.shared.config.VistaDemo;

public class PmcCreatorDev {

    public static Pmc createPmc(String pmcName, boolean mini) {
        Pmc pmc = EntityFactory.create(Pmc.class);
        pmc.name().setValue(pmcName + " Demo");
        pmc.dnsName().setValue(pmcName);
        pmc.namespace().setValue(pmcName.replace('-', '_'));

        pmc.features().occupancyModel().setValue(Boolean.TRUE);
        pmc.features().productCatalog().setValue(Boolean.TRUE);
        pmc.features().leases().setValue(Boolean.TRUE);
        pmc.features().onlineApplication().setValue(Boolean.TRUE);
        pmc.features().defaultProductCatalog().setValue(true);
        pmc.features().yardiIntegration().setValue(Boolean.FALSE);
        pmc.features().tenantSureIntegration().setValue(Boolean.TRUE);

        if (pmcName.equals(DemoPmc.gondor.name())) {
            pmc.features().countryOfOperation().setValue(CountryOfOperation.UK);
        } else {
            pmc.features().countryOfOperation().setValue(CountryOfOperation.Canada);
        }
        if (!mini && pmcName.equals(DemoPmc.star.name())) {
            pmc.features().occupancyModel().setValue(Boolean.FALSE);
            pmc.features().yardiIntegration().setValue(Boolean.TRUE);
            pmc.features().defaultProductCatalog().setValue(Boolean.TRUE);
        }

        pmc.equifaxInfo().status().setValue(PmcEquifaxStatus.Active);
        pmc.equifaxInfo().reportType().setValue(CreditCheckReportType.FullCreditReport);

        if (VistaDemo.isDemo()) {
            pmc.equifaxFee().recommendationReportPerApplicantFee().setValue(BigDecimal.ZERO);
            pmc.equifaxFee().recommendationReportSetUpFee().setValue(BigDecimal.ZERO);
            pmc.equifaxFee().fullCreditReportPerApplicantFee().setValue(BigDecimal.ZERO);
            pmc.equifaxFee().fullCreditReportSetUpFee().setValue(BigDecimal.ZERO);
        }

        PmcBusinessInfoDocument doc = pmc.equifaxInfo().businessInformation().documents().$();
        doc.type().setValue(Type.BusinessLicense);
        PmcDocumentFile docPage = doc.documentPages().$();
        PmcDocumentBlob docPageBlob = EntityFactory.create(PmcDocumentBlob.class);
        docPageBlob.contentType().setValue("text/plain");
        String licenseText = "This mockup business is licensed to operate in Property Vista development environment (Issued by Property Vista Dev Team)\n";
        docPageBlob.data().setValue(licenseText.getBytes());
        Persistence.service().persist(docPageBlob);
        docPage.blobKey().setValue(docPageBlob.getPrimaryKey());
        docPage.fileSize().setValue(licenseText.getBytes().length);
        docPage.fileName().setValue("dev-business-license.txt");
        docPage.contentMimeType().setValue(docPageBlob.contentType().getValue());
        doc.documentPages().add(docPage);
        pmc.equifaxInfo().businessInformation().documents().add(doc);

        pmc.yardiCredentials().add(DevYardiCredentials.getTestPmcYardiCredential());

        ServerSideFactory.create(PmcFacade.class).create(pmc);

        pmc.status().setValue(PmcStatus.Active);
        Persistence.service().persist(pmc);

        return pmc;
    }

}
