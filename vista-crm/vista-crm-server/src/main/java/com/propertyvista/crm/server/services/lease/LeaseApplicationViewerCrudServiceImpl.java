/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-12
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonCreditCheck;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.util.LeaseParticipantUtils;
import com.propertyvista.server.common.util.TenantConverter;

public class LeaseApplicationViewerCrudServiceImpl extends LeaseViewerCrudServiceBaseImpl<LeaseApplicationDTO> implements LeaseApplicationViewerCrudService {

    private final static I18n i18n = I18n.get(LeaseApplicationViewerCrudServiceImpl.class);

    public LeaseApplicationViewerCrudServiceImpl() {
        super(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseApplicationDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(in, dto, retrieveTraget);
        enhanceRetrievedCommon(in, dto);

        for (Tenant tenantId : dto.currentTerm().version().tenants()) {
            loadLeaseParticipant(dto, tenantId);
        }

        for (Guarantor guarantorId : dto.currentTerm().version().guarantors()) {
            loadLeaseParticipant(dto, guarantorId);
        }

        ServerSideFactory.create(ScreeningFacade.class).calculateSuggestedDecision(
                dto.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue(), dto.leaseApproval());

        dto.masterApplicationStatus().set(
                ServerSideFactory.create(OnlineApplicationFacade.class).calculateOnlineApplicationStatus(dto.leaseApplication().onlineApplication()));
    }

    private void loadLeaseParticipant(LeaseApplicationDTO dto, LeaseParticipant<?> leaseParticipantId) {
        LeaseParticipant<?> leaseParticipant = (LeaseParticipant<?>) Persistence.service().retrieve(leaseParticipantId.getValueClass(),
                leaseParticipantId.getPrimaryKey());

        LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(leaseParticipant, AttachLevel.Attached);

        {
            Persistence.service().retrieve(leaseParticipant.leaseCustomer().customer().emergencyContacts());
            TenantInfoDTO tenantInfoDTO = new TenantConverter.LeaseParticipant2TenantInfo().createDTO(leaseParticipant);
            new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(leaseParticipant.effectiveScreening(), tenantInfoDTO);
            dto.tenantInfo().add(tenantInfoDTO);
        }

        {
            TenantFinancialDTO tenantFinancial = new TenantConverter.TenantFinancialEditorConverter().createDTO(leaseParticipant.effectiveScreening());
            tenantFinancial.person().set(leaseParticipant.leaseCustomer().customer().person());
            dto.tenantFinancials().add(tenantFinancial);
        }

        // approval data
        {
            LeaseParticipanApprovalDTO approval = EntityFactory.create(LeaseParticipanApprovalDTO.class);
            approval.leaseParticipant().set(leaseParticipant.duplicate());

            EntityQueryCriteria<PersonCreditCheck> criteria = EntityQueryCriteria.create(PersonCreditCheck.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().screening().screene(), leaseParticipant.leaseCustomer().customer()));
            criteria.desc(criteria.proto().creditCheckDate());
            approval.creditCheck().set(Persistence.service().retrieve(criteria));
            if (!approval.creditCheck().isNull()) {
                approval.screening().set(approval.creditCheck().screening());
                Persistence.service().retrieve(approval.screening(), AttachLevel.ToStringMembers);
            } else {
                approval.screening().set(leaseParticipant.effectiveScreening());
            }

            dto.leaseApproval().participants().add(approval);
        }
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceListRetrieved(in, dto);
        enhanceRetrievedCommon(in, dto);
    }

    private void enhanceRetrievedCommon(Lease in, LeaseApplicationDTO dto) {
        dto.numberOfOccupants().setValue(dto.currentTerm().version().tenants().size());
        dto.numberOfGuarantors().setValue(dto.currentTerm().version().guarantors().size());
        dto.numberOfApplicants().setValue(0);

        for (Tenant tenant : dto.currentTerm().version().tenants()) {
            Persistence.service().retrieve(tenant);
            Persistence.service().retrieve(tenant.screening(), AttachLevel.ToStringMembers);

            if (tenant.role().getValue() == LeaseParticipant.Role.Applicant) {
                dto.mainApplicant().set(tenant.leaseCustomer().customer());
                dto.numberOfApplicants().setValue(dto.numberOfApplicants().getValue() + 1);
            } else if (tenant.role().getValue() == LeaseParticipant.Role.CoApplicant) {
                dto.numberOfApplicants().setValue(dto.numberOfApplicants().getValue() + 1);
            }
        }
    }

    @Override
    public void startOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(EntityFactory.createIdentityStub(Lease.class, entityId));
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void inviteUsers(AsyncCallback<String> callback, Key entityId, Vector<LeaseParticipant<?>> users) {
        CommunicationFacade commFacade = ServerSideFactory.create(CommunicationFacade.class);
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No users to send invitation"));
        }

        // check that we can send the e-mail before we actually try to send email
        for (LeaseParticipant<?> user : users) {
            // check that all lease participants have an associated user entity (email)            
            if (user.leaseCustomer().customer().user().isNull()) {
                throw new UserRuntimeException(i18n.tr("Failed to invite users, email of lease participant {0} was not found", user.leaseCustomer().customer()
                        .person().name().getStringView()));
            }

            // check that selected guarantors/co-applicants have online-applications
            if (user.application().isNull()) {
                throw new UserRuntimeException(
                        i18n.tr("Failed to invite users, application invitation for {0} can be sent only after the main applicant will have finished his own applicaiton",
                                user.leaseCustomer().customer().person().name().getStringView()));
            }
        }

        for (LeaseParticipant<?> user : users) {
            if (user.isInstanceOf(Tenant.class)) {
                Tenant tenant = user.duplicate(Tenant.class);
                if (tenant.role().getValue() == LeaseParticipant.Role.Applicant) {
                    commFacade.sendApplicantApplicationInvitation(tenant);
                } else if (tenant.role().getValue() == LeaseParticipant.Role.CoApplicant) {
                    commFacade.sendCoApplicantApplicationInvitation(tenant);
                } else {
                    throw new Error("It's unknown what to do with tenant role " + tenant.role().getValue() + " in this context");
                }
            } else if (user.isInstanceOf(Guarantor.class)) {
                Guarantor guarantor = user.duplicate(Guarantor.class);
                commFacade.sendGuarantorApplicationInvitation(guarantor);
            }
        }

        Persistence.service().commit();

        String successMessage = users.size() > 1 ? i18n.tr("Invitations were sent successfully") : i18n.tr("Invitation was sent successfully");
        callback.onSuccess(successMessage);
    }

    @Override
    public void creditCheck(AsyncCallback<String> callback, Key entityId, BigDecimal creditCheckAmount, Vector<LeaseParticipant<?>> users) {
        for (LeaseParticipant<?> leaseParticipant : users) {
            ServerSideFactory.create(ScreeningFacade.class).runCreditCheck(creditCheckAmount, leaseParticipant);
        }
        String successMessage = i18n.tr("Credit check has been proceeded successfully.");
        callback.onSuccess(successMessage);
    }

    @Override
    public void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO) {

        switch (actionDTO.action().getValue()) {
        case Approve:
            ServerSideFactory.create(LeaseFacade.class).approveApplication(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
                    actionDTO.decisionReason().getValue());
            break;
        case Decline:
            ServerSideFactory.create(LeaseFacade.class).declineApplication(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
                    actionDTO.decisionReason().getValue());
            break;
        case Cancel:
            ServerSideFactory.create(LeaseFacade.class).cancelApplication(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
                    actionDTO.decisionReason().getValue());
            break;
        default:
            throw new IllegalArgumentException();
        }
        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
