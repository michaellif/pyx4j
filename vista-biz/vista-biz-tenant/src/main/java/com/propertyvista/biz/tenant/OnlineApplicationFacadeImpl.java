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
 */
package com.propertyvista.biz.tenant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.policy.policies.LeaseApplicationLegalPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationConfirmationTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm;
import com.propertyvista.domain.policy.policies.domain.LeaseApplicationLegalTerm.TargetRole;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.PortalProspectBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.ProspectSignUp;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepStatus;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationConfirmationTerm;
import com.propertyvista.domain.tenant.prospect.SignedOnlineApplicationLegalTerm;

public class OnlineApplicationFacadeImpl implements OnlineApplicationFacade {

    private static final I18n i18n = I18n.get(OnlineApplicationFacadeImpl.class);

    @Override
    public void prospectSignUp(ProspectSignUp request) {
        // Minimal Validation first
        Validate.isFalse(request.firstName().isNull(), "First name required");
        Validate.isFalse(request.lastName().isNull(), "Last name required");
        Validate.isFalse(request.email().isNull(), "Email required");

        request.email().setValue(EmailValidator.normalizeEmailAddress(request.email().getValue()));
        {
            EntityQueryCriteria<CustomerUser> criteria = EntityQueryCriteria.create(CustomerUser.class);
            criteria.eq(criteria.proto().email(), request.email());
            if (Persistence.service().count(criteria) > 0) {
                throw new UserRuntimeException(true, i18n.tr("E-mail address already registered, please login to your account"));
            }
        }
        // Validate Building and floorplan
        Building building;
        Floorplan floorplan = null;
        AptUnit unit = null;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().propertyCode(), request.ilsBuildingId());
            criteria.eq(criteria.proto().suspended(), false);
            building = Persistence.service().retrieve(criteria);
            Validate.notNull(building, "building {0} required or not found", request.ilsBuildingId());
        }
        if (!request.ilsFloorplanId().isNull()) {
            EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
            criteria.eq(criteria.proto().name(), request.ilsFloorplanId());
            criteria.eq(criteria.proto().building(), building);
            floorplan = Persistence.service().retrieve(criteria);
        }
        if (!request.ilsUnitId().isNull()) {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.eq(criteria.proto().info().number(), request.ilsUnitId());
            criteria.eq(criteria.proto().building(), building);
            unit = Persistence.service().retrieve(criteria);
            Validate.notNull(unit, "unit {0} {1} not found", request.ilsBuildingId(), request.ilsUnitId());
        }

        //Start application creation
        Lease lease = ServerSideFactory.create(LeaseFacade.class).create(Status.Application);
        if (unit != null) {
            ServerSideFactory.create(LeaseFacade.class).setUnit(lease, unit);
        }

        LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().$();
        lease.currentTerm().version().tenants().add(mainTenant);

        mainTenant.leaseParticipant().customer().person().name().firstName().setValue(request.firstName().getValue());
        mainTenant.leaseParticipant().customer().person().name().middleName().setValue(request.middleName().getValue());
        mainTenant.leaseParticipant().customer().person().name().lastName().setValue(request.lastName().getValue());
        mainTenant.leaseParticipant().customer().person().email().setValue(request.email().getValue());

        mainTenant.role().setValue(LeaseTermParticipant.Role.Applicant);

        ServerSideFactory.create(LeaseFacade.class).persist(lease);

        ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(lease, building, floorplan);

        ServerSideFactory.create(CustomerFacade.class).setCustomerPassword(mainTenant.leaseParticipant().customer(), request.password().getValue());
    }

    @Override
    public void createMasterOnlineApplication(MasterOnlineApplication masterApplication, Building building, Floorplan floorplan) {
        Persistence.ensureRetrieve(masterApplication, AttachLevel.Attached);

        masterApplication.status().setValue(MasterOnlineApplication.Status.Incomplete);
        masterApplication.ilsBuilding().set(building);
        masterApplication.ilsFloorplan().set(floorplan);
        initOnlineApplicationFeeData(masterApplication);
        Persistence.service().merge(masterApplication);

        for (LeaseTermTenant tenant : masterApplication.leaseApplication().lease().currentTerm().version().tenants()) {
            Persistence.ensureRetrieve(tenant, AttachLevel.Attached);

            if (LeaseTermParticipant.Role.Applicant == tenant.role().getValue()) {
                if (tenant.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Primary applicant must have an e-mail to start Online Application."));
                }
                tenant.application().set(createOnlineApplication(masterApplication, tenant, LeaseTermParticipant.Role.Applicant));

                if (masterApplication.leaseApplication().lease().unit().isNull()) {
                    ServerSideFactory.create(CommunicationFacade.class).sendProspectWelcome(tenant);
                } else {
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicantApplicationInvitation(tenant);
                }
                Persistence.service().merge(tenant);
                return;
            }
        }
        throw new UserRuntimeException("Main applicant not found");
    }

    @Override
    public void approveMasterOnlineApplication(MasterOnlineApplication masterApplication) {
        Persistence.ensureRetrieve(masterApplication, AttachLevel.Attached);

        if (!masterApplication.status().isNull()) {
            masterApplication.status().setValue(MasterOnlineApplication.Status.Approved);
            Persistence.service().merge(masterApplication);
        }
    }

    @Override
    public void cancelMasterOnlineApplication(MasterOnlineApplication masterApplication) {
        Persistence.ensureRetrieve(masterApplication, AttachLevel.Attached);

        if (!masterApplication.status().isNull()) {
            masterApplication.status().setValue(MasterOnlineApplication.Status.Cancelled);
            Persistence.service().persist(masterApplication);

            for (OnlineApplication application : retrieveActiveApplications(masterApplication)) {
                application.status().setValue(OnlineApplication.Status.Cancelled);
                Persistence.service().merge(application);
            }
        }
    }

    @Override
    public void submitOnlineApplication(OnlineApplication application) {
        application.status().setValue(OnlineApplication.Status.Submitted);
        application.submitDate().setValue(SystemDateManager.getLogicalDate());
        Persistence.service().merge(application);

        // TODO: update behavior somehow:
        //            CustomerUser user = application.customer().user();
        //            CustomerUserCredential credential = Persistence.service().retrieve(CustomerUserCredential.class, user.getPrimaryKey());
        //            boolean isApplicant = false;
        //            boolean isCoApplicant = false;
        //            boolean isGuarantor = false;
        //
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
        //            Persistence.service().persist(credential);

        MasterOnlineApplication masterApplication = application.masterOnlineApplication();
        Persistence.service().retrieve(masterApplication);
        Persistence.service().retrieve(masterApplication.leaseApplication().lease());
        masterApplication.leaseApplication().lease().currentTerm()
                .set(Persistence.service().retrieve(LeaseTerm.class, masterApplication.leaseApplication().lease().currentTerm().getPrimaryKey().asDraftKey()));

        // Invite customers:
        switch (application.role().getValue()) {
        case Applicant:
            inviteCoApplicants(masterApplication.leaseApplication().lease());
            inviteGuarantors(masterApplication.leaseApplication().lease(), application.customer());
            break;
        case CoApplicant:
            inviteGuarantors(masterApplication.leaseApplication().lease(), application.customer());
            break;
        case Guarantor:
            break;
        }

        // check application completeness:

        boolean allApplicationsSubmited = true;
        for (OnlineApplication app : retrieveActiveApplications(masterApplication)) {
            if (app.status().getValue() != OnlineApplication.Status.Submitted) {
                allApplicationsSubmited = false;
                break;
            }
        }

        if (allApplicationsSubmited) {
            masterApplication.status().setValue(MasterOnlineApplication.Status.Submitted);
            Persistence.service().merge(masterApplication);

            ServerSideFactory.create(LeaseFacade.class).submitApplication(masterApplication.leaseApplication().lease(), null, null);
        }
    }

    @Override
    public void resendInvitationEmail(LeaseTermParticipant<?> leaseParticipant) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<OnlineApplication> getOnlineApplications(CustomerUser customerUser) {
        Validate.isFalse(customerUser.isNull(), "Customer User can't be null");

        // See if active Application exists
        EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);

        criteria.eq(criteria.proto().customer().user(), customerUser);
        criteria.ne(criteria.proto().status(), OnlineApplication.Status.Cancelled);
        criteria.in(criteria.proto().masterOnlineApplication().status(), MasterOnlineApplication.Status.inProgress());
        criteria.in(criteria.proto().masterOnlineApplication().leaseApplication().status(), LeaseApplication.Status.draft());
        criteria.eq(criteria.proto().masterOnlineApplication().leaseApplication().lease().status(), Lease.Status.Application);

        List<OnlineApplication> applications = Persistence.service().query(criteria);

        // Check for suspended building.
        for (Iterator<OnlineApplication> it = applications.iterator(); it.hasNext();) {
            OnlineApplication app = it.next();
            Building building = getOnlineApplicationPolicyNode(app);
            Persistence.ensureRetrieve(building, AttachLevel.Attached);
            if (building.suspended().getValue(false)) {
                it.remove();
            }
        }

        return applications;
    }

    @Override
    public Collection<PortalProspectBehavior> getOnlineApplicationBehavior(OnlineApplication application) {
        Persistence.ensureRetrieve(application.customer(), AttachLevel.Attached);

        Collection<PortalProspectBehavior> retVal = new HashSet<>();

        {
            EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
            criteria.eq(criteria.proto().leaseTermV().holder().lease().leaseApplication().onlineApplication().applications(), application);
            criteria.eq(criteria.proto().leaseParticipant().customer(), application.customer());
            for (LeaseTermTenant tenant : Persistence.service().query(criteria)) {
                switch (tenant.role().getValue()) {
                case Applicant:
                    retVal.add(PortalProspectBehavior.Applicant);
                case CoApplicant:
                    retVal.add(PortalProspectBehavior.CoApplicant);
                }
            }
        }
        {
            EntityQueryCriteria<LeaseTermGuarantor> criteria = EntityQueryCriteria.create(LeaseTermGuarantor.class);
            criteria.eq(criteria.proto().leaseTermV().holder().lease().leaseApplication().onlineApplication().applications(), application);
            criteria.eq(criteria.proto().leaseParticipant().customer(), application.customer());
            if (Persistence.service().exists(criteria)) {
                retVal.add(PortalProspectBehavior.Guarantor);
            }
        }

        if (retVal.contains(PortalProspectBehavior.Applicant)) {
            EntityQueryCriteria<MasterOnlineApplication> criteria = EntityQueryCriteria.create(MasterOnlineApplication.class);
            criteria.eq(criteria.proto().applications(), application);
            MasterOnlineApplication moa = Persistence.service().retrieve(criteria);
            if (moa != null && (!moa.ilsBuilding().isNull() || !moa.ilsFloorplan().isNull())) {
                retVal.add(PortalProspectBehavior.CanEditLeaseTerms);
            }
        }

        return retVal;
    }

    @Override
    public List<SignedOnlineApplicationLegalTerm> getOnlineApplicationLegalTerms(OnlineApplication application) {
        Building policyNode = getOnlineApplicationPolicyNode(application);

        LeaseApplicationLegalPolicy leaseApplicationPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode,
                LeaseApplicationLegalPolicy.class);
        List<SignedOnlineApplicationLegalTerm> terms = new ArrayList<SignedOnlineApplicationLegalTerm>();
        for (LeaseApplicationLegalTerm term : leaseApplicationPolicy.legalTerms()) {
            TargetRole termRole = term.applyToRole().getValue();
            if (termRole.matchesApplicationRole(application.role().getValue())) {
                SignedOnlineApplicationLegalTerm signedTerm = EntityFactory.create(SignedOnlineApplicationLegalTerm.class);
                signedTerm.term().set(term);
                signedTerm.signature().signatureFormat().set(term.signatureFormat());
                terms.add(signedTerm);
            }
        }
        return terms;
    }

    @Override
    public List<SignedOnlineApplicationConfirmationTerm> getOnlineApplicationConfirmationTerms(OnlineApplication application) {
        Building building = getOnlineApplicationPolicyNode(application);

        List<SignedOnlineApplicationConfirmationTerm> terms = new ArrayList<SignedOnlineApplicationConfirmationTerm>();

        LeaseApplicationLegalPolicy leaseApplicationPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building,
                LeaseApplicationLegalPolicy.class);
        for (LeaseApplicationConfirmationTerm term : leaseApplicationPolicy.confirmationTerms()) {
            TargetRole termRole = term.applyToRole().getValue();
            if (termRole.matchesApplicationRole(application.role().getValue())) {
                SignedOnlineApplicationConfirmationTerm signedTerm = EntityFactory.create(SignedOnlineApplicationConfirmationTerm.class);
                signedTerm.term().set(term);
                signedTerm.signature().signatureFormat().set(term.signatureFormat());
                terms.add(signedTerm);
            }
        }
        return terms;
    }

    @Override
    public Building getOnlineApplicationPolicyNode(OnlineApplication application) {
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().units().$().leases().$().leaseApplication().onlineApplication(), application.masterOnlineApplication());
            building = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
        }
        if (building != null) {
            return building;
        } else {
            // Case of ILS link
            Persistence.ensureRetrieve(application.masterOnlineApplication().ilsBuilding(), AttachLevel.IdOnly);
            return application.masterOnlineApplication().ilsBuilding();
        }
    }

    @Override
    public MasterOnlineApplicationStatus calculateOnlineApplicationStatus(MasterOnlineApplication masterApplication) {
        if (masterApplication == null) {
            return null; // no online application was run!..
        }

        Persistence.ensureRetrieve(masterApplication, AttachLevel.Attached);
        List<OnlineApplication> applications = retrieveActiveApplications(masterApplication);

        MasterOnlineApplicationStatus moaStatus = EntityFactory.create(MasterOnlineApplicationStatus.class);

        moaStatus.status().setValue(masterApplication.status().getValue());

        BigDecimal progressSum = new BigDecimal("0.0");
        for (OnlineApplication application : applications) {
            Persistence.ensureRetrieve(application, AttachLevel.Attached);

            OnlineApplicationStatus status = EntityFactory.create(OnlineApplicationStatus.class);

            status.status().setValue(application.status().getValue());
            status.customer().set(application.customer());
            status.role().setValue(application.role().getValue());

            // calculate progress:
            status.progress().setValue(calculateProgress(application));
            status.daysOpen().setValue((SystemDateManager.getLogicalDate().getTime() - application.createDate().getValue().getTime()) / (1000 * 60 * 60 * 24));

            moaStatus.individualApplications().add(status);

            progressSum = progressSum.add(status.progress().getValue());
        }

        if (!applications.isEmpty()) {
            moaStatus.progress().setValue(progressSum.divide(new BigDecimal(applications.size()), 2, RoundingMode.HALF_UP));
        }

        return moaStatus;
    }

    // implementation internals

    @Override
    public void initOnlineApplicationFeeData(MasterOnlineApplication masterApplication) {
        Building building = masterApplication.ilsBuilding().duplicate();

        if (building.isNull()) {
            Persistence.ensureRetrieve(masterApplication.leaseApplication().lease().unit().building(), AttachLevel.IdOnly);
            building = masterApplication.leaseApplication().lease().unit().building();
        }
        if (!building.isNull()) {
            ProspectPortalPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ProspectPortalPolicy.class);
            masterApplication.feePayment().setValue(policy.feePayment().getValue());
            masterApplication.feeAmount().setValue(policy.feeAmount().getValue());
        }
    }

    private BigDecimal calculateProgress(OnlineApplication application) {
        switch (application.status().getValue()) {
        case Invited:
            return BigDecimal.ZERO;
        case Incomplete:
            if (application.stepsStatuses().isNull()) {
                return BigDecimal.ZERO;
            }

            Persistence.ensureRetrieve(application.masterOnlineApplication().leaseApplication().lease(), AttachLevel.Attached);
            if (!Lease.Status.isApplicationUnitSelected(application.masterOnlineApplication().leaseApplication().lease())) {
                return BigDecimal.ZERO;
            }

            BigDecimal sum = BigDecimal.ZERO;
            for (OnlineApplicationWizardStepStatus stepStatus : application.stepsStatuses()) {
                if (stepStatus.completed().getValue(false)) {
                    sum = sum.add(new BigDecimal(1));
                } else if (stepStatus.visited().getValue(false)) {
                    sum = sum.add(new BigDecimal(0.5));
                }
            }
            return sum.divide(new BigDecimal(application.stepsStatuses().size()), 2, RoundingMode.CEILING);
        case InformationRequested:
            return new BigDecimal(1);
        case Submitted:
            return new BigDecimal(1);
        default:
            return BigDecimal.ZERO;
        }
    }

    private void inviteCoApplicants(Lease lease) {
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            if ((tenant.role().getValue() == LeaseTermParticipant.Role.CoApplicant)) {
                if (tenant.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Co-Applicant must have an e-mail to start Online Application."));
                }
                tenant.application().set(createOnlineApplication(lease.leaseApplication().onlineApplication(), tenant, LeaseTermParticipant.Role.CoApplicant));
                ServerSideFactory.create(CommunicationFacade.class).sendCoApplicantApplicationInvitation(tenant);
                Persistence.service().merge(tenant);
            }
        }
    }

    private void inviteGuarantors(Lease lease, Customer tenant) {
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        for (LeaseTermGuarantor guarantor : lease.currentTerm().version().guarantors()) {
            if (guarantor.tenant().customer().equals(tenant)) {
                if (guarantor.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Guarantor must have an e-mail to start Online Application."));
                }
                guarantor.application().set(
                        createOnlineApplication(lease.leaseApplication().onlineApplication(), guarantor, LeaseTermParticipant.Role.Guarantor));
                ServerSideFactory.create(CommunicationFacade.class).sendGuarantorApplicationInvitation(guarantor);
                Persistence.service().merge(guarantor);
            }
        }
    }

    private OnlineApplication createOnlineApplication(MasterOnlineApplication masterApplication, LeaseTermParticipant<?> participant,
            LeaseTermParticipant.Role role) {
        OnlineApplication application = EntityFactory.create(OnlineApplication.class);

        application.masterOnlineApplication().set(masterApplication);
        application.status().setValue(OnlineApplication.Status.Invited);
        application.customer().set(participant.leaseParticipant().customer());
        application.role().setValue(role);

        Persistence.service().persist(application);

        // ensure participant screening is present:
        if (participant.screening().isNull()) {
            participant.screening().set(EntityFactory.create(CustomerScreening.class));
            Persistence.service().merge(participant);
        }

        return application;
    }

    private List<OnlineApplication> retrieveActiveApplications(MasterOnlineApplication masterApplication) {
        EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);

        criteria.eq(criteria.proto().masterOnlineApplication(), masterApplication);
        criteria.ne(criteria.proto().status(), OnlineApplication.Status.Cancelled);

        return Persistence.service().query(criteria);
    }
}
