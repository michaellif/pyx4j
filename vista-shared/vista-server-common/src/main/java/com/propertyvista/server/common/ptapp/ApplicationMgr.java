/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 9, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.ptapp;

import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Status;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class ApplicationMgr {

    private static ApplicationWizardStep createWizardStep(Class<? extends AppPlace> place, ApplicationWizardStep.Status status) {
        ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
        ws.placeId().setValue(AppPlaceInfo.getPlaceId(place));
        ws.status().setValue(status);
        return ws;
    }

    public static List<ApplicationWizardStep> createApplicationProgress() {
        List<ApplicationWizardStep> progress = new Vector<ApplicationWizardStep>();
        progress.add(createWizardStep(PtSiteMap.Apartment.class, ApplicationWizardStep.Status.latest));
        progress.add(createWizardStep(PtSiteMap.Tenants.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Info.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Financial.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Charges.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Summary.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
        return progress;
    }

    public static MasterApplication createMasterApplication(Lease lease) {
        lease.status().setValue(Lease.Status.ApplicationInProgress);
        MasterApplication ma = EntityFactory.create(MasterApplication.class);
        ma.lease().set(lease);
        ma.status().setValue(Status.Invited);
        Persistence.service().retrieve(lease.tenants());
        for (TenantInLease tenantInLease : lease.tenants()) {
            if (TenantInLease.Role.Applicant == tenantInLease.role().getValue()) {
                Application a = EntityFactory.create(Application.class);
                a.belongsTo().set(ma);
                a.status().setValue(Status.Invited);
                a.steps().addAll(ApplicationMgr.createApplicationProgress());
                a.user().set(tenantInLease.tenant().user());
                a.lease().set(ma.lease());
                ma.applications().add(a);
                break;
            }
        }

        ma.createDate().setValue(new LogicalDate());
        return ma;
    }
}
