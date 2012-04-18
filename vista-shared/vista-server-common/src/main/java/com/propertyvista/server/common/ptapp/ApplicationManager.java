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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.rpc.AuthorizationChangedSystemNotification;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.TenantUserHolder;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
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
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.common.security.VistaContext;
import com.propertyvista.server.domain.security.CustomerUserCredential;

// This class is in moving to OnlineApplicationFacade
@Deprecated
public class ApplicationManager {

    private static final I18n i18n = I18n.get(ApplicationManager.class);

    public static void sendApproveDeclineApplicationEmail(Lease lease, boolean isApproved) {
        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenantInLease : lease.version().tenants()) {
            OnlineApplication test = tenantInLease.application();
            if (!test.isNull()) { //co-applicants have no dedicated application
                ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenantInLease);
            }
        }
    }

    public static void makeApplicationCompleted(OnlineApplication application) {
        application.status().setValue(OnlineApplication.Status.Submitted);
        Persistence.service().persist(application);

        CustomerUser user = application.user();
        CustomerUserCredential credential = Persistence.service().retrieve(CustomerUserCredential.class, user.getPrimaryKey());
        boolean isApplicant = false;
        boolean isCoApplicant = false;
        boolean isGuarantor = false;

//        boolean isApplicant = credential.behaviors().contains(VistaCustomerBehavior.ProspectiveApplicant);
//        boolean isCoApplicant = credential.behaviors().contains(VistaCustomerBehavior.ProspectiveCoApplicant);
//        boolean isGuarantor = credential.behaviors().contains(VistaCustomerBehavior.Guarantor);
//
//        credential.behaviors().clear();
//        credential.behaviors().add(VistaCustomerBehavior.ProspectiveSubmitted);
//        if (isApplicant) {
//            credential.behaviors().add(VistaCustomerBehavior.ProspectiveSubmittedApplicant);
//        } else if (isGuarantor) {
//            credential.behaviors().add(VistaCustomerBehavior.GuarantorSubmitted);
//        } else if (isCoApplicant) {
//            credential.behaviors().add(VistaCustomerBehavior.ProspectiveSubmittedCoApplicant);
//        }

        Persistence.service().persist(credential);
        if (Context.isUserLoggedIn() && user.getPrimaryKey().equals(VistaContext.getCurrentUserPrimaryKey())) {
            Context.getVisit().setAclRevalidationRequired();
            Context.addResponseSystemNotification(new AuthorizationChangedSystemNotification());
        }

        MasterOnlineApplication ma = application.masterOnlineApplication();
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
                inviteUser(ma, personGuarantor.guarantor(), personGuarantor.guarantor().customer().person(), VistaCustomerBehavior.Guarantor);
            }
        }

        // Invite CoApplicants:
        if (isApplicant) {
            Persistence.service().retrieve(ma.lease().version().tenants());
            for (Tenant tenantInLease : ma.lease().version().tenants()) {
                if ((Tenant.Role.CoApplicant == tenantInLease.role().getValue() && (!tenantInLease.takeOwnership().isBooleanTrue()))) {
                    tenantInLease.application().set(
                            inviteUser(ma, tenantInLease.customer(), tenantInLease.customer().person(), VistaCustomerBehavior.ProspectiveCoApplicant));
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

    public static OnlineApplication inviteUser(MasterOnlineApplication ma, TenantUserHolder tenant, Person person, VistaCustomerBehavior behaviour) {
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

            application.masterOnlineApplication().set(ma);
            application.lease().set(ma.lease());
            //application.status().setValue(OnlineMasterApplication.Status.Created);
            //application.steps().addAll(ApplicationManager.createApplicationProgress(application, tenant, behaviour));
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
        Tenant tenant1 = null;
        Guarantor guarantor = null;
        switch (behaviour) {
        case ProspectiveApplicant:
            ServerSideFactory.create(CommunicationFacade.class).sendApplicantApplicationInvitation(tenant1);
            break;
        case ProspectiveCoApplicant:
            ServerSideFactory.create(CommunicationFacade.class).sendCoApplicantApplicationInvitation(tenant1);
            break;
        case Guarantor:
            ServerSideFactory.create(CommunicationFacade.class).sendGuarantorApplicationInvitation(guarantor);
            break;
        }

        // update app status:
        application.status().setValue(OnlineApplication.Status.Invited);
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
            status.progress().setValue(0d);

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

    public static CustomerUser ensureProspectiveTenantUser(TenantUserHolder tenant, Person person, VistaCustomerBehavior behavior) {
        CustomerUser user = tenant.user();
        if (user.getPrimaryKey() == null) {
            if (person.email().isNull()) {
                throw new UnRecoverableRuntimeException(i18n.tr("Can't create application user for tenant  {0} without e-mail address", person.name()
                        .getStringView()));
            }
            user.name().setValue(person.name().getStringView());
            user.email().setValue(person.email().getValue());
            Persistence.service().persist(user);
            Persistence.service().persist(tenant);

            CustomerUserCredential credential = EntityFactory.create(CustomerUserCredential.class);
            credential.setPrimaryKey(user.getPrimaryKey());

            credential.user().set(user);
            if (ApplicationMode.isDevelopment()) {
                credential.credential().setValue(PasswordEncryptor.encryptPassword(person.email().getValue()));
            }
            credential.enabled().setValue(Boolean.TRUE);
            // TODO tenant can be guarantor in other applications.
            Persistence.service().persist(credential);
        } else {
            CustomerUserCredential credential = Persistence.service().retrieve(CustomerUserCredential.class, user.getPrimaryKey());
            credential.setPrimaryKey(user.getPrimaryKey());
            credential.user().set(user);
            if (ApplicationMode.isDevelopment()) {
                credential.credential().setValue(PasswordEncryptor.encryptPassword(person.email().getValue()));
            }
            credential.enabled().setValue(Boolean.TRUE);
            // TODO tenant can be guarantor in other applications.
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

}
