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
import java.util.Date;
import java.util.EnumSet;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.deferred.DeferredProcessRegistry;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.system.yardi.YardiLeaseApplicationFacade;
import com.propertyvista.biz.system.yardi.YardiProspectNotFoundException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.config.ThreadPoolNames;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationViewerCrudService;
import com.propertyvista.crm.rpc.services.lease.ac.ApplicationApprove;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;
import com.propertyvista.domain.tenant.CustomerCreditCheck;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.income.CustomerScreeningIncome;
import com.propertyvista.domain.tenant.income.IEmploymentInfo;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.LeaseApplicationDocument;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.LeaseParticipanApprovalDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.util.LeaseParticipantUtils;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseApplicationViewerCrudServiceImpl extends LeaseViewerCrudServiceBaseImpl<LeaseApplicationDTO> implements LeaseApplicationViewerCrudService {

    private final static I18n i18n = I18n.get(LeaseApplicationViewerCrudServiceImpl.class);

    public LeaseApplicationViewerCrudServiceImpl() {
        super(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> boCriteria, EntityListCriteria<LeaseApplicationDTO> toCriteria) {
        super.enhanceListCriteria(boCriteria, toCriteria);

        boCriteria.isNotNull(boCriteria.proto().leaseApplication().status());
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceListRetrieved(in, dto);
        enhanceRetrievedCommon(in, dto);
    }

    @Override
    protected void enhanceRetrieved(Lease lease, LeaseApplicationDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(lease, to, retrieveTarget);
        enhanceRetrievedCommon(lease, to);

        for (LeaseTermTenant tenantId : to.currentTerm().version().tenants()) {
            loadLeaseParticipant(lease, to, tenantId);
        }

        for (LeaseTermGuarantor guarantorId : to.currentTerm().version().guarantors()) {
            loadLeaseParticipant(lease, to, guarantorId);
        }

        if (Lease.Status.isApplicationUnitSelected(to)) {
            BigDecimal total = ServerSideFactory.create(BillingFacade.class).getMaxLeaseTermMonthlyTotal(to.currentTerm());
            ServerSideFactory.create(ScreeningFacade.class).calculateSuggestedDecision(total, to.leaseApproval());
        }

        to.masterApplicationStatus().set(
                ServerSideFactory.create(OnlineApplicationFacade.class).calculateOnlineApplicationStatus(to.leaseApplication().onlineApplication()));

        loadLeaseApplicationDocuments(to);

        // indicates whether lease editing is still possible
        if (Status.PendingDecision.equals(to.leaseApplication().status().getValue()) && VistaFeatures.instance().yardiIntegration()) {
            try {
                to.isYardiApproved().setValue(ServerSideFactory.create(YardiLeaseApplicationFacade.class).isLeaseSigned(lease));
            } catch (YardiProspectNotFoundException pnf) {
                // no lease in yardi yet
                to.isYardiApproved().setValue(false);
            } catch (YardiServiceException e) {
                throw new UserRuntimeException(e.getMessage(), e);
            }
        } else {
            to.isYardiApproved().setValue(false);
        }

    }

    private void enhanceRetrievedCommon(Lease in, LeaseApplicationDTO dto) {
        dto.numberOfOccupants().setValue(dto.currentTerm().version().tenants().size());
        dto.numberOfGuarantors().setValue(dto.currentTerm().version().guarantors().size());
        dto.numberOfApplicants().setValue(0);

        for (LeaseTermTenant tenant : dto.currentTerm().version().tenants()) {
            Persistence.service().retrieve(tenant.screening(), AttachLevel.ToStringMembers, false);

            switch (tenant.role().getValue()) {
            case Applicant:
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

        dto.onlineApplication().setValue(Status.isOnlineApplication(in.leaseApplication()));
    }

    @Override
    protected void loadCurrentTerm(LeaseApplicationDTO dto) {
        assert (!dto.leaseApplication().isNull());
        assert (!dto.currentTerm().isNull());

        if (dto.leaseApplication().status().getValue() == Status.Approved) {
            dto.currentTermNote().setValue(null);
            Key current = dto.currentTerm().getPrimaryKey();

            dto.currentTerm().set(loadHistoricTermVersion(dto, dto.approvalDate().getValue()));

            if (!current.equalsIgnoreVersion(dto.currentTerm().getPrimaryKey())) {
                dto.currentTermNote().setValue("Application effective Term is not the same as Lease one!");
            }
        } else {
            dto.currentTerm().set(Persistence.retriveFinalOrDraft(LeaseTerm.class, dto.currentTerm().getPrimaryKey(), AttachLevel.Attached));
        }

        assert (!dto.currentTerm().isNull());
        assert (!dto.currentTerm().version().isNull());

        Persistence.service().retrieveMember(dto.currentTerm().version().tenants());
        Persistence.service().retrieveMember(dto.currentTerm().version().guarantors());
    }

    private LeaseTerm loadHistoricTermVersion(LeaseApplicationDTO dto, Date forDate) {
        LeaseTerm result = null;

        EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);
        criteria.eq(criteria.proto().lease().id(), dto.getPrimaryKey());
        criteria.in(criteria.proto().status(), EnumSet.of(LeaseTerm.Status.Current, LeaseTerm.Status.Historic));
        criteria.asc(criteria.proto().creationDate());

        for (LeaseTerm term : Persistence.service().query(criteria)) {
            result = Persistence.secureRetrieve(LeaseTerm.class, term.getPrimaryKey().asVersionKey(forDate));
            if (!result.version().isNull()) {
                break; // found
            }
        }

        return result;
    }

    private void loadLeaseParticipant(Lease lease, LeaseApplicationDTO dto, LeaseTermParticipant<? extends LeaseParticipant<?>> participantId) {
        LeaseTermParticipant<? extends LeaseParticipant<?>> termParticipant = (LeaseTermParticipant<?>) Persistence.service().retrieve(
                participantId.getValueClass(), participantId.getPrimaryKey());

        CustomerScreening screening = LeaseParticipantUtils.retrieveLeaseTermEffectiveScreening(lease, termParticipant);

        if (screening != null) {
            {
                Persistence.service().retrieve(termParticipant.leaseParticipant().customer().emergencyContacts());
                TenantInfoDTO tenantInfoDTO = new TenantConverter.LeaseParticipant2TenantInfo().createTO(termParticipant);
                new TenantConverter.TenantScreening2TenantInfo().copyBOtoTO(screening, tenantInfoDTO);
                dto.tenantInfo().add(fillQuickSummary(tenantInfoDTO));
            }

            {
                TenantFinancialDTO tenantFinancialDTO = new TenantConverter.TenantFinancialEditorConverter().createTO(screening);
                tenantFinancialDTO.person().set(termParticipant.leaseParticipant().customer().person());
                dto.tenantFinancials().add(fillQuickSummary(tenantFinancialDTO));
            }
        }

        // approval data
        {
            LeaseParticipanApprovalDTO approval = EntityFactory.create(LeaseParticipanApprovalDTO.class);
            approval.leaseParticipant().set(termParticipant.duplicate());

            if (LeaseParticipantUtils.isApplicationInPogress(lease, termParticipant.leaseTermV())) {
                approval.creditCheck().set(
                        ServerSideFactory.create(ScreeningFacade.class).retrivePersonCreditCheck(termParticipant.leaseParticipant().customer()));
            } else {
                approval.creditCheck().set(termParticipant.creditCheck());
                Persistence.ensureRetrieve(approval.creditCheck(), AttachLevel.Attached);
            }

            approval.screening().set(LeaseParticipantUtils.createScreeningPointer(termParticipant.leaseParticipant(), screening));

            dto.leaseApproval().participants().add(approval);
        }
    }

    private void loadLeaseApplicationDocuments(LeaseApplicationDTO application) {
        EntityQueryCriteria<LeaseApplicationDocument> criteria = EntityQueryCriteria.create(LeaseApplicationDocument.class);
        criteria.eq(criteria.proto().lease(), application.getPrimaryKey());
        criteria.asc(criteria.proto().file().timestamp());
        application.applicationDocuments().addAll(Persistence.service().query(criteria));
    }

    private TenantInfoDTO fillQuickSummary(TenantInfoDTO tenantInfo) {
        return tenantInfo;
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

        if (tenantFinancial.consolidatedIncome().getValue().compareTo(BigDecimal.ZERO) == 0) {
            tenantFinancial.consolidatedIncome().set(null);
        }

        if (tenantFinancial.employer().isNull()) {
            if (!tenantFinancial.incomes().isEmpty()) {
                tenantFinancial.employer().setValue(tenantFinancial.incomes().get(0).incomeSource().getStringView());
            }
        }

        return tenantFinancial;
    }

    @Override
    public void startOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(EntityFactory.createIdentityStub(Lease.class, entityId), null, null);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).cancelMasterOnlineApplication(EntityFactory.createIdentityStub(Lease.class, entityId));
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
                        i18n.tr("Failed to invite users, application invitation for {0} can be sent only after the main applicant will have finished his own application",
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

        StringBuffer errors = new StringBuffer();
        for (LeaseTermParticipant<?> leaseParticipant : users) {
            CustomerCreditCheck pcc = ServerSideFactory.create(ScreeningFacade.class).runCreditCheck(creditCheckAmount, leaseParticipant, currentUserEmployee);

            if (pcc.creditCheckResult().getValue() == CreditCheckResult.Error) {
                Persistence.ensureRetrieve(pcc.screening().screene().person().name(), AttachLevel.ToStringMembers);
                errors.append(i18n.tr("{0} - {1}", pcc.screening().screene().person().name().getStringView(), pcc.reason()));
                errors.append('\n');
            }
        }

        String retMessage = i18n.tr("Credit check has been proceeded successfully.");
        if (errors.length() > 0) {
            retMessage = i18n.tr("Credit check error(s):") + '\n' + errors.toString();
        }

        callback.onSuccess(retMessage);
    }

    @Override
    public void applicationAction(AsyncCallback<String> callback, final LeaseApplicationActionDTO actionDTO) {

        switch (actionDTO.action().getValue()) {
        case Approve:
            SecurityController.assertPermission(new ActionPermission(ApplicationApprove.class));
            break;
        case Decline:
            SecurityController.assertPermission(new ActionPermission(ApplicationApprove.class));
            break;
        default:
            break;
        }

        long maxExpectedTimeMs = VistaFeatures.instance().yardiIntegration() ? 15000 : 1000;
        callback.onSuccess(DeferredProcessRegistry.fork(new LeaseApplicationActionDeferredProcess(actionDTO, maxExpectedTimeMs), ThreadPoolNames.IMPORTS));
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

    @Override
    public void saveApplicationDocument(AsyncCallback<VoidSerializable> callback, LeaseApplicationDocument doc) {
        Persistence.secureRetrieve(Lease.class, doc.lease().getPrimaryKey()); // security enforcement

        doc.uploader().set(CrmAppContext.getCurrentUser());
        doc.isSignedByInk().setValue(true);

        Persistence.service().persist(doc);
        Persistence.service().commit();

        callback.onSuccess(null);
    }
}
