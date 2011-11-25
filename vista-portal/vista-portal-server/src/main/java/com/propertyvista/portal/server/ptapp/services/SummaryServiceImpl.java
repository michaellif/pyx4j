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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.ServletMapping;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoSummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.report.SummaryReport;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class SummaryServiceImpl extends ApplicationEntityServiceImpl implements SummaryService {

    private final static Logger log = LoggerFactory.getLogger(SummaryServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<SummaryDTO> callback, Key tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        callback.onSuccess(retrieveSummary());
    }

    @Override
    public void save(AsyncCallback<SummaryDTO> callback, SummaryDTO summaryDTO) {
        Summary summary = EntityFactory.create(Summary.class);
        summary.setValue(summaryDTO.getValue());

        if (!summaryDTO.signed().isBooleanTrue() && summary.application().signature().agree().isBooleanTrue()) {
            //TODO validate signature
            Persistence.service().merge(summary.application().signature());
            Persistence.service().merge(summary.application());
        }

        saveApplicationEntity(summary);

//        createSummaryDTO(summary);
        callback.onSuccess(summaryDTO);
    }

    public SummaryDTO retrieveSummary() {
        Summary summary = retrieveApplicationEntity(Summary.class);
        if (summary == null) {
            log.info("Creating new Summary for appl {}", PtAppContext.getCurrentUserApplicationPrimaryKey());
            summary = EntityFactory.create(Summary.class);
            summary.application().set(PtAppContext.getCurrentUserApplication());
        } else {
            Persistence.service().retrieve(summary.application());
            if (!summary.application().signature().isNull()) {
                Persistence.service().retrieve(summary.application().signature());
            }
        }

        return createSummaryDTO(summary);
    }

    public SummaryDTO createSummaryDTO(Summary dbo) {

        SummaryDTO summary = EntityFactory.create(SummaryDTO.class);
        summary.setValue(dbo.getValue());
        summary.signed().set(dbo.application().signature().agree());

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
            summary.tenantsWithInfo().add(tis.retrieveData(tr));
            summary.tenantFinancials().add(tfs.retrieveData(tr));
        }

        summary.application().signature().timestamp().setValue(new Date());
        summary.application().signature().ipAddress().setValue(Context.getRequestRemoteAddr());

        // TODO This should be taken from building policy
        summary.leaseTerms().set(Persistence.service().retrieve(EntityQueryCriteria.create(LeaseTerms.class)));

        return summary;
    }

    @Override
    public void downloadSummary(AsyncCallback<String> callback, VoidSerializable none) {
        SummaryDTO summary = retrieveSummary();
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
