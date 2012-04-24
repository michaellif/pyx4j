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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.Tenant.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class LeaseApplicationCrudServiceImpl extends LeaseCrudServiceBaseImpl<LeaseApplicationDTO> implements LeaseApplicationCrudService {

    private final static I18n i18n = I18n.get(LeaseApplicationCrudServiceImpl.class);

    public LeaseApplicationCrudServiceImpl() {
        super(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceRetrieved(in, dto);
        enhanceRetrievedCommon(in, dto);

        for (Tenant tenant : dto.version().tenants()) {
            TenantInLeaseRetriever tr = new TenantInLeaseRetriever(tenant.getPrimaryKey(), true);
            dto.tenantInfo().add(createTenantInfoDTO(tr));
            dto.tenantFinancials().add(createTenantFinancialDTO(tr));
        }

        Persistence.service().retrieve(dto.leaseApplication().onlineApplication());
        dto.masterApplicationStatus().set(ApplicationManager.calculateStatus(dto.leaseApplication().onlineApplication()));
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceListRetrieved(in, dto);
        enhanceRetrievedCommon(in, dto);
    }

    @Override
    public void startOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    private void enhanceRetrievedCommon(Lease in, LeaseApplicationDTO dto) {
        dto.numberOfOccupants().setValue(dto.version().tenants().size());
        dto.numberOfGuarantors().setValue(dto.version().guarantors().size());
        dto.numberOfCoApplicants().setValue(0);

        for (Tenant tenant : dto.version().tenants()) {
            Persistence.service().retrieve(tenant);
            Persistence.service().retrieve(tenant.screening(), AttachLevel.ToStringMembers);

            if (tenant.role().getValue() == Role.Applicant) {
                dto.mainApplicant().set(tenant.customer());
            } else if (tenant.role().getValue() == Role.CoApplicant) {
                dto.numberOfCoApplicants().setValue(dto.numberOfCoApplicants().getValue() + 1);
            }
        }
    }

    @Override
    public void inviteUsers(AsyncCallback<String> callback, Key entityId, Vector<ApplicationUserDTO> users) {
        CommunicationFacade commFacade = ServerSideFactory.create(CommunicationFacade.class);
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No users to send invitation"));
        }

        // check that we can send the e-mail before we actually try to send email
        for (ApplicationUserDTO user : users) {
            // check that all lease participants have an associated user entity (email)            
            if (user.leaseParticipant().customer().user().isNull()) {
                throw new UserRuntimeException(i18n.tr("Failed to invite users, email of lease participant {0} was not found", user.leaseParticipant()
                        .customer().person().name().getStringView()));
            }

            // check that selected guarantors/co-applicants have online-applications
            if (user.leaseParticipant().application().isNull()) {
                throw new UserRuntimeException(
                        i18n.tr("Failed to invite users, application invitation for {0} can be sent only after the main applicant will have finished his own applicaiton",
                                user.leaseParticipant().customer().person().name().getStringView()));
            }
        }

        for (ApplicationUserDTO user : users) {
            if (user.leaseParticipant().isInstanceOf(Tenant.class)) {
                Tenant tenant = user.leaseParticipant().duplicate(Tenant.class);
                if (tenant.role().getValue() == Role.Applicant) {
                    commFacade.sendApplicantApplicationInvitation(tenant);
                } else if (tenant.role().getValue() == Role.CoApplicant) {
                    commFacade.sendCoApplicantApplicationInvitation(tenant);
                } else {
                    throw new Error("It's unknown what to do with tenant role " + tenant.role().getValue() + " in this context");
                }
            } else if (user.leaseParticipant().isInstanceOf(Guarantor.class)) {
                Guarantor guarantor = user.leaseParticipant().duplicate(Guarantor.class);
                commFacade.sendGuarantorApplicationInvitation(guarantor);
            }
        }

        Persistence.service().commit();

        String successMessage = users.size() > 1 ? i18n.tr("Invitations were sent successfully") : i18n.tr("Invitation was sent successfully");
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

    // internal helpers:
    private TenantInfoDTO createTenantInfoDTO(TenantInLeaseRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.personScreening, tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantInLeaseRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.personScreening);
        tfDTO.person().set(tr.getPerson());
        return tfDTO;
    }
}
