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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.mail.template.model.ApplicationT;
import com.propertyvista.biz.communication.mail.template.model.AutopayAgreementT;
import com.propertyvista.biz.communication.mail.template.model.BuildingT;
import com.propertyvista.biz.communication.mail.template.model.CompanyInfoT;
import com.propertyvista.biz.communication.mail.template.model.EmailTemplateContext;
import com.propertyvista.biz.communication.mail.template.model.LeaseT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestT;
import com.propertyvista.biz.communication.mail.template.model.MaintenanceRequestWOT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestCrmT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestProspectT;
import com.propertyvista.biz.communication.mail.template.model.PasswordRequestTenantT;
import com.propertyvista.biz.communication.mail.template.model.PaymentT;
import com.propertyvista.biz.communication.mail.template.model.PortalLinksT;
import com.propertyvista.biz.communication.mail.template.model.TenantT;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.payment.AutopayAgreement.AutopayAgreementCoveredItem;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.settings.PmcCompanyInfo;
import com.propertyvista.domain.settings.PmcCompanyInfoContact;
import com.propertyvista.domain.settings.PmcCompanyInfoContact.CompanyInfoContactType;
import com.propertyvista.domain.site.SiteDescriptor;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class EmailTemplateRootObjectLoader {
    private static final I18n i18n = I18n.get(EmailTemplateRootObjectLoader.class);

    public static <T extends IEntity> T loadRootObject(T tObj, EmailTemplateContext context) {
        if (tObj == null || context == null) {
            throw new Error("Loading object or Context cannot be null");
        }
        if (tObj instanceof CompanyInfoT) {
            CompanyInfoT t = (CompanyInfoT) tObj;
            PmcCompanyInfo info = Persistence.service().retrieve(EntityQueryCriteria.create(PmcCompanyInfo.class));
            t.CompanyName().setValue(info.companyName().getValue());
            for (PmcCompanyInfoContact cont : info.contacts()) {
                if (cont.type().isNull()) {
                    continue;
                } else if (cont.type().getValue().equals(CompanyInfoContactType.administrator)) {
                    t.Administrator().ContactName().set(cont.name());
                    t.Administrator().Phone().set(cont.phone());
                    t.Administrator().Email().set(cont.email());
                }
            }
        } else if (tObj instanceof PortalLinksT) {
            PortalLinksT t = (PortalLinksT) tObj;
            t.SiteHomeUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.site, false));
            t.TenantPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true));
            t.ProspectPortalUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true));
            t.CompanyLogo().setValue(t.SiteHomeUrl().getValue() + "/" + DeploymentConsts.portalLogo + DeploymentConsts.siteImageResourceServletMapping);

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
        } else if (tObj instanceof BuildingT) {
            BuildingT t = (BuildingT) tObj;
            Building bld = null;
            if (!context.lease().isNull()) {
                bld = getBuilding(context.lease());
            } else if (!context.leaseTermParticipant().isNull()) {
                Lease lease = getLease(context.leaseTermParticipant());
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
            Customer customer;
            if (!context.leaseTermParticipant().isNull()) {
                app = getApplication(context.leaseTermParticipant());
                Persistence.ensureRetrieve(context.leaseTermParticipant(), AttachLevel.Attached);
                customer = context.leaseTermParticipant().leaseParticipant().customer();
            } else {
                throw new Error("LeaseTermParticipant should be provided in context");
            }
            t.ApplicantName().setValue(customer.person().name().getStringView());
            t.ApplicantFirstName().setValue(customer.person().name().firstName().getStringView());
            t.ApplicantLastName().setValue(customer.person().name().lastName().getStringView());
            t.ReferenceNumber().setValue(app.getPrimaryKey().toString());
            if (!context.accessToken().isNull()) {
                t.SignUpUrl().setValue(getPtappAccessUrl(context.accessToken().getValue()));
            } else {
                t.SignUpUrl().setValue(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true));
            }
        } else if (tObj instanceof TenantT) {
            TenantT t = (TenantT) tObj;
            Customer customer;
            if (!context.leaseParticipant().isNull()) {
                Persistence.ensureRetrieve(context.leaseParticipant(), AttachLevel.Attached);
                customer = context.leaseParticipant().customer();
            } else if (!context.leaseTermParticipant().isNull()) {
                Persistence.ensureRetrieve(context.leaseTermParticipant(), AttachLevel.Attached);
                customer = context.leaseTermParticipant().leaseParticipant().customer();
            } else {
                throw new Error("LeaseParticipant or LeaseTermParticipant should be provided in context");
            }
            t.Name().setValue(customer.person().name().getStringView());
            t.FirstName().setValue(customer.person().name().firstName().getStringView());
            t.LastName().setValue(customer.person().name().lastName().getStringView());
        } else if (tObj instanceof LeaseT) {
            LeaseT t = (LeaseT) tObj;
            if (context.lease().isNull()) {
                context.lease().set(getLease(context.leaseTermParticipant()));
            }
            t.ApplicantName().setValue(context.leaseTermParticipant().leaseParticipant().customer().person().name().getStringView());
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
            t.resolved().setValue(mr.resolvedDate().getStringView());
            t.resolution().set(mr.resolution());
            t.cancellationNote().set(mr.cancellationNote());
            // generate url for maintenance request viewer in Resident Portal
            String residentUrl = VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true);
            String mrUrl = AppPlaceInfo.absoluteUrl(residentUrl, true,
                    new ResidentPortalSiteMap.Maintenance.MaintenanceRequestPage().formPlace(mr.getPrimaryKey()));
            t.requestViewUrl().setValue(mrUrl);
        } else if (tObj instanceof MaintenanceRequestWOT) {
            MaintenanceRequestWOT t = (MaintenanceRequestWOT) tObj;

            MaintenanceRequest mr = context.maintenanceRequest();
            MaintenanceRequestSchedule wo = mr.workHistory().get(mr.workHistory().size() - 1);
            Persistence.ensureRetrieve(wo, AttachLevel.Attached);
            t.scheduledDate().setValue(wo.scheduledDate().getStringView());
            t.scheduledTimeSlot().setValue(i18n.tr("between {0} and {1}", wo.scheduledTimeFrom().getStringView(), wo.scheduledTimeTo().getStringView()));
            t.workDescription().set(wo.workDescription());
        } else if (tObj instanceof AutopayAgreementT) {
            AutopayAgreementT t = (AutopayAgreementT) tObj;
            if (context.preauthorizedPayment().isNull()) {
                throw new Error("PreauthorizedPayment should be provided in context");
            }
            BigDecimal amount = BigDecimal.ZERO;
            for (AutopayAgreementCoveredItem item : context.preauthorizedPayment().coveredItems()) {
                amount = amount.add(item.amount().getValue());
            }
            t.Amount().setValue(i18n.tr("$") + amount.toString());
            t.NextPaymentDate().setValue(
                    new SimpleDateFormat("MM/dd/yyyy").format(ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayDate(context.lease())));
        } else if (tObj instanceof PaymentT) {
            PaymentT t = (PaymentT) tObj;
            if (context.paymentRecord().isNull()) {
                throw new Error("PaymentRecord should be provided in context");
            }
            t.Amount().setValue(i18n.tr("$") + context.paymentRecord().amount().getStringView());
            t.ConvenienceFee().setValue(i18n.tr("$") + context.paymentRecord().convenienceFee().getStringView());
            t.ReferenceNumber().setValue(context.paymentRecord().id().getStringView());
            t.Date().setValue(context.paymentRecord().receivedDate().getStringView());
            t.RejectReason().setValue(context.paymentRecord().transactionErrorMessage().getValue());
        }
        return tObj;
    }

    private static String getCrmAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.crm, true), true, CrmSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);

    }

    private static String getPtappAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.prospect, true), true, PortalSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);

    }

    private static String getPortalAccessUrl(String token) {
        return AppPlaceInfo.absoluteUrl(VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true), true, PortalSiteMap.LoginWithToken.class,
                AuthenticationService.AUTH_TOKEN_ARG, token);
    }

    private static OnlineApplication getApplication(LeaseTermParticipant<?> tenantInLease) {
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

    private static Lease getLease(LeaseTermParticipant<?> tenantInLease) {
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
