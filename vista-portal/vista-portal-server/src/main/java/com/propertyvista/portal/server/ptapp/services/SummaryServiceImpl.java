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
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.charges.ChargeLineSelectable;
import com.propertyvista.misc.ServletMapping;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryTenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.report.SummaryReport;

public class SummaryServiceImpl extends ApplicationEntityServiceImpl implements SummaryService {

    private final static Logger log = LoggerFactory.getLogger(SummaryServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<SummaryDTO> callback, Key tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        callback.onSuccess(retrieveSummary());
    }

    @Override
    public void save(AsyncCallback<SummaryDTO> callback, SummaryDTO summary) {
        saveApplicationEntity(summary);
        loadTransientData(summary);
        callback.onSuccess(summary);
    }

    public SummaryDTO retrieveSummary() {
        SummaryDTO summary = retrieveApplicationEntity(SummaryDTO.class);
        if (summary == null) {
            log.info("Creating new Summary for appl {}", PtAppContext.getCurrentUserApplicationPrimaryKey());
            summary = EntityFactory.create(SummaryDTO.class);
            summary.application().set(PtAppContext.getCurrentUserApplication());
        }
        loadTransientData(summary);
        return summary;
    }

    @SuppressWarnings("unchecked")
    public void loadTransientData(SummaryDTO summary) {

        retrieveApplicationEntity(summary.tenantList(), summary.application());

        // We do not remove the info from DB if Tenant status changes
        summary.tenantsWithInfo().tenants().clear();
        for (TenantInLeaseDTO tenant : summary.tenantList().tenants()) {
            if (ApplicationProgressMgr.shouldEnterInformation(tenant)) {
                summary.tenantsWithInfo().tenants().add(tenant);
            }
        }

        EntityQueryCriteria<TenantFinancialDTO> financialCriteria = EntityQueryCriteria.create(TenantFinancialDTO.class);
        //TODO financialCriteria.add(PropertyCriterion.eq(financialCriteria.proto().application(), summary.application()));
        summary.tenantFinancials().clear();
        for (TenantFinancialDTO fin : PersistenceServicesFactory.getPersistenceService().query(financialCriteria)) {
            // Update Transient values and see if we need to show this Tenant
            findTenenat: for (TenantInLeaseDTO tenant : summary.tenantList().tenants()) {
                if (fin.id().equals(tenant.id())) {
                    if (ApplicationProgressMgr.shouldEnterInformation(tenant)) {
                        SummaryTenantFinancialDTO sf = summary.tenantFinancials().$();
                        sf.tenantFullName().setValue(
                                EntityFromatUtils.nvl_concat(" ", tenant.person().name().firstName(), tenant.person().name().middleName(), tenant.person()
                                        .name().lastName()));
                        sf.tenantFinancial().set(fin);
                        summary.tenantFinancials().add(sf);
                    }
                    break findTenenat;
                }
            }
        }

// TODO here should be retrived from Lease:        
//        retrieveApplicationEntity(summary.addons(), summary.application());
        retrieveApplicationEntity(summary.charges(), summary.application());

        // Move selected upgrades for presentation.
        for (ChargeLineSelectable charge : summary.charges().monthlyCharges().upgradeCharges()) {
            if (charge.selected().isBooleanTrue()) {
                summary.charges().monthlyCharges().charges().add(charge);
            }
        }
        summary.charges().monthlyCharges().upgradeCharges().clear();
        loopOverTenantCharge: for (TenantCharge charge : summary.charges().paymentSplitCharges().charges()) {
            for (TenantInLeaseDTO tenant : summary.tenantList().tenants()) {
                if (tenant.equals(charge.tenant())) {
                    charge.tenantFullName().setValue(
                            EntityFromatUtils.nvl_concat(" ", tenant.person().name().firstName(), tenant.person().name().middleName(), tenant.person().name()
                                    .lastName()));
                    continue loopOverTenantCharge;
                }
            }
        }
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
}
