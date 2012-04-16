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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.mail.MessageTemplates;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.TenantUserHolder;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.PersonGuarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.dto.OnlineApplicationStatusDTO;
import com.propertyvista.dto.OnlineApplicationStatusDTO.Role;
import com.propertyvista.dto.OnlineMasterApplicationStatusDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.common.util.IdAssignmentSequenceUtil;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class ApplicationManager {

    private static final I18n i18n = I18n.get(ApplicationManager.class);

    public static MasterOnlineApplication createMasterApplication(Lease lease) {
        if (!lease.application().isNull()) {
            Persistence.service().retrieve(lease.application());
            return lease.application();
        }

        MasterOnlineApplication mapp = EntityFactory.create(MasterOnlineApplication.class);
        mapp.lease().set(lease);
        mapp.status().setValue(MasterOnlineApplication.Status.Incomplete);

        mapp.onlineApplicationId().setValue(IdAssignmentSequenceUtil.getId(IdTarget.application));
        Persistence.service().persist(mapp);

        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenantInLease : lease.version().tenants()) {
            if (Tenant.Role.Applicant == tenantInLease.role().getValue()) {
                OnlineApplication app = EntityFactory.create(OnlineApplication.class);
                app.belongsTo().set(mapp);
                app.status().setValue(OnlineApplication.Status.Invited);
                app.steps().addAll(ApplicationManager.createApplicationProgress(app, tenantInLease.customer(), VistaTenantBehavior.ProspectiveApplicant));
                app.user().set(
                        ensureProspectiveTenantUser(tenantInLease.customer(), tenantInLease.customer().person(), VistaTenantBehavior.ProspectiveApplicant));
                app.lease().set(mapp.lease());
                Persistence.service().persist(app);

                tenantInLease.application().set(app);
                Persistence.service().persist(tenantInLease);

                lease.application().set(mapp);
                Persistence.service().merge(lease);

                mapp.applications().add(app);
                Persistence.service().merge(mapp);
                return mapp;
            }
        }

        // oops:
        Persistence.service().delete(mapp);
        throw new UserRuntimeException("Main applicant not found");
    }

    public static void sendMasterApplicationEmail(MasterOnlineApplication mapp) {
        Persistence.service().retrieve(mapp.lease());
        Persistence.service().retrieve(mapp.lease().version().tenants());
        for (Tenant tenantInLease : mapp.lease().version().tenants()) {
            if (Tenant.Role.Applicant == tenantInLease.role().getValue()) {
                Persistence.service().retrieve(tenantInLease.customer().user());
                sendInvitationEmail(tenantInLease.customer().user(), mapp.lease(), EmailTemplateType.ApplicationCreatedApplicant);

                //mapp.status().setValue(OnlineMasterApplication.Status.Invited);
                Persistence.service().persist(mapp);

                mapp.lease().version().status().setValue(Lease.Status.ApplicationInProgress);
                Persistence.service().merge(mapp.lease());
                break;
            }
        }
    }

    public static void sendApproveDeclineApplicationEmail(Lease lease, boolean isApproved) {
        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenantInLease : lease.version().tenants()) {
            OnlineApplication test = tenantInLease.application();
            if (test.getValue() == null) { //co-applicants have no dedicated application
                return;
            }
            MailMessage m = MessageTemplates.createApplicationStatusEmail(tenantInLease, isApproved ? EmailTemplateType.ApplicationApproved
                    : EmailTemplateType.ApplicationDeclined);
            if (MailDeliveryStatus.Success != Mail.send(m)) {
                throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
            }
        }
    }

    public static void makeApplicationCompleted(OnlineApplication application) {
        application.status().setValue(OnlineApplication.Status.Submitted);
        Persistence.service().persist(application);

        TenantUser user = application.user();
        TenantUserCredential credential = Persistence.service().retrieve(TenantUserCredential.class, user.getPrimaryKey());
        boolean isApplicant = credential.behaviors().contains(VistaTenantBehavior.ProspectiveApplicant);
        boolean isCoApplicant = credential.behaviors().contains(VistaTenantBehavior.ProspectiveCoApplicant);
        boolean isGuarantor = credential.behaviors().contains(VistaTenantBehavior.Guarantor);

        credential.behaviors().clear();
        credential.behaviors().add(VistaTenantBehavior.ProspectiveSubmitted);
        if (isApplicant) {
            credential.behaviors().add(VistaTenantBehavior.ProspectiveSubmittedApplicant);
        } else if (isGuarantor) {
            credential.behaviors().add(VistaTenantBehavior.GuarantorSubmitted);
        } else if (isCoApplicant) {
            credential.behaviors().add(VistaTenantBehavior.ProspectiveSubmittedCoApplicant);
        }

        Persistence.service().persist(credential);
        if (Context.isUserLoggedIn() && user.getPrimaryKey().equals(VistaContext.getCurrentUserPrimaryKey())) {
            Context.getVisit().setAclRevalidationRequired();
            Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification());
        }

        MasterOnlineApplication ma = application.belongsTo();
        Persistence.service().retrieve(ma);
        Persistence.service().retrieve(ma.lease());
        boolean allApplicationsSubmited = false;

        // Invite Guarantors:
        if (!isGuarantor) {
            EntityQueryCriteria<Tenant> criteriaTL = EntityQueryCriteria.create(Tenant.class);
            criteriaTL.add(PropertyCriterion.eq(criteriaTL.proto().application(), application));
            Tenant tenantInLease = Persistence.service().retrieve(criteriaTL);

            EntityQueryCriteria<PersonScreening> criteriaPS = EntityQueryCriteria.create(PersonScreening.class);
            criteriaPS.add(PropertyCriterion.eq(criteriaPS.proto().screene(), tenantInLease.customer()));
            PersonScreening tenantScreenings = Persistence.service().retrieve(criteriaPS);

            Persistence.service().retrieve(tenantScreenings.guarantors());
            for (PersonGuarantor personGuarantor : tenantScreenings.guarantors()) {
                inviteUser(ma, personGuarantor.guarantor(), personGuarantor.guarantor().customer().person(), VistaTenantBehavior.Guarantor);
            }
        }

        // Invite CoApplicants:
        if (isApplicant) {
            Persistence.service().retrieve(ma.lease().version().tenants());
            for (Tenant tenantInLease : ma.lease().version().tenants()) {
                if ((Tenant.Role.CoApplicant == tenantInLease.role().getValue() && (!tenantInLease.takeOwnership().isBooleanTrue()))) {
                    tenantInLease.application().set(
                            inviteUser(ma, tenantInLease.customer(), tenantInLease.customer().person(), VistaTenantBehavior.ProspectiveCoApplicant));
                    Persistence.service().persist(tenantInLease);
                }
            }
        } else {
            allApplicationsSubmited = true;
            Persistence.service().retrieve(ma.applications());
            for (OnlineApplication app : ma.applications()) {
                if (!app.status().getValue().equals(MasterOnlineApplication.Status.Submitted)) {
                    allApplicationsSubmited = false;
                    break;
                }
            }
        }

        if (allApplicationsSubmited) {
            //ma.status().setValue(OnlineMasterApplication.Status.PendingDecision);
            Persistence.service().persist(ma);
            for (OnlineApplication app : ma.applications()) {
                //app.status().setValue(OnlineMasterApplication.Status.PendingDecision);
                Persistence.service().persist(app);
            }
        }
    }

    public static OnlineApplication inviteUser(MasterOnlineApplication ma, TenantUserHolder tenant, Person person, VistaTenantBehavior behaviour) {
        OnlineApplication application = null;
        for (OnlineApplication app : ma.applications()) {
            Persistence.service().retrieve(app);
            if (app.user().equals(tenant.user())) {
                application = app;
            }
        }
        // create new if not found:
        if (application == null) {
            application = EntityFactory.create(OnlineApplication.class);

            application.belongsTo().set(ma);
            application.lease().set(ma.lease());
            //application.status().setValue(OnlineMasterApplication.Status.Created);
            application.steps().addAll(ApplicationManager.createApplicationProgress(application, tenant, behaviour));
            application.user().set(ensureProspectiveTenantUser(tenant, person, behaviour));

            Persistence.service().persist(application);
            ma.applications().add(application);
            Persistence.service().persist(ma);
        }
        if (application.user().isValueDetached()) {
            Persistence.service().retrieve(application.user());
        }
        if (application.lease().isValueDetached()) {
            Persistence.service().retrieve(application.lease());
        }

        switch (behaviour) {
        case ProspectiveApplicant:
            sendInvitationEmail(application.user(), application.lease(), EmailTemplateType.ApplicationCreatedApplicant);
            break;
        case ProspectiveCoApplicant:
            sendInvitationEmail(application.user(), application.lease(), EmailTemplateType.ApplicationCreatedCoApplicant);
            break;
        case Guarantor:
            sendInvitationEmail(application.user(), application.lease(), EmailTemplateType.ApplicationCreatedGuarantor);
            break;
        }

        // update app status:
        //application.status().setValue(OnlineMasterApplication.Status.Invited);
        Persistence.service().persist(application);
        return application;
    }

    public static OnlineMasterApplicationStatusDTO calculateStatus(MasterOnlineApplication ma) {
        OnlineMasterApplicationStatusDTO maStatus = EntityFactory.create(OnlineMasterApplicationStatusDTO.class);

        double progressSum = 0.0;

        for (OnlineApplication app : ma.applications()) {
            if (app.isValueDetached()) {
                Persistence.service().retrieve(app);
            }

            OnlineApplicationStatusDTO status = EntityFactory.create(OnlineApplicationStatusDTO.class);

            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), app.user()));
            Customer tenant = Persistence.service().retrieve(criteria);
            if (tenant != null) {
                status.person().set(tenant.person().name());
                status.role().setValue(Role.Tenant);
            } else {
                EntityQueryCriteria<PersonGuarantor> criteria1 = EntityQueryCriteria.create(PersonGuarantor.class);
                criteria1.add(PropertyCriterion.eq(criteria1.proto().guarantor().user(), app.user()));
                PersonGuarantor guarantor = Persistence.service().retrieve(criteria1);
                if (guarantor != null) {
                    status.person().set(guarantor.guarantor().customer().person().name());
                    status.role().setValue(Role.Guarantor);
                }
            }

            //status.status().setValue(app.status().getValue());
            status.user().set(app.user());

            // calculate progress:
            if (!status.person().isEmpty()) {
                int complete = 0;
                for (int i = 0; i < app.steps().size(); ++i) {
                    switch (app.steps().get(i).status().getValue()) {
                    case complete:
                        ++complete;
                    case latest:
                        if (i + 1 == app.steps().size()) {
                            ++complete; // count last 'Completion' step...
                        }
                        break;
                    }
                }

                status.progress().setValue(1.0 * complete / app.steps().size() * 100.0);
                status.description().setValue(SimpleMessageFormat.format("{0} out of {1} steps completed", complete, app.steps().size()));

                maStatus.individualApplications().add(status);
            }

            progressSum += status.progress().getValue();
        }

        maStatus.progress().setValue(progressSum / ma.applications().size());
        return maStatus;
    }

    private static ApplicationWizardStep createWizardStep(Class<? extends AppPlace> place, ApplicationWizardStep.Status status) {
        ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
        ws.placeId().setValue(AppPlaceInfo.getPlaceId(place));
        ws.status().setValue(status);
        return ws;
    }

    // internals:

    private static List<ApplicationWizardStep> createApplicationProgress(OnlineApplication application, TenantUserHolder tenant, VistaTenantBehavior behaviour) {
        List<ApplicationWizardStep> progress = new Vector<ApplicationWizardStep>();
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            for (Class<? extends AppPlace> place : Arrays.<Class<? extends AppPlace>> asList(//@formatter:off
                        PtSiteMap.ApprovedTenantWizard.ReviewLease.class,
                        PtSiteMap.ApprovedTenantWizard.MoveInSchedule.class,
                        PtSiteMap.ApprovedTenantWizard.Insurance.class,
                        PtSiteMap.ApprovedTenantWizard.CompletetionMessage.class
                    )) {//@formatter:on
                progress.add(createWizardStep(place, ApplicationWizardStep.Status.notVisited));
            }
            progress.get(0).status().setValue(ApplicationWizardStep.Status.latest);

        } else {
            progress.add(createWizardStep(PtSiteMap.Apartment.class, ApplicationWizardStep.Status.latest));
            progress.add(createWizardStep(PtSiteMap.Tenants.class, ApplicationWizardStep.Status.notVisited));
            progress.add(createWizardStep(PtSiteMap.Info.class, ApplicationWizardStep.Status.notVisited));
            progress.add(createWizardStep(PtSiteMap.Financial.class, ApplicationWizardStep.Status.notVisited));
// TODO : Charges and Payment steps are closed (removed) so far...        
            if (false) {
                progress.add(createWizardStep(PtSiteMap.Charges.class, ApplicationWizardStep.Status.notVisited));
            }
            progress.add(createWizardStep(PtSiteMap.Summary.class, ApplicationWizardStep.Status.notVisited));
// TODO : Charges and Payment steps are closed (removed) so far...        
            if (false) {
                switch (behaviour) {
                case ProspectiveApplicant:
                    progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
                case ProspectiveCoApplicant:
                    if (isTenantInSplitCharge(application, tenant)) {
                        progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
                    }
                    break;
                }
            }
        }
        progress.add(createWizardStep(PtSiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
        return progress;
    }

    public static TenantUser ensureProspectiveTenantUser(TenantUserHolder tenant, Person person, VistaTenantBehavior behavior) {
        TenantUser user = tenant.user();
        if (user.getPrimaryKey() == null) {
            if (person.email().isNull()) {
                throw new UnRecoverableRuntimeException(i18n.tr("Can't create application user for tenant  {0} without e-mail address", person.name()
                        .getStringView()));
            }
            user.name().setValue(person.name().getStringView());
            user.email().setValue(person.email().getValue());
            Persistence.service().persist(user);
            Persistence.service().persist(tenant);

            TenantUserCredential credential = EntityFactory.create(TenantUserCredential.class);
            credential.setPrimaryKey(user.getPrimaryKey());

            credential.user().set(user);
            if (ApplicationMode.isDevelopment()) {
                credential.credential().setValue(PasswordEncryptor.encryptPassword(person.email().getValue()));
            }
            credential.enabled().setValue(Boolean.TRUE);
            // TODO tenant can be guarantor in other applications.
            credential.behaviors().clear();
            credential.behaviors().add(behavior);
            Persistence.service().persist(credential);
        } else {
            TenantUserCredential credential = Persistence.service().retrieve(TenantUserCredential.class, user.getPrimaryKey());
            credential.setPrimaryKey(user.getPrimaryKey());
            credential.user().set(user);
            if (ApplicationMode.isDevelopment()) {
                credential.credential().setValue(PasswordEncryptor.encryptPassword(person.email().getValue()));
            }
            credential.enabled().setValue(Boolean.TRUE);
            // TODO tenant can be guarantor in other applications.
            credential.behaviors().clear();
            credential.behaviors().add(behavior);
            Persistence.service().persist(credential);
        }
        return tenant.user();
    }

    public static boolean isTenantInSplitCharge(OnlineApplication application, TenantUserHolder tenant) {
        EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
// TODO: make sure we get user from necessary application (if he participate in more than one):         
//        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), tenant));
        Tenant tenantInLease = Persistence.service().retrieve(criteria);

        return !(tenantInLease.percentage().isNull() || tenantInLease.percentage().getValue() == 0);
    }

    private static void sendInvitationEmail(TenantUser user, Lease lease, EmailTemplateType emailTemplateType) {
        String token = AccessKey.createAccessToken(user, TenantUserCredential.class, 10);
        if (token == null) {
            throw new UserRuntimeException("Invalid user account");
        }
        MailMessage m = MessageTemplates.createTenantInvitationEmail(user, lease, emailTemplateType, token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }
}
