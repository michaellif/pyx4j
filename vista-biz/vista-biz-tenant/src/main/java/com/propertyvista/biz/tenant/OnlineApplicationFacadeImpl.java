/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class OnlineApplicationFacadeImpl implements OnlineApplicationFacade {

    private static final I18n i18n = I18n.get(OnlineApplicationFacadeImpl.class);

    @Override
    public void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication) {
        masterOnlineApplication.status().setValue(MasterOnlineApplication.Status.Incomplete);
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(masterOnlineApplication);
        Persistence.service().persist(masterOnlineApplication);

        for (Tenant tenant : masterOnlineApplication.leaseApplication().lease().version().tenants()) {
            Persistence.service().retrieve(tenant);
            if (Tenant.Role.Applicant == tenant.role().getValue()) {
                if (tenant.customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Primary applicant must have an e-mail to start Online Application."));
                }
                createOnlineApplication(masterOnlineApplication, tenant);
                ServerSideFactory.create(CommunicationFacade.class).sendApplicantApplicationInvitation(tenant);
                return;
            }
        }
        throw new UserRuntimeException("Main applicant not found");
    }

    private void createOnlineApplication(MasterOnlineApplication masterOnlineApplication, Tenant tenant) {
        OnlineApplication app = EntityFactory.create(OnlineApplication.class);
        app.status().setValue(OnlineApplication.Status.Invited);
        app.steps().addAll(createApplicantApplicationProgress());

        app.masterOnlineApplication().set(masterOnlineApplication);
        app.customer().set(tenant.customer());
        Persistence.service().persist(app);

        tenant.application().set(app);
        Persistence.service().persist(tenant);
    }

    @Override
    public List<OnlineApplication> getOnlineApplications(CustomerUser customerUser) {
        Customer customer;
        {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), customerUser));
            customer = Persistence.service().retrieve(criteria);
            if (customer == null) {
                return null;
            }
        }

        {
            // See if active Application exists
            EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
            return Persistence.service().query(criteria);
        }
    }

    @Override
    public VistaCustomerBehavior getOnlineApplicationBehavior(OnlineApplication application) {
        MasterOnlineApplication masterOnlineApplication = Persistence.service().retrieve(MasterOnlineApplication.class,
                application.masterOnlineApplication().getPrimaryKey());

        Lease lease = Persistence.retrieveDraft(Lease.class, masterOnlineApplication.leaseApplication().lease().getPrimaryKey());
        for (Tenant tenant : lease.version().tenants()) {
            Persistence.service().retrieve(tenant);
            if (application.customer().equals(tenant.customer())) {

                switch (tenant.role().getValue()) {
                case Applicant:
                    if (application.status().getValue() == OnlineApplication.Status.Submitted) {
                        return VistaCustomerBehavior.ProspectiveSubmittedApplicant;
                    } else {
                        return VistaCustomerBehavior.ProspectiveApplicant;
                    }
                case CoApplicant:
                    if (application.status().getValue() == OnlineApplication.Status.Submitted) {
                        return VistaCustomerBehavior.ProspectiveSubmittedCoApplicant;
                    } else {
                        return VistaCustomerBehavior.ProspectiveCoApplicant;
                    }
                default:
                    return null;
                }
            }
        }
        for (Guarantor guarantor : lease.version().guarantors()) {
            Persistence.service().retrieve(guarantor);
            if (application.customer().equals(guarantor.customer())) {
                if (application.status().getValue() == OnlineApplication.Status.Submitted) {
                    return VistaCustomerBehavior.GuarantorSubmitted;
                } else {
                    return VistaCustomerBehavior.Guarantor;
                }
            }
        }

        return null;
    }

    @Override
    public void submitOnlineApplication(OnlineApplication application) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resendInvitationEmail(LeaseParticipant leaseParticipant) {
        // TODO Auto-generated method stub

    }

    // implementation internals

    private static ApplicationWizardStep createWizardStep(Class<? extends AppPlace> place, ApplicationWizardStep.Status status) {
        ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
        ws.placeId().setValue(AppPlaceInfo.getPlaceId(place));
        ws.status().setValue(status);
        return ws;
    }

    private static List<ApplicationWizardStep> createApplicantApplicationProgress() {
        List<ApplicationWizardStep> progress = new Vector<ApplicationWizardStep>();
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            for (Class<? extends AppPlace> place : Arrays.<Class<? extends AppPlace>> asList(//@formatter:off
                        PtSiteMap.WelcomeWizard.ReviewLease.class,
                        PtSiteMap.WelcomeWizard.MoveInSchedule.class,
                        PtSiteMap.WelcomeWizard.Insurance.class,
                        PtSiteMap.WelcomeWizard.Completion.class
                        
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
                progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
            }
            progress.add(createWizardStep(PtSiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
        }
        return progress;
    }

    private static List<ApplicationWizardStep> createCoApplicantApplicationProgress(Tenant tenant) {
        List<ApplicationWizardStep> progress = new Vector<ApplicationWizardStep>();
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
            if (isTenantInSplitCharge(tenant)) {
                progress.add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
            }
        }
        progress.add(createWizardStep(PtSiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
        return progress;
    }

    private static boolean isTenantInSplitCharge(Tenant tenant) {
        return !(tenant.percentage().isNull() || tenant.percentage().getValue() == 0);
    }

}
