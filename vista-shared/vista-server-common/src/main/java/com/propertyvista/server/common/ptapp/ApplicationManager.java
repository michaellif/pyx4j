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

import com.pyx4j.commons.SimpleMessageFormat;
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

import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.TenantUserHolder;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.PersonGuarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.ApplicationStatusDTO;
import com.propertyvista.dto.ApplicationStatusDTO.Role;
import com.propertyvista.dto.MasterApplicationStatusDTO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.server.common.mail.MessageTemplates;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.common.util.IdAssignmentSequenceUtil;
import com.propertyvista.server.domain.security.TenantUserCredential;

public class ApplicationManager {

    private static final I18n i18n = I18n.get(ApplicationManager.class);

    public static MasterApplication createMasterApplication(Lease lease) {
        if (!lease.application().isNull()) {
            Persistence.service().retrieve(lease.application());
            return lease.application();
        }

        MasterApplication mapp = EntityFactory.create(MasterApplication.class);
        mapp.lease().set(lease);
        mapp.status().setValue(MasterApplication.Status.Created);

        mapp.applicationId().setValue(IdAssignmentSequenceUtil.getId(IdTarget.application));
        Persistence.service().persist(mapp);

        Persistence.service().retrieve(lease.version().tenants());
        for (TenantInLease tenantInLease : lease.version().tenants()) {
            if (TenantInLease.Role.Applicant == tenantInLease.role().getValue()) {
                Application app = EntityFactory.create(Application.class);
                app.belongsTo().set(mapp);
                app.status().setValue(MasterApplication.Status.Created);
                app.steps().addAll(ApplicationManager.createApplicationProgress(app, tenantInLease.tenant(), VistaTenantBehavior.ProspectiveApplicant));
                app.user().set(ensureProspectiveTenantUser(tenantInLease.tenant(), tenantInLease.tenant().person(), VistaTenantBehavior.ProspectiveApplicant));
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

    public static void sendMasterApplicationEmail(MasterApplication mapp) {
        Persistence.service().retrieve(mapp.lease());
        Persistence.service().retrieve(mapp.lease().version().tenants());
        for (TenantInLease tenantInLease : mapp.lease().version().tenants()) {
            if (TenantInLease.Role.Applicant == tenantInLease.role().getValue()) {
                Persistence.service().retrieve(tenantInLease.tenant().user());
                sendInvitationEmail(tenantInLease.tenant().user(), mapp.lease(), EmailTemplateType.ApplicationCreatedApplicant);

                mapp.status().setValue(MasterApplication.Status.Invited);
                Persistence.service().persist(mapp);

                mapp.lease().version().status().setValue(Lease.Status.ApplicationInProgress);
                Persistence.service().merge(mapp.lease());
                break;
            }
        }
    }

    public static void sendApproveDeclineApplicationEmail(Lease lease, boolean isApproved) {
        Persistence.service().retrieve(lease.version().tenants());
        for (TenantInLease tenantInLease : lease.version().tenants()) {
            Application test = tenantInLease.application();
            if (test.getValue() == null) { //co-applicants have no dedicated application
                return;
            }
            Persistence.service().retrieve(tenantInLease.tenant().user());
            TenantUser user = tenantInLease.tenant().user();
            MailMessage m = new MailMessage();
            m.setTo(user.email().getValue());
            m.setSender(MessageTemplates.getSender());
            sendApproveDeclineEmail(user, tenantInLease, isApproved);
        }
    }

    public static void makeApplicationCompleted(Application application) {
        application.status().setValue(MasterApplication.Status.Submitted);
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

        MasterApplication ma = application.belongsTo();
        Persistence.service().retrieve(ma);
        Persistence.service().retrieve(ma.lease());
        boolean allApplicationsSubmited = false;

        // Invite Guarantors:
        if (!isGuarantor) {
            EntityQueryCriteria<TenantInLease> criteriaTL = EntityQueryCriteria.create(TenantInLease.class);
            criteriaTL.add(PropertyCriterion.eq(criteriaTL.proto().application(), application));
            TenantInLease tenantInLease = Persistence.service().retrieve(criteriaTL);

            EntityQueryCriteria<PersonScreening> criteriaPS = EntityQueryCriteria.create(PersonScreening.class);
            criteriaPS.add(PropertyCriterion.eq(criteriaPS.proto().screene(), tenantInLease.tenant()));
            PersonScreening tenantScreenings = Persistence.service().retrieve(criteriaPS);

            Persistence.service().retrieve(tenantScreenings.guarantors());
            for (PersonGuarantor personGuarantor : tenantScreenings.guarantors()) {
                inviteUser(ma, personGuarantor.guarantor(), personGuarantor.guarantor().person(), VistaTenantBehavior.Guarantor);
            }
        }

        // Invite CoApplicants:
        if (isApplicant) {
            Persistence.service().retrieve(ma.lease().version().tenants());
            for (TenantInLease tenantInLease : ma.lease().version().tenants()) {
                if ((TenantInLease.Role.CoApplicant == tenantInLease.role().getValue() && (!tenantInLease.takeOwnership().isBooleanTrue()))) {
                    tenantInLease.application().set(
                            inviteUser(ma, tenantInLease.tenant(), tenantInLease.tenant().person(), VistaTenantBehavior.ProspectiveCoApplicant));
                    Persistence.service().persist(tenantInLease);
                }
            }
        } else {
            allApplicationsSubmited = true;
            Persistence.service().retrieve(ma.applications());
            for (Application app : ma.applications()) {
                if (!app.status().getValue().equals(MasterApplication.Status.Submitted)) {
                    allApplicationsSubmited = false;
                    break;
                }
            }
        }

        if (allApplicationsSubmited) {
            ma.status().setValue(MasterApplication.Status.PendingDecision);
            Persistence.service().persist(ma);
            for (Application app : ma.applications()) {
                app.status().setValue(MasterApplication.Status.PendingDecision);
                Persistence.service().persist(app);
            }
        }
    }

    public static Application inviteUser(MasterApplication ma, TenantUserHolder tenant, Person person, VistaTenantBehavior behaviour) {
        Application application = null;
        for (Application app : ma.applications()) {
            Persistence.service().retrieve(app);
            if (app.user().equals(tenant.user())) {
                application = app;
            }
        }
        // create new if not found:
        if (application == null) {
            application = EntityFactory.create(Application.class);

            application.belongsTo().set(ma);
            application.lease().set(ma.lease());
            application.status().setValue(MasterApplication.Status.Created);
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
        application.status().setValue(MasterApplication.Status.Invited);
        Persistence.service().persist(application);
        return application;
    }

    public static MasterApplicationStatusDTO calculateStatus(MasterApplication ma) {
        MasterApplicationStatusDTO maStatus = EntityFactory.create(MasterApplicationStatusDTO.class);

        double progressSum = 0.0;

        for (Application app : ma.applications()) {
            if (app.isValueDetached()) {
                Persistence.service().retrieve(app);
            }

            ApplicationStatusDTO status = EntityFactory.create(ApplicationStatusDTO.class);

            EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), app.user()));
            Tenant tenant = Persistence.service().retrieve(criteria);
            if (tenant != null) {
                status.person().set(tenant.person().name());
                status.role().setValue(Role.Tenant);
            } else {
                EntityQueryCriteria<PersonGuarantor> criteria1 = EntityQueryCriteria.create(PersonGuarantor.class);
                criteria1.add(PropertyCriterion.eq(criteria1.proto().guarantor().user(), app.user()));
                PersonGuarantor guarantor = Persistence.service().retrieve(criteria1);
                if (guarantor != null) {
                    status.person().set(guarantor.guarantor().person().name());
                    status.role().setValue(Role.Guarantor);
                }
            }

            status.status().setValue(app.status().getValue());
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

    private static List<ApplicationWizardStep> createApplicationProgress(Application application, TenantUserHolder tenant, VistaTenantBehavior behaviour) {
        List<ApplicationWizardStep> progress = new Vector<ApplicationWizardStep>();
        progress.add(createWizardStep(PtSiteMap.Apartment.class, ApplicationWizardStep.Status.latest));
        progress.add(createWizardStep(PtSiteMap.Tenants.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Info.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Financial.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Charges.class, ApplicationWizardStep.Status.notVisited));
        progress.add(createWizardStep(PtSiteMap.Summary.class, ApplicationWizardStep.Status.notVisited));
        switch (behaviour) {
        case ProspectiveApplicant:
            progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
        case ProspectiveCoApplicant:
            if (isTenantInSplitCharge(application, tenant)) {
                progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
            }
            break;
        }
        progress.add(createWizardStep(PtSiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
        return progress;
    }

    private static TenantUser ensureProspectiveTenantUser(TenantUserHolder tenant, Person person, VistaTenantBehavior behavior) {
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
            //TODO use tokens
            credential.credential().setValue(PasswordEncryptor.encryptPassword(person.email().getValue()));
            credential.enabled().setValue(Boolean.TRUE);
            credential.behaviors().add(behavior);
            Persistence.service().persist(credential);
        }
        return tenant.user();
    }

    public static boolean isTenantInSplitCharge(Application application, TenantUserHolder tenant) {
        EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
// TODO: make sure we get user from necessary application (if he participate in more than one):         
//        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
        TenantInLease tenantInLease = Persistence.service().retrieve(criteria);

        return !(tenantInLease.percentage().isNull() || tenantInLease.percentage().getValue() == 0);
    }

    private static void sendInvitationEmail(TenantUser user, Lease lease, EmailTemplateType emailTemplateType) {
        // Create Token and other stuff
        String token = AccessKey.createAccessToken(user, TenantUserCredential.class, 10);
        if (token == null) {
            throw new UserRuntimeException("Invalid user account");
        }

        MailMessage m = new MailMessage();
        m.setTo(user.email().getValue());
        m.setSender(MessageTemplates.getSender());
        // set email subject and body from the template
        MessageTemplates.createMasterApplicationInvitationEmail(m, user, emailTemplateType, lease, token);
        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }

    private static void sendApproveDeclineEmail(TenantUser user, TenantInLease tenantInLease, boolean isApproved) {
        // Create Token and other stuff
//        String token = AccessKey.createAccessToken(user, TenantUserCredential.class, 10);
//        if (token == null) {
//            throw new UserRuntimeException("Invalid user account");
//        }

        MailMessage m = new MailMessage();
        m.setTo(user.email().getValue());
        m.setSender(MessageTemplates.getSender());
        // set email subject and body from the template
        if (isApproved) {
            MessageTemplates.createApplicationApprovedEmail(m, tenantInLease);
        } else {
            MessageTemplates.createApplicationDeclinedEmail(m, tenantInLease);
        }

        if (MailDeliveryStatus.Success != Mail.send(m)) {
            throw new UserRuntimeException(i18n.tr("Mail Service Is Temporary Unavailable. Please Try Again Later"));
        }
    }
}
