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
package com.propertyvista.biz.communication.mail.template;

import java.text.SimpleDateFormat;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.mail.template.model.ApplicationT;
import com.propertyvista.biz.communication.mail.template.model.BuildingT;
import com.propertyvista.biz.communication.mail.template.model.EmailTemplateContext;
import com.propertyvista.biz.communication.mail.template.model.LeaseT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestWOT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestCrmT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestProspectT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestTenantT;
import com.propertyvista.biz.communication.mail.template.model.PortalLinksT;
import com.propertyvista.biz.communication.mail.template.model.TenantT;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class EmailTemplateRootObjectLoader {
    private static final I18n i18n = I18n.get(EmailTemplateRootObjectLoader.class);

    public static <T extends IEntity> T loadRootObject(T tObj, EmailTemplateContext context) {
        if (tObj == null || context == null) {
            throw new Error("Loading object or Context cannot be null");
        }
        if (tObj instanceof PortalLinksT) {
            PortalLinksT t = (PortalLinksT) tObj;
            t.PortalHomeUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.site, false));
            t.TenantPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.portal, true));
            t.ProspectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true));
            t.CompanyLogo().setValue(t.PortalHomeUrl().getValue() + "/" + DeploymentConsts.portalLogo + DeploymentConsts.siteImageResourceServletMapping);

            // TODO use SiteThemeServicesImpl.getSiteDescriptorFromCache()
            // TODO use proper locale
            SiteDescriptor siteDescriptor = Persistence.service().retrieve(EntityQueryCriteria.create(SiteDescriptor.class));
            if (siteDescriptor != null && !siteDescriptor.siteTitles().isEmpty()) {
                t.CopyrightNotice().set(siteDescriptor.siteTitles().get(0).copyright());
                t.CompanyName().set(siteDescriptor.siteTitles().get(0).residentPortalTitle());
            }

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
            if (context.leaseParticipant().isNull()) {
                throw new Error("TenantInLease should be provided in context");
            }
            t.Name().setValue(context.leaseParticipant().leaseParticipant().customer().person().name().getStringView());
        } else if (tObj instanceof BuildingT) {
            BuildingT t = (BuildingT) tObj;
            Building bld = null;
            if (!context.lease().isNull()) {
                bld = getBuilding(context.lease());
            } else if (!context.leaseParticipant().isNull()) {
                Lease lease = getLease(context.leaseParticipant());
                bld = getBuilding(lease);
            } else if (!context.maintenanceRequest().isNull()) {
                Persistence.ensureRetrieve(context.maintenanceRequest().building(), AttachLevel.Attached);
                bld = context.maintenanceRequest().building();
            }
            if (bld == null) {
                throw new Error("Either Building or TenantInLease should be provided in context");
            }
            t.PropertyCode().set(bld.propertyCode());
            t.PropertyMarketingName().set(bld.marketing().name());
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
            OnlineApplication app = null;
            AbstractUser user = null;
            if (!context.leaseParticipant().isNull()) {
                app = getApplication(context.leaseParticipant());
                if (context.leaseParticipant().leaseParticipant().customer().user().isValueDetached()) {
                    Persistence.service().retrieve(context.leaseParticipant().leaseParticipant().customer().user());
                }
                user = context.leaseParticipant().leaseParticipant().customer().user();
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
            if (context.lease().isNull()) {
                context.lease().set(getLease(context.leaseParticipant()));
            }
            t.ApplicantName().setValue(context.leaseParticipant().leaseParticipant().customer().person().name().getStringView());
            t.StartDate().setValue(context.lease().currentTerm().termFrom().getStringView());
            t.StartDateWeekDay().setValue(new SimpleDateFormat("EEEE").format(context.lease().currentTerm().termFrom().getValue()));
        } else if (tObj instanceof MaintenanceRequestT) {
            MaintenanceRequestT t = (MaintenanceRequestT) tObj;

            MaintenanceRequest mr = context.maintenanceRequest();
            Persistence.ensureRetrieve(mr.building(), AttachLevel.Attached);
            t.requestId().set(mr.requestId());
            t.propertyCode().set(mr.building().propertyCode());
            t.unitNo().setValue(mr.unit().isNull() ? "" : mr.unit().info().number().getStringView());
            t.category().setValue(formatMaintenanceCategory(mr.category()));
            t.description().set(mr.description());
            t.summary().set(mr.summary());
            t.permissionToEnter().setValue(mr.permissionToEnter().getStringView());
            t.petInstructions().set(mr.petInstructions());
            if (!mr.unit().isNull()) {
                t.unitNo().set(mr.unit().info().number());
            }
            if (!mr.originator().isNull()) {
                t.originatorName().set(mr.originator().name());
            }
            if (!mr.reporter().isNull()) {
                t.reporterName().setValue(mr.reporter().customer().person().name().getStringView());
                t.reporterPhone().setValue(mr.reporter().customer().person().homePhone().getStringView());
                t.reporterEmail().set(mr.reporter().customer().person().email());
            } else {
                t.reporterName().set(mr.reporterName());
                t.reporterPhone().set(mr.reporterPhone());
                t.reporterEmail().set(mr.reporterEmail());
            }
            t.preferredDateTime1().setValue(formatEntryDateTime(mr.preferredDate1().getStringView(), mr.preferredTime1().getStringView()));
            t.preferredDateTime2().setValue(formatEntryDateTime(mr.preferredDate2().getStringView(), mr.preferredTime2().getStringView()));
            t.priority().setValue(mr.priority().getStringView());
            t.status().setValue(mr.status().getStringView());
            t.submitted().setValue(mr.submitted().getStringView());
            t.updated().setValue(mr.updated().getStringView());
            t.cancellationNote().set(mr.cancellationNote());
            // generate url for maintenance request viewer in Resident Portal
            String residentUrl = VistaDeployment.getBaseApplicationURL(VistaApplication.portal, true);
            String mrUrl = AppPlaceInfo.absoluteUrl(residentUrl, true,
                    new PortalSiteMap.Resident.Maintenance.ViewMaintenanceRequest().formPlace(mr.getPrimaryKey()));
            t.requestViewUrl().setValue(mrUrl);
        } else if (tObj instanceof MaintenanceRequestWOT) {
            MaintenanceRequestWOT t = (MaintenanceRequestWOT) tObj;

            MaintenanceRequest mr = context.maintenanceRequest();
            MaintenanceRequestSchedule wo = mr.workHistory().get(mr.workHistory().size() - 1);
            Persistence.ensureRetrieve(wo, AttachLevel.Attached);
            t.scheduledDate().setValue(wo.scheduledDate().getStringView());
            t.scheduledTimeSlot().setValue(i18n.tr("between {0} and {1}", wo.scheduledTimeFrom().getStringView(), wo.scheduledTimeTo().getStringView()));
            t.workDescription().set(wo.workDescription());
        }
        return tObj;
    }

    private static String getCrmAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.crm, true), true, CrmSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);

    }

    private static String getPtappAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true), true, PtSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);

    }

    private static String getPortalAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.portal, true), true, PortalSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);
    }

    private static OnlineApplication getApplication(LeaseTermParticipant tenantInLease) {
        if (tenantInLease == null || tenantInLease.isNull()) {
            throw new Error("Context cannot be null");
        }

        if (tenantInLease.application().isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.application());
        }
        OnlineApplication app = tenantInLease.application();
        if (app == null || app.isNull()) {
            throw new Error("Invalid context. No Application found.");
        }
        return app;
    }

    private static OnlineApplication getApplication(AbstractUser user, Lease lease) {
        if (user == null || user.isNull() || lease == null || lease.isNull()) {
            throw new Error("Context cannot be null");
        }
        EntityQueryCriteria<OnlineApplication> appSearch = EntityQueryCriteria.create(OnlineApplication.class);
        appSearch.add(PropertyCriterion.eq(appSearch.proto().customer().user(), user));
        appSearch.add(PropertyCriterion.eq(appSearch.proto().masterOnlineApplication().leaseApplication().lease(), lease));
        OnlineApplication app = Persistence.service().retrieve(appSearch);
        if (app == null || app.isNull()) {
            throw new Error("Invalid context. No Application found.");
        }
        return app;
    }

    private static Lease getLease(LeaseTermParticipant tenantInLease) {
        if (tenantInLease.isNull()) {
            throw new Error("Context cannot be null");
        }

        if (tenantInLease.leaseTermV().isValueDetached()) {
            Persistence.service().retrieve(tenantInLease.leaseTermV());
        }
        Persistence.service().retrieve(tenantInLease.leaseTermV().holder().lease());

        Lease lease = tenantInLease.leaseTermV().holder().lease();
        if (lease == null || lease.isNull()) {
            throw new Error("Invalid context. No lease found.");
        }
        return lease;
    }

    private static Building getBuilding(Lease lease) {
        if (lease.isNull()) {
            throw new Error("Context cannot be null");
        }
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
        }
        if (lease.unit().building().isValueDetached()) {
            Persistence.service().retrieve(lease.unit().building());
        }

        Persistence.service().retrieve(lease.unit().building().contacts().propertyContacts());
        return lease.unit().building();
    }

    private static String formatMaintenanceCategory(MaintenanceRequestCategory category) {
        StringBuilder result = new StringBuilder();
        do {
            Persistence.ensureRetrieve(category, AttachLevel.Attached);
            Persistence.ensureRetrieve(category.parent(), AttachLevel.Attached);
            if (!category.name().isNull()) {
                result.insert(0, result.length() > 0 ? " > " : "").insert(0, category.name().getValue());
            }
            category = category.parent();
        } while (!category.parent().isNull());
        return result.toString();
    }

    private static String formatEntryDateTime(String date, String time) {
        return (date == null || date.length() == 0 || time == null || time.length() == 0) ? i18n.tr("Not Provided") : date + ", " + time;
    }
}
