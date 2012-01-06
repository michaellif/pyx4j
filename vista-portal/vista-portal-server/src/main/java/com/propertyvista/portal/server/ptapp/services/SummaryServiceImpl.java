/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.misc.ServletMapping;
import com.propertyvista.portal.domain.ptapp.IAgree;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoSummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.rpc.ptapp.validators.DigitalSignatureValidation;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.services.util.ApplicationProgressMgr;
import com.propertyvista.portal.server.ptapp.services.util.LegalStuffUtils;
import com.propertyvista.portal.server.report.SummaryReport;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class SummaryServiceImpl extends ApplicationEntityServiceImpl implements SummaryService {

    private final static Logger log = LoggerFactory.getLogger(SummaryServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<SummaryDTO> callback, Key tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<SummaryDTO> callback, SummaryDTO entity) {
        Summary summary = EntityFactory.create(Summary.class);
        summary.setValue(entity.getValue());

        for (DigitalSignature sig : summary.application().signatures()) {
            if (!DigitalSignatureValidation.isSignatureValid(sig.tenant().tenant(), sig.fullName().getValue())) {
                // reset all if mismatch:
                sig.fullName().setValue(null);
            }
            Persistence.service().merge(sig);
        }

        saveApplicationEntity(summary);

        // we do not use return value, so return the same as input one:        
        callback.onSuccess(entity);
        // but, strictly speaking, this call should look like:        
//        callback.onSuccess(retrieveData());
    }

    public SummaryDTO retrieveData() {
        Summary summary = retrieveApplicationEntity(Summary.class);
        if (summary == null) {
            log.info("Creating new Summary for appl {}", PtAppContext.getCurrentUserApplicationPrimaryKey());
            summary = EntityFactory.create(Summary.class);
            summary.application().set(PtAppContext.getCurrentUserApplication());
        } else {
            Persistence.service().retrieve(summary.application());
        }

        return createSummaryDTO(summary);
    }

    public SummaryDTO createSummaryDTO(Summary dbo) {

        SummaryDTO summary = EntityFactory.create(SummaryDTO.class);
        summary.setValue(dbo.getValue());

        summary.selectedUnit().set(new ApartmentServiceImpl().retrieveData());
        summary.apartmentSummary().add(createApartmentSummary(summary.selectedUnit()));
        summary.charges().set(new ChargesServiceImpl().retrieveData());

        TenantInfoServiceImpl tis = new TenantInfoServiceImpl();
        TenantFinancialServiceImpl tfs = new TenantFinancialServiceImpl();

        Lease lease = PtAppContext.getCurrentUserLease();
        TenantInLeaseRetriever.UpdateLeaseTenants(lease);
        for (TenantInLease tenantInLease : lease.tenants()) {
            Persistence.service().retrieve(tenantInLease);
            TenantInLeaseRetriever tr = new TenantInLeaseRetriever(tenantInLease.getPrimaryKey(), true);

            summary.tenantList().tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantInLease));
            if (ApplicationProgressMgr.shouldEnterInformation(tenantInLease)) {
                summary.tenantsWithInfo().add(tis.retrieveData(tr));
                summary.tenantFinancials().add(tfs.retrieveData(tr));
            }
        }

        // Legal stuff:

        boolean allSigned = true;
        List<IAgree> agrees = new ArrayList<IAgree>();
        for (DigitalSignature sig : summary.application().signatures()) {
            allSigned = (allSigned && !sig.fullName().isNull());

            // add I Agree option:
            IAgree agree = EntityFactory.create(IAgree.class);
            agree.person().set(sig.tenant().tenant().person());
            agree.agree().setValue(Boolean.TRUE);
            agrees.add(agree);

            if (sig.fullName().isNull()) { // update time stamp and IP for non-signed signatures:
                sig.timestamp().setValue(new Date());
                sig.ipAddress().setValue(Context.getRequestRemoteAddr());

                agree.agree().setValue(Boolean.FALSE);
            }
        }

        // fill Lease Terms:
        LegalTermsPolicy termsPolicy = LegalStuffUtils.retrieveLegalTermsPolicy();

        for (LegalTermsDescriptor terms : termsPolicy.summaryTerms()) {
            LegalTermsDescriptorDTO ltd = LegalStuffUtils.formLegalTerms(terms);
            for (IAgree agree : agrees) {
                ltd.agrees().add((IAgree) agree.duplicate());
            }

            summary.leaseTerms().add(ltd);
        }

        summary.signed().setValue(allSigned);

        return summary;
    }

    @Override
    public void downloadSummary(AsyncCallback<String> callback, VoidSerializable none) {
        SummaryDTO summary = retrieveData();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            JasperReportProcessor.createReport(SummaryReport.createModel(summary), JasperFileFormat.PDF, bos);
            Downloadable d = new Downloadable(bos.toByteArray(), Downloadable.getContentType(DownloadFormat.PDF));
            String fileName = "ApplicationSummary.pdf";
            d.save(fileName);
            callback.onSuccess(ServletMapping.REPORTS_DOWNLOAD + "/" + System.currentTimeMillis() + "/" + fileName);
        } finally {
            IOUtils.closeQuietly(bos);
        }
    }

    private static ApartmentInfoSummaryDTO createApartmentSummary(ApartmentInfoDTO selectedUnit) {
        ApartmentInfoSummaryDTO summary = EntityFactory.create(ApartmentInfoSummaryDTO.class);
        summary.floorplan().set(selectedUnit.floorplan());
        summary.address().setValue(selectedUnit.address().street2().getValue());
        summary.bedrooms().setValue(selectedUnit.bedrooms().getValue());
        summary.dens().setValue(selectedUnit.dens().getValue());
        summary.landlordName().setValue(selectedUnit.landlordName().getValue());
        return summary;
    }
}
