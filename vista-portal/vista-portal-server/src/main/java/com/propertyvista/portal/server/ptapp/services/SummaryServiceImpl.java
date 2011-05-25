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

import com.pyx4j.entity.report.JasperFileFormat;
import com.pyx4j.entity.report.JasperReportProcessor;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.ptapp.ChargeLineSelectable;
import com.propertyvista.portal.domain.ptapp.LeaseTerms;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.ServletMapping;
import com.propertyvista.portal.rpc.ptapp.services.SummaryService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.portal.server.ptapp.util.Converter;
import com.propertyvista.portal.server.report.SummaryReport;

public class SummaryServiceImpl extends ApplicationEntityServiceImpl implements SummaryService {

    private final static Logger log = LoggerFactory.getLogger(SummaryServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<Summary> callback, String tenantId) {
        log.info("Retrieving summary for tenant {}", tenantId);
        callback.onSuccess(retrieveSummary());
    }

    @Override
    public void save(AsyncCallback<Summary> callback, Summary summary) {
        saveApplicationEntity(summary);
        loadTransientData(summary);
        callback.onSuccess(summary);
    }

    public Summary retrieveSummary() {
        EntityQueryCriteria<Summary> criteria = EntityQueryCriteria.create(Summary.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        Summary summary = secureRetrieve(criteria);
        if (summary == null) {
            log.info("Creating new Summary for appl {}", PtAppContext.getCurrentUserApplicationPrimaryKey());
            summary = EntityFactory.create(Summary.class);
            summary.application().set(PtAppContext.getCurrentUserApplication());
        }
        loadTransientData(summary);
        return summary;
    }

    @SuppressWarnings("unchecked")
    public void loadTransientData(Summary summary) {

        // this code starts to become very convoluted and all-over-the place
        retrieveApplicationEntity(summary.unitSelection(), summary.application());
        if (!summary.unitSelection().selectedUnitId().isNull()) {
            summary.selectedUnit().set(
                    Converter.convert(PersistenceServicesFactory.getPersistenceService().retrieve(AptUnit.class,
                            summary.unitSelection().selectedUnitId().getValue())));
        }

        // I have no idea so far for why this line gets called
        //        PersistenceServicesFactory.getPersistenceService().retrieve(summary.unitSelection().selectedUnit().floorplan());

        retrieveApplicationEntity(summary.tenantList(), summary.application());

        // We do not remove the info from DB if Tenant status changes
        summary.tenantsWithInfo().tenants().clear();
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                summary.tenantsWithInfo().tenants().add(tenant);
            }
        }

        EntityQueryCriteria<PotentialTenantFinancial> financialCriteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        financialCriteria.add(PropertyCriterion.eq(financialCriteria.proto().application(), summary.application()));
        summary.tenantFinancials().clear();
        for (PotentialTenantFinancial fin : PersistenceServicesFactory.getPersistenceService().query(financialCriteria)) {
            // Update Transient values and see if we need to show this Tenant
            findTenenat: for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
                if (fin.id().equals(tenant.id())) {
                    if (ApplicationServiceImpl.shouldEnterInformation(tenant)) {
                        SummaryPotentialTenantFinancial sf = summary.tenantFinancials().$();
                        sf.tenantFullName().setValue(
                                EntityFromatUtils.nvl_concat(" ", tenant.name().firstName(), tenant.name().middleName(), tenant.name().lastName()));
                        sf.tenantFinancial().set(fin);
                        summary.tenantFinancials().add(sf);
                    }
                    break findTenenat;
                }
            }
        }

        retrieveApplicationEntity(summary.pets(), summary.application());
        retrieveApplicationEntity(summary.charges(), summary.application());

        // Move selected upgrades for presentation.
        for (ChargeLineSelectable charge : summary.charges().monthlyCharges().upgradeCharges()) {
            if (charge.selected().isBooleanTrue()) {
                summary.charges().monthlyCharges().charges().add(charge);
            }
        }
        summary.charges().monthlyCharges().upgradeCharges().clear();
        loopOverTenantCharge: for (TenantCharge charge : summary.charges().paymentSplitCharges().charges()) {
            for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
                if (tenant.equals(charge.tenant())) {
                    charge.tenantFullName().setValue(
                            EntityFromatUtils.nvl_concat(" ", tenant.name().firstName(), tenant.name().middleName(), tenant.name().lastName()));
                    continue loopOverTenantCharge;
                }
            }
        }

        if (!summary.selectedUnit().newLeaseTerms().id().isNull()) {
            summary.leaseTerms().set(
                    PersistenceServicesFactory.getPersistenceService().retrieve(LeaseTerms.class, summary.selectedUnit().newLeaseTerms().getPrimaryKey()));
        }
    }

    @Override
    public void downloadSummary(AsyncCallback<String> callback, VoidSerializable none) {

        Summary summary = retrieveSummary();
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
