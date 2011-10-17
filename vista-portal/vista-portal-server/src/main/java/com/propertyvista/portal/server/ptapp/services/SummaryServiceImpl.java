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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.charges.ChargeLineSelectable;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.ServletMapping;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryDTO;
import com.propertyvista.portal.rpc.ptapp.dto.SummaryTenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
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
            summary.tenantList().tenants().add(tenantInLease);
        }

        // We do not remove the info from DB if Tenant status changes
        for (TenantInLease tenantInLease : lease.tenants()) {
            if (ApplicationProgressMgr.shouldEnterInformation(tenantInLease)) {
                summary.tenantsWithInfo().tenants().add(tenantInLease);
            }
        }

        for (TenantInLease tenantInLease : lease.tenants()) {
            EntityQueryCriteria<TenantScreening> criteria = EntityQueryCriteria.create(TenantScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenantInLease.tenant()));
            TenantScreening tenantScreening = Persistence.service().retrieve(criteria);

            Persistence.service().retrieve(tenantScreening.documents());
            Persistence.service().retrieve(tenantScreening.incomes());
            Persistence.service().retrieve(tenantScreening.assets());
            Persistence.service().retrieve(tenantScreening.guarantors());

            // Update Transient values and see if we need to show this Tenant
            if (ApplicationProgressMgr.shouldEnterInformation(tenantInLease)) {
                SummaryTenantFinancialDTO sf = EntityFactory.create(SummaryTenantFinancialDTO.class);
                sf.tenantFullName().setValue(
                        EntityFromatUtils.nvl_concat(" ", tenantInLease.tenant().person().name().firstName(), tenantInLease.tenant().person().name()
                                .middleName(), tenantInLease.tenant().person().name().lastName()));

                TenantFinancialDTO tf = EntityFactory.create(TenantFinancialDTO.class);
                tf.incomes().addAll(tenantScreening.incomes());
                tf.incomes2().addAll(tenantScreening.incomes2());
                tf.assets().addAll(tenantScreening.assets());
                tf.guarantors().addAll(tenantScreening.guarantors());

                sf.tenantFinancial().set(tf);

                summary.tenantFinancials().add(sf);
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
            for (TenantInLease tenant : summary.tenantList().tenants()) {
                if (tenant.equals(charge.tenant())) {
                    charge.tenantFullName().setValue(
                            EntityFromatUtils.nvl_concat(" ", tenant.tenant().person().name().firstName(), tenant.tenant().person().name().middleName(), tenant
                                    .tenant().person().name().lastName()));
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
}
