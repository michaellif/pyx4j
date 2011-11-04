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

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.ServletMapping;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.util.TenantConverter;
import com.propertyvista.portal.server.ptapp.util.TenantRetriever;
import com.propertyvista.portal.server.report.SummaryReport;

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
        saveApplicationEntity(summary);
        createSummaryDTO(summary);
        callback.onSuccess(summaryDTO);
    }

    public SummaryDTO retrieveSummary() {
        Summary summary = retrieveApplicationEntity(Summary.class);
        if (summary == null) {
            log.info("Creating new Summary for appl {}", PtAppContext.getCurrentUserApplicationPrimaryKey());
            summary = EntityFactory.create(Summary.class);
            summary.application().set(PtAppContext.getCurrentUserApplication());
        }

        return createSummaryDTO(summary);
    }

    @SuppressWarnings("unchecked")
    public SummaryDTO createSummaryDTO(Summary dbo) {

        SummaryDTO summary = EntityFactory.create(SummaryDTO.class);
        summary.setValue(dbo.getValue());

        summary.selectedUnit().set(new ApartmentServiceImpl().retrieveData());

        Lease lease = PtAppContext.getCurrentUserLease();
        TenantRetriever.UpdateLeaseTenants(lease);

        for (TenantInLease tenantInLease : lease.tenants()) {
            Persistence.service().retrieve(tenantInLease);

            TenantRetriever tr = new TenantRetriever(tenantInLease.getPrimaryKey(), true);

            summary.tenantList().tenants().add(new TenantConverter.TenantEditorConverter().createDTO(tenantInLease));
            summary.tenantsWithInfo().add(createTenantInfoDTO(tr));
            summary.tenantFinancials().add(createTenantFinancialDTO(tr));
        }

        retrieveApplicationEntity(summary.charges(), summary.application());
        loopOverTenantCharge: for (TenantCharge charge : summary.charges().paymentSplitCharges().charges()) {
            for (TenantInApplicationDTO tenant : summary.tenantList().tenants()) {
                if (tenant.equals(charge.tenant())) {
                    charge.tenantName().set(tenant.person().name());
                    continue loopOverTenantCharge;
                }
            }
        }

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

    // internal helpers:

    private TenantInfoDTO createTenantInfoDTO(TenantRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.tenant);
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        tfDTO.person().set(tr.tenant.person());
        return tfDTO;
    }
}
