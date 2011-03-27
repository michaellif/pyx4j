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
package com.propertyvista.portal.server.pt.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.LeaseTerms;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.rpc.pt.services.SummaryServices;
import com.propertyvista.portal.server.pt.PtAppContext;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Context;

public class SummaryServicesImpl extends ApplicationEntityServicesImpl implements SummaryServices {

    private final static Logger log = LoggerFactory.getLogger(SummaryServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<Summary> callback, Long tenantId) {
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
            log.info("Creating new Summary");
            summary = EntityFactory.create(Summary.class);
        }
        loadTransientData(summary);
        return summary;
    }

    @SuppressWarnings("unchecked")
    public void loadTransientData(Summary summary) {

        // this code starts to become very convoluted and all-over-the place
        retrieveApplicationEntity(summary.unitSelection(), summary.application());
        ApartmentServicesImpl apartmentServices = new ApartmentServicesImpl();
        apartmentServices.loadTransientData(summary.unitSelection());

        // I have no idea so far for why this line gets called
        //        PersistenceServicesFactory.getPersistenceService().retrieve(summary.unitSelection().selectedUnit().floorplan());

        retrieveApplicationEntity(summary.tenantList(), summary.application());

        // We do not remove the info from DB if Tenant status changes
        for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
            if (ApplicationServicesImpl.shouldEnterInformation(tenant)) {
                summary.tenantsWithInfo().tenants().add(tenant);
            }
        }

        EntityQueryCriteria<PotentialTenantFinancial> financialCriteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        financialCriteria.add(PropertyCriterion.eq(financialCriteria.proto().application(), summary.application()));
        for (PotentialTenantFinancial fin : PersistenceServicesFactory.getPersistenceService().query(financialCriteria)) {
            // Update Transient values and see if we need to show this Tenant
            findTenenat: for (PotentialTenantInfo tenant : summary.tenantList().tenants()) {
                if (fin.id().equals(tenant.id())) {
                    if (ApplicationServicesImpl.shouldEnterInformation(tenant)) {
                        SummaryPotentialTenantFinancial sf = summary.tenantFinancials().$();
                        sf.tenantFullName().setValue(EntityFromatUtils.nvl_concat(" ", tenant.firstName(), tenant.middleName(), tenant.lastName()));
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
                    charge.tenantFullName().setValue(EntityFromatUtils.nvl_concat(" ", tenant.firstName(), tenant.middleName(), tenant.lastName()));
                    continue loopOverTenantCharge;
                }
            }
        }

        summary.leaseTerms().set(
                PersistenceServicesFactory.getPersistenceService().retrieve(LeaseTerms.class,
                        summary.unitSelection().selectedUnit().newLeaseTerms().getPrimaryKey()));
    }

    @Override
    public void downloadSummary(AsyncCallback<String> callback, VoidSerializable none) {

        Summary summary = retrieveSummary();

        try {
            Downloadable d = new Downloadable(IOUtils.getResource("com/propertyvista/portal/server/preloader/apartment1.jpg"),
                    Downloadable.getContentType(DownloadFormat.PDF));
            d.save("da.jpg");
            callback.onSuccess(Context.getRequest().getContextPath() + "/download/" + System.currentTimeMillis() + "/" + "da.jpg");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("Error", e);
        }

    }
}
