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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
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
    protected void enhanceRetrieved(Lease lease, LeaseApplicationDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(lease, dto, retrieveTraget);
        enhanceRetrievedCommon(lease, dto);

        for (LeaseTermTenant tenantId : dto.currentTerm().version().tenants()) {
            loadLeaseParticipant(lease, dto, tenantId);
        }

        for (LeaseTermGuarantor guarantorId : dto.currentTerm().version().guarantors()) {
            loadLeaseParticipant(lease, dto, guarantorId);
        }

        ServerSideFactory.create(ScreeningFacade.class).calculateSuggestedDecision(
                ServerSideFactory.create(BillingFacade.class).getMaxLeaseTermMonthlyTotal(dto.currentTerm()), dto.leaseApproval());

        dto.masterApplicationStatus().set(
                ServerSideFactory.create(OnlineApplicationFacade.class).calculateOnlineApplicationStatus(dto.leaseApplication().onlineApplication()));
    }

    private void loadLeaseParticipant(Lease lease, LeaseApplicationDTO dto, LeaseTermParticipant<?> leaseParticipantId) {
        LeaseTermParticipant<?> leaseParticipant = (LeaseTermParticipant<?>) Persistence.service().retrieve(leaseParticipantId.getValueClass(),
                leaseParticipantId.getPrimaryKey());

        LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(lease, leaseParticipant, AttachLevel.Attached);

        {
            Persistence.service().retrieve(leaseParticipant.leaseParticipant().customer().emergencyContacts());
            TenantInfoDTO tenantInfoDTO = new TenantConverter.LeaseParticipant2TenantInfo().createDTO(leaseParticipant);
            new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(leaseParticipant.effectiveScreening(), tenantInfoDTO);
            dto.tenantInfo().add(tenantInfoDTO);
        }

        {
            TenantFinancialDTO tenantFinancialDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(leaseParticipant.effectiveScreening());
            tenantFinancialDTO.person().set(leaseParticipant.leaseParticipant().customer().person());
            dto.tenantFinancials().add(fillQuickSummary(tenantFinancialDTO));
        }

        // approval data
        {
            LeaseParticipanApprovalDTO approval = EntityFactory.create(LeaseParticipanApprovalDTO.class);
            approval.leaseParticipant().set(leaseParticipant.duplicate());

            if (LeaseParticipantUtils.isApplicationInPogress(lease, leaseParticipant.leaseTermV())) {
                approval.creditCheck().set(
                        ServerSideFactory.create(ScreeningFacade.class).retrivePersonCreditCheck(leaseParticipant.leaseParticipant().customer()));
            } else {
                approval.creditCheck().set(leaseParticipant.creditCheck());
            }

            if (!approval.creditCheck().isNull()) {
                Persistence.ensureRetrieve(approval.creditCheck(), AttachLevel.Attached);
                approval.screening().set(approval.creditCheck().screening());
                Persistence.service().retrieve(approval.screening(), AttachLevel.ToStringMembers);
            } else {
                approval.screening().set(leaseParticipant.effectiveScreening());
            }

            dto.leaseApproval().participants().add(approval);
        }
    }

    private TenantFinancialDTO fillQuickSummary(TenantFinancialDTO tenantFinancial) {
        tenantFinancial.consolidatedIncome().setValue(BigDecimal.ZERO);

        for (CustomerScreeningIncome income : tenantFinancial.incomes()) {
            tenantFinancial.consolidatedIncome().setValue(tenantFinancial.consolidatedIncome().getValue().add(income.details().monthlyAmount().getValue()));

            if (tenantFinancial.employer().isNull()) {
                if (income.details().isInstanceOf(IEmploymentInfo.class)) {
                    IEmploymentInfo ei = income.details().cast();
                    tenantFinancial.employer().setValue(ei.name().getValue());
                    tenantFinancial.position().setValue(ei.position().getValue());
                }
            }
        }

        if (tenantFinancial.employer().isNull()) {
            if (!tenantFinancial.incomes().isEmpty()) {
                tenantFinancial.employer().setValue(tenantFinancial.incomes().get(0).incomeSource().getStringView());
            }
        }

        return tenantFinancial;
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

        for (LeaseTermTenant tenant : dto.currentTerm().version().tenants()) {
            Persistence.service().retrieve(tenant);
            Persistence.service().retrieve(tenant.screening(), AttachLevel.ToStringMembers);

            switch (tenant.role().getValue()) {
            case Applicant:
                dto.mainApplicant().set(tenant.leaseParticipant().customer());
                dto.numberOfApplicants().setValue(dto.numberOfApplicants().getValue() + 1);
                break;
            case CoApplicant:
                dto.numberOfApplicants().setValue(dto.numberOfApplicants().getValue() + 1);
                break;
            case Dependent:
                if (dto.numberOfDepentands().isNull()) {
                    dto.numberOfDepentands().setValue(0);
                }
                dto.numberOfDepentands().setValue(dto.numberOfDepentands().getValue() + 1);
                break;
            case Guarantor:
                break;
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
    public void inviteUsers(AsyncCallback<String> callback, Key entityId, Vector<LeaseTermParticipant<?>> users) {
        CommunicationFacade commFacade = ServerSideFactory.create(CommunicationFacade.class);
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No users to send invitation"));
        }

        // check that we can send the e-mail before we actually try to send email
        for (LeaseTermParticipant<?> user : users) {
            // check that all lease participants have an associated user entity (email)
            if (user.leaseParticipant().customer().user().isNull()) {
                throw new UserRuntimeException(i18n.tr("Failed to invite users, email of lease participant {0} was not found", user.leaseParticipant()
                        .customer().person().name().getStringView()));
            }

            // check that selected guarantors/co-applicants have online-applications
            if (user.application().isNull()) {
                throw new UserRuntimeException(
                        i18n.tr("Failed to invite users, application invitation for {0} can be sent only after the main applicant will have finished his own applicaiton",
                                user.leaseParticipant().customer().person().name().getStringView()));
            }
        }

        for (LeaseTermParticipant<?> user : users) {
            if (user.isInstanceOf(LeaseTermTenant.class)) {
                LeaseTermTenant tenant = user.duplicate(LeaseTermTenant.class);
                if (tenant.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                    commFacade.sendApplicantApplicationInvitation(tenant);
                } else if (tenant.role().getValue() == LeaseTermParticipant.Role.CoApplicant) {
                    commFacade.sendCoApplicantApplicationInvitation(tenant);
                } else {
                    throw new Error("It's unknown what to do with tenant role " + tenant.role().getValue() + " in this context");
                }
            } else if (user.isInstanceOf(LeaseTermGuarantor.class)) {
                LeaseTermGuarantor guarantor = user.duplicate(LeaseTermGuarantor.class);
                commFacade.sendGuarantorApplicationInvitation(guarantor);
            }
        }

        Persistence.service().commit();

        String successMessage = users.size() > 1 ? i18n.tr("Invitations were sent successfully") : i18n.tr("Invitation was sent successfully");
        callback.onSuccess(successMessage);
    }

    @Override
    public void creditCheck(AsyncCallback<String> callback, Key entityId, BigDecimal creditCheckAmount, Vector<LeaseTermParticipant<?>> users) {
        Employee currentUserEmployee = CrmAppContext.getCurrentUserEmployee();
        for (LeaseTermParticipant<?> leaseParticipant : users) {
            ServerSideFactory.create(ScreeningFacade.class).runCreditCheck(creditCheckAmount, leaseParticipant, currentUserEmployee);
        }
        String successMessage = i18n.tr("Credit check has been proceeded successfully.");
        callback.onSuccess(successMessage);
    }

    @Override
    public void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO) {

        switch (actionDTO.action().getValue()) {
        case Approve:
            ServerSideFactory.create(LeaseFacade.class).approve(actionDTO.leaseId(), CrmAppContext.getCurrentUserEmployee(),
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

    @Override
    public void getCreditCheckServiceStatus(AsyncCallback<PmcEquifaxStatus> callback) {
        callback.onSuccess(ServerSideFactory.create(ScreeningFacade.class).getCreditCheckServiceStatus());
    }

    @Override
    public void isCreditCheckViewAllowed(AsyncCallback<VoidSerializable> callback) {
        if (ServerSideFactory.create(ScreeningFacade.class).isReadReportLimitReached()) {
            throw new UserRuntimeException(i18n.tr("Read Report Daily limit exceeded"));
        }
        callback.onSuccess(null);
    }
}
