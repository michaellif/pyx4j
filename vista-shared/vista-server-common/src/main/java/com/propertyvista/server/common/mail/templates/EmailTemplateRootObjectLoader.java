/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates;

import java.text.SimpleDateFormat;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.AbstractUser;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.BuildingT;
import com.propertyvista.server.common.mail.templates.model.EmailTemplateContext;
import com.propertyvista.server.common.mail.templates.model.LeaseT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestCrmT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestProspectT;
import com.propertyvista.server.common.mail.templates.model.PasswordRequestTenantT;
import com.propertyvista.server.common.mail.templates.model.PortalLinksT;
import com.propertyvista.server.common.mail.templates.model.TenantT;
import com.propertyvista.server.common.util.VistaDeployment;

public class EmailTemplateRootObjectLoader {

    public static <T extends IEntity> T loadRootObject(T tObj, EmailTemplateContext context) {
        if (tObj == null || context == null) {
            throw new Error("Loading object or Context cannot be null");
        }
        if (tObj instanceof PortalLinksT) {
            PortalLinksT t = (PortalLinksT) tObj;
            t.PortalHomeUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, false));
            t.TenantPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true) + DeploymentConsts.TENANT_URL);
            t.ProspectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.ProspectiveApp, true));

            // TODO use SiteThemeServicesImpl.getSiteDescriptorFromCache()
            // TODO use proper locale
            SiteDescriptor siteDescriptor = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
            if (siteDescriptor != null && !siteDescriptor.siteTitles().isEmpty()) {
                t.CopyrightNotice().set(siteDescriptor.siteTitles().get(0).copyright());
                t.CompanyName().set(siteDescriptor.siteTitles().get(0).residentPortalTitle());
            }
            t.CompanyLogo().setValue(t.PortalHomeUrl().getValue() + "/logo.png/vista.siteimgrc");

        } else if (tObj instanceof PasswordRequestTenantT) {
            PasswordRequestTenantT t = (PasswordRequestTenantT) tObj;
            if (context.user().isNull() || context.accessToken().isNull()) {
                throw new Error("Both AbstractUser and AccessToken should be provided in context");
            }
            AbstractUser user = context.user();
            String token = context.accessToken().getValue();
            t.RequestorName().set(user.name());
            t.PasswordResetUrl().setValue(getPortalAccessUrl(token));
        } else if (tObj instanceof PasswordRequestProspectT) {
            PasswordRequestProspectT t = (PasswordRequestProspectT) tObj;
            if (context.user().isNull() || context.accessToken().isNull()) {
                throw new Error("Both AbstractUser and AccessToken should be provided in context");
            }
            AbstractUser user = context.user();
            String token = context.accessToken().getValue();
            t.RequestorName().set(user.name());
            t.PasswordResetUrl().setValue(getPtappAccessUrl(token));
        } else if (tObj instanceof PasswordRequestCrmT) {
            PasswordRequestCrmT t = (PasswordRequestCrmT) tObj;
            if (context.user().isNull() || context.accessToken().isNull()) {
                throw new Error("Both AbstractUser and AccessToken should be provided in context");
            }
            AbstractUser user = context.user();
            String token = context.accessToken().getValue();
            t.RequestorName().set(user.name());
            t.PasswordResetUrl().setValue(getCrmAccessUrl(token));
        } else if (tObj instanceof TenantT) {
            TenantT t = (TenantT) tObj;
            TenantInLease tenantInLease = context.tenantInLease();
            if (tenantInLease.isNull()) {
                throw new Error("TenantInLease should be provided in context");
            }
            if (tenantInLease.tenant().user().isValueDetached()) {
                Persistence.service().retrieve(tenantInLease.tenant().user());
            }
            t.Name().set(tenantInLease.tenant().user().name());
        } else if (tObj instanceof BuildingT) {
            BuildingT t = (BuildingT) tObj;
            Building bld = null;
            if (!context.lease().isNull()) {
                bld = getBuilding(context.lease());
            } else if (!context.tenantInLease().isNull()) {
                TenantInLease tenantInLease = context.tenantInLease();
                Lease lease = getLease(tenantInLease);
                bld = getBuilding(lease);
            }
            if (bld == null) {
                throw new Error("Either Building or TenantInLease should be provided in context");
            }
            t.PropertyCode().set(bld.propertyCode());
            t.PropertyName().set(bld.marketing().name());
            t.Website().set(bld.contacts().website());
            t.Address().setValue(bld.info().address().getStringView());
            // set contact info
            for (PropertyContact cont : bld.contacts().propertyContacts()) {
                if (cont.type().isNull()) {
                    continue;
                } else if (cont.type().getValue().equals(PropertyContact.PropertyContactType.administrator)) {
                    t.Administrator().ContactName().set(cont.name());
                    t.Administrator().Phone().set(cont.phone());
                    t.Administrator().Email().set(cont.email());
                } else if (cont.type().getValue().equals(PropertyContact.PropertyContactType.superintendent)) {
                    t.Superintendent().ContactName().set(cont.name());
                    t.Superintendent().Phone().set(cont.phone());
                    t.Superintendent().Email().set(cont.email());
                } else if (cont.type().getValue().equals(PropertyContact.PropertyContactType.mainOffice)) {
                    t.MainOffice().ContactName().set(cont.name());
                    t.MainOffice().Phone().set(cont.phone());
                    t.MainOffice().Email().set(cont.email());
                }
            }
        } else if (tObj instanceof ApplicationT) {
            ApplicationT t = (ApplicationT) tObj;
            Application app = null;
            AbstractUser user = null;
            if (!context.tenantInLease().isNull()) {
                TenantInLease tenantInLease = context.tenantInLease();
                app = getApplication(tenantInLease);
                if (tenantInLease.tenant().user().isValueDetached()) {
                    Persistence.service().retrieve(tenantInLease.tenant().user());
                }
                user = tenantInLease.tenant().user();
            } else if (!context.lease().isNull() && !context.user().isNull()) {
                app = getApplication(context.user(), context.lease());
                user = context.user();
            }
            if (app == null || user == null) {
                throw new Error("Either TenantInLease or AbstractUser and Lease should be provided in context");
            }
            t.ApplicantName().set(user.name());
            t.ReferenceNumber().setValue(app.getPrimaryKey().toString());
            if (!context.accessToken().isNull()) {
                t.SignUpUrl().setValue(getPtappAccessUrl(context.accessToken().getValue()));
            }
        } else if (tObj instanceof LeaseT) {
            LeaseT t = (LeaseT) tObj;
            TenantInLease tenantInLease = context.tenantInLease();
            if (tenantInLease.isNull()) {
                throw new Error("TenantInLease should be provided in context");
            }
            Lease lease = getLease(tenantInLease);
            if (tenantInLease.tenant().user().isValueDetached()) {
                Persistence.service().retrieve(tenantInLease.tenant().user());
            }
            t.ApplicantName().set(tenantInLease.tenant().user().name());
            t.StartDate().setValue(lease.leaseFrom().getStringView());
            t.StartDateWeekDay().setValue(new SimpleDateFormat("EEEE").format(lease.leaseFrom().getValue()));
        }
        return tObj;
    }

    private static String getCrmAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.CRM, true), CrmSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);

    }

    private static String getPtappAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.ProspectiveApp, true), PtSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);

    }

    private static String getPortalAccessUrl(String token) {
        return VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true) + DeploymentConsts.TENANT_URL + '?'
                + AuthenticationService.AUTH_TOKEN_ARG + '=' + token;
    }

    private static Application getApplication(TenantInLease tenantInLease) {
        if (tenantInLease == null || tenantInLease.isNull()) {
            throw new Error("Context cannot be null");
        }

        if (tenantInLease.application().isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.application());
        }
        Application app = tenantInLease.application();
        if (app == null || app.isNull()) {
            throw new Error("Invalid context. No Application found.");
        }
        return app;
    }

    private static Application getApplication(AbstractUser user, Lease lease) {
        if (user == null || user.isNull() || lease == null || lease.isNull()) {
            throw new Error("Context cannot be null");
        }
        EntityQueryCriteria<Application> appSearch = EntityQueryCriteria.create(Application.class);
        appSearch.add(PropertyCriterion.eq(appSearch.proto().user(), user));
        appSearch.add(PropertyCriterion.eq(appSearch.proto().lease(), lease));
        Application app = Persistence.service().retrieve(appSearch);
        if (app == null || app.isNull()) {
            throw new Error("Invalid context. No Application found.");
        }
        return app;
    }

    private static Lease getLease(TenantInLease tenantInLease) {
        if (tenantInLease == null) {
            throw new Error("Context cannot be null");
        }

        if (tenantInLease.leaseV().isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.leaseV());
            Persistence.service().retrieve(tenantInLease.leaseV().holder());
        }
        Lease lease = tenantInLease.leaseV().holder();
        if (lease == null || lease.isNull()) {
            throw new Error("Invalid context. No lease found.");
        }
        return lease;
    }

    private static Building getBuilding(Lease lease) {
        if (lease == null) {
            throw new Error("Context cannot be null");
        }
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
            Persistence.service().retrieve(lease.unit().belongsTo());
        }
        Building bld = lease.unit().belongsTo();
        if (bld == null || bld.isNull()) {
            throw new Error("Invalid context. No building found.");
        }
        Persistence.service().retrieve(bld.contacts().propertyContacts());
        return bld;
    }
}
