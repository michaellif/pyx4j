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

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
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
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplicationStatus;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication.Role;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationStatus;

public class OnlineApplicationFacadeImpl implements OnlineApplicationFacade {

    private static final I18n i18n = I18n.get(OnlineApplicationFacadeImpl.class);

    @Override
    public void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication, Building building, Floorplan floorplan) {
        masterOnlineApplication.status().setValue(MasterOnlineApplication.Status.Incomplete);
        masterOnlineApplication.building().set(building);
        masterOnlineApplication.floorplan().set(floorplan);
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(masterOnlineApplication);
        Persistence.service().persist(masterOnlineApplication);

        for (LeaseTermTenant tenant : masterOnlineApplication.leaseApplication().lease().currentTerm().version().tenants()) {
            Persistence.service().retrieve(tenant);
            if (LeaseTermParticipant.Role.Applicant == tenant.role().getValue()) {
                if (tenant.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Primary applicant must have an e-mail to start Online Application."));
                }
                tenant.application().set(createOnlineApplication(masterOnlineApplication, tenant, Role.Applicant));

                if (masterOnlineApplication.leaseApplication().lease().unit().isNull()) {
                    ServerSideFactory.create(CommunicationFacade.class).sendProspectWelcome(tenant);
                } else {
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicantApplicationInvitation(tenant);
                }
                Persistence.service().persist(tenant);
                return;
            }
        }
        throw new UserRuntimeException("Main applicant not found");
    }

    @Override
    public void approveMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication) {
        Persistence.ensureRetrieve(masterOnlineApplication, AttachLevel.Attached);

        if (!masterOnlineApplication.status().isNull()) {
            masterOnlineApplication.status().setValue(MasterOnlineApplication.Status.Approved);

            Persistence.service().persist(masterOnlineApplication);
        }
    }

    @Override
    public List<OnlineApplication> getOnlineApplications(CustomerUser customerUser) {
        Validate.isFalse(customerUser.isNull(), "Custiomer User can't be null");

        // See if active Application exists
        EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
        criteria.eq(criteria.proto().customer().user(), customerUser);
        return Persistence.service().query(criteria);
    }

    @Override
    public EnumSet<PortalProspectBehavior> getOnlineApplicationBehavior(OnlineApplication application) {
        EnumSet<PortalProspectBehavior> retVal = EnumSet.noneOf(PortalProspectBehavior.class);

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
            if (moa != null && (!moa.building().isNull() || !moa.floorplan().isNull())) {
                retVal.add(PortalProspectBehavior.CanEditLeaseTerms);
            }
        }

        return retVal;
    }

    @Override
    public void submitOnlineApplication(OnlineApplication application) {
        application.status().setValue(OnlineApplication.Status.Submitted);
        Persistence.service().persist(application);

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

        MasterOnlineApplication ma = application.masterOnlineApplication();
        Persistence.service().retrieve(ma);
        Persistence.service().retrieve(ma.leaseApplication().lease());

        // Invite customers:
        switch (application.role().getValue()) {
        case Applicant:
            inviteCoApplicants(ma.leaseApplication().lease());
            inviteGuarantors(ma.leaseApplication().lease(), application.customer());
            break;
        case CoApplicant:
            inviteGuarantors(ma.leaseApplication().lease(), application.customer());
            break;
        case Guarantor:
            break;
        }

        // check application completeness:
        boolean allApplicationsSubmited = true;
        Persistence.service().retrieve(ma.applications());
        for (OnlineApplication app : ma.applications()) {
            if (app.status().getValue() != OnlineApplication.Status.Submitted) {
                allApplicationsSubmited = false;
                break;
            }
        }
        if (allApplicationsSubmited) {
            ma.status().setValue(MasterOnlineApplication.Status.Submitted);
            Persistence.service().persist(ma);
            ma.leaseApplication().status().setValue(LeaseApplication.Status.PendingDecision);
            Persistence.service().persist(ma.leaseApplication());
        }
    }

    @Override
    public void resendInvitationEmail(LeaseTermParticipant leaseParticipant) {
        // TODO Auto-generated method stub

    }

    @Override
    public MasterOnlineApplicationStatus calculateOnlineApplicationStatus(MasterOnlineApplication moa) {
        if (moa == null) {
            return null;
        }

        if (moa.isValueDetached()) {
            Persistence.service().retrieve(moa);
        }

        MasterOnlineApplicationStatus moaStatus = EntityFactory.create(MasterOnlineApplicationStatus.class);
        BigDecimal progressSum = new BigDecimal("0.0");

        moaStatus.status().setValue(moa.status().getValue());

        for (OnlineApplication app : moa.applications()) {
            if (app.isValueDetached()) {
                Persistence.service().retrieve(app);
            }

            OnlineApplicationStatus status = EntityFactory.create(OnlineApplicationStatus.class);
            status.status().setValue(app.status().getValue());

            status.customer().set(app.customer());
            status.role().set(app.role());

            // calculate progress:
            status.progress().setValue(app.progress().getValue());

            moaStatus.individualApplications().add(status);

            progressSum = progressSum.add(status.progress().getValue());
        }

        if (!moa.applications().isEmpty()) {
            moaStatus.progress().setValue(progressSum.divide(new BigDecimal(moa.applications().size())));
        }

        return moaStatus;
    }

    // implementation internals

    private void inviteCoApplicants(Lease lease) {
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            if ((tenant.role().getValue() == LeaseTermParticipant.Role.CoApplicant)) {
                if (tenant.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Co-Applicant must have an e-mail to start Online Application."));
                }
                tenant.application().set(createOnlineApplication(lease.leaseApplication().onlineApplication(), tenant, Role.CoApplicant));
                ServerSideFactory.create(CommunicationFacade.class).sendCoApplicantApplicationInvitation(tenant);
                Persistence.service().persist(tenant);
            }
        }
    }

    private void inviteGuarantors(Lease lease, Customer tenant) {
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermGuarantor guarantor : lease.currentTerm().version().guarantors()) {
            if (guarantor.tenant().customer().equals(tenant)) {
                if (guarantor.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Guarantor must have an e-mail to start Online Application."));
                }
                guarantor.application().set(createOnlineApplication(lease.leaseApplication().onlineApplication(), guarantor, Role.Guarantor));
                ServerSideFactory.create(CommunicationFacade.class).sendGuarantorApplicationInvitation(guarantor);
                Persistence.service().persist(guarantor);
            }
        }
    }

    private OnlineApplication createOnlineApplication(MasterOnlineApplication masterOnlineApplication, LeaseTermParticipant<?> participant, Role role) {
        OnlineApplication app = EntityFactory.create(OnlineApplication.class);
        app.status().setValue(OnlineApplication.Status.Invited);

        // create empty new screening if null:
        if (participant.screening().isNull()) {
            participant.screening().set(EntityFactory.create(CustomerScreening.class));
            Persistence.service().persist(participant);
        }

        app.masterOnlineApplication().set(masterOnlineApplication);
        app.customer().set(participant.leaseParticipant().customer());
        app.role().setValue(role);
        app.progress().setValue(BigDecimal.ZERO);

        Persistence.service().persist(app);
        return app;
    }

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
}
