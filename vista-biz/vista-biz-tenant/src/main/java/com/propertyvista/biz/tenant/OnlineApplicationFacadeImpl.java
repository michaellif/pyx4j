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
import java.util.List;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.MasterOnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.domain.tenant.prospect.OnlineApplication.Role;
import com.propertyvista.dto.MasterOnlineApplicationOnlineStatusDTO;
import com.propertyvista.dto.OnlineApplicationStatusDTO;

public class OnlineApplicationFacadeImpl implements OnlineApplicationFacade {

    private static final I18n i18n = I18n.get(OnlineApplicationFacadeImpl.class);

    @Override
    public void createMasterOnlineApplication(MasterOnlineApplication masterOnlineApplication) {
        masterOnlineApplication.status().setValue(MasterOnlineApplication.Status.Incomplete);
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(masterOnlineApplication);
        Persistence.service().persist(masterOnlineApplication);

        for (LeaseTermTenant tenant : masterOnlineApplication.leaseApplication().lease().currentTerm().version().tenants()) {
            Persistence.service().retrieve(tenant);
            if (LeaseTermParticipant.Role.Applicant == tenant.role().getValue()) {
                if (tenant.leaseParticipant().customer().user().isNull()) {
                    throw new UserRuntimeException(i18n.tr("Primary applicant must have an e-mail to start Online Application."));
                }
                tenant.application().set(createOnlineApplication(masterOnlineApplication, tenant, Role.Applicant));
                ServerSideFactory.create(CommunicationFacade.class).sendApplicantApplicationInvitation(tenant);
                Persistence.service().persist(tenant);
                return;
            }
        }
        throw new UserRuntimeException("Main applicant not found");
    }

    @Override
    public List<OnlineApplication> getOnlineApplications(CustomerUser customerUser) {
        Validate.isTrue(customerUser.isNull(), "Custiomer User can't be null");

        // See if active Application exists
        EntityQueryCriteria<OnlineApplication> criteria = EntityQueryCriteria.create(OnlineApplication.class);
        criteria.eq(criteria.proto().customer().user(), customerUser);
        return Persistence.service().query(criteria);
    }

    @Override
    public VistaCustomerBehavior getOnlineApplicationBehavior(OnlineApplication application) {
        EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
        criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease().leaseApplication().onlineApplication().applications(), application));
        LeaseTerm leaseTerm = Persistence.service().retrieve(criteria);

        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            Persistence.service().retrieve(tenant);
            if (application.customer().equals(tenant.leaseParticipant().customer())) {

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
        for (LeaseTermGuarantor guarantor : leaseTerm.version().guarantors()) {
            Persistence.service().retrieve(guarantor);
            if (application.customer().equals(guarantor.leaseParticipant().customer())) {
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
        application.status().setValue(OnlineApplication.Status.Submitted);
        Persistence.service().persist(application);

// TODO: update behaviour somehow: 
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
    public MasterOnlineApplicationOnlineStatusDTO calculateOnlineApplicationStatus(MasterOnlineApplication ma) {
        if (ma == null) {
            return null;
        }

        if (ma.isValueDetached()) {
            Persistence.service().retrieve(ma);
        }

        MasterOnlineApplicationOnlineStatusDTO maStatus = EntityFactory.create(MasterOnlineApplicationOnlineStatusDTO.class);
        BigDecimal progressSum = new BigDecimal("0.0");

        for (OnlineApplication app : ma.applications()) {
            if (app.isValueDetached()) {
                Persistence.service().retrieve(app);
            }

            OnlineApplicationStatusDTO status = EntityFactory.create(OnlineApplicationStatusDTO.class);
            status.status().setValue(app.status().getValue());

            status.person().set(app.customer().person().name());
            status.role().set(app.role());

            // calculate progress:
            status.progress().setValue(app.progress().getValue());

            if (!status.person().isEmpty()) {
                maStatus.individualApplications().add(status);
            }

            progressSum = progressSum.add(status.progress().getValue());
        }

        maStatus.progress().setValue(progressSum.divide(new BigDecimal(ma.applications().size())));
        return maStatus;
    }

    // implementation internals

    private void inviteCoApplicants(Lease lease) {
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            if ((tenant.role().getValue() == LeaseTermParticipant.Role.CoApplicant && (!tenant.takeOwnership().isBooleanTrue()))) {
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

    private OnlineApplication createOnlineApplication(MasterOnlineApplication masterOnlineApplication, LeaseTermParticipant participant, Role role) {
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
        Persistence.service().persist(app);
        return app;
    }

}
