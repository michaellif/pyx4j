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
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.TenantUserCredential;

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
        progress.add(createWizardStep(PtSiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
        return progress;
    }

    static TenantUser ensureTenantUser(Tenant tenant, VistaTenantBehavior behavior) {
        TenantUser user = tenant.user();
        if (user.getPrimaryKey() == null) {
            user.name().setValue(tenant.person().name().getStringView());
            user.email().setValue(tenant.person().email().getValue());
            Persistence.service().persist(user);
            Persistence.service().persist(tenant);

            TenantUserCredential credential = EntityFactory.create(TenantUserCredential.class);
            credential.setPrimaryKey(user.getPrimaryKey());

            credential.user().set(user);
            //TODO use tokens
            credential.credential().setValue(PasswordEncryptor.encryptPassword(tenant.person().email().getValue()));
            credential.enabled().setValue(Boolean.TRUE);
            credential.behaviors().add(behavior);
            Persistence.service().persist(credential);
        }
        return tenant.user();
    }

    public static MasterApplication createMasterApplication(Lease lease) {
        lease.status().setValue(Lease.Status.ApplicationInProgress);
        Persistence.service().persist(lease);

        MasterApplication ma = EntityFactory.create(MasterApplication.class);
        ma.lease().set(lease);
        ma.status().setValue(MasterApplication.Status.Invited);
        ma.createDate().setValue(new LogicalDate());
        Persistence.service().persist(ma);

        Persistence.service().retrieve(lease.tenants());
        for (TenantInLease tenantInLease : lease.tenants()) {
            if (TenantInLease.Role.Applicant == tenantInLease.role().getValue()) {
                Application a = EntityFactory.create(Application.class);
                a.belongsTo().set(ma);
                a.status().setValue(MasterApplication.Status.Invited);
                a.steps().addAll(ApplicationMgr.createApplicationProgress());
                a.user().set(ensureTenantUser(tenantInLease.tenant(), VistaTenantBehavior.ProspectiveApplicant));
                a.lease().set(ma.lease());
                Persistence.service().persist(a);

                tenantInLease.application().set(a);
                Persistence.service().persist(tenantInLease);

                ma.applications().add(a);
                return ma;
            }
        }
        throw new Error("Main applicant not found");
    }

    public static void makeApplicationCompleted(Application application) {
        application.status().setValue(MasterApplication.Status.Submitted);
        Persistence.service().persist(application);

        TenantUser user = application.user();
        TenantUserCredential credential = Persistence.service().retrieve(TenantUserCredential.class, user.getPrimaryKey());
        boolean isPrimary = credential.behaviors().contains(VistaTenantBehavior.ProspectiveApplicant);
        credential.behaviors().clear();
        credential.behaviors().add(VistaTenantBehavior.ProspectiveSubmited);
        Persistence.service().persist(credential);
        if (Context.isUserLoggedIn() && user.getPrimaryKey().equals(VistaContext.getCurrentUserPrimaryKey())) {
            Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification());
        }

        MasterApplication ma = application.belongsTo();
        Persistence.service().retrieve(ma);
        Lease lease = ma.lease();
        Persistence.service().retrieve(lease);
        boolean allApplicationsSubmited = true;

        if (isPrimary) {
            Persistence.service().retrieve(lease.tenants());
            for (TenantInLease tenantInLease : lease.tenants()) {
                if ((TenantInLease.Role.CoApplicant == tenantInLease.role().getValue() && (!tenantInLease.takeOwnership().isBooleanTrue()))) {
                    Application a = EntityFactory.create(Application.class);
                    a.belongsTo().set(ma);
                    a.status().setValue(MasterApplication.Status.Invited);
                    a.steps().addAll(ApplicationMgr.createApplicationProgress());
                    a.user().set(ensureTenantUser(tenantInLease.tenant(), VistaTenantBehavior.ProspectiveCoApplicant));
                    a.lease().set(ma.lease());
                    Persistence.service().persist(a);

                    tenantInLease.application().set(a);
                    Persistence.service().persist(tenantInLease);

                    ma.applications().add(a);
                    allApplicationsSubmited = false;

                }
            }
            if (!allApplicationsSubmited) {
                // TODO send E-mail to created applications
            }
        } else {
            for (Application app : ma.applications()) {
                if (!app.status().getValue().equals(MasterApplication.Status.Submitted)) {
                    allApplicationsSubmited = false;
                    break;
                }
            }
        }

        if (allApplicationsSubmited) {
            lease.status().setValue(Lease.Status.PendingDecision);
            Persistence.service().persist(lease);
            ma.status().setValue(MasterApplication.Status.PendingDecision);
            Persistence.service().persist(ma);
            for (Application app : ma.applications()) {
                app.status().setValue(MasterApplication.Status.PendingDecision);
                Persistence.service().persist(app);
            }
        }
    }

}
