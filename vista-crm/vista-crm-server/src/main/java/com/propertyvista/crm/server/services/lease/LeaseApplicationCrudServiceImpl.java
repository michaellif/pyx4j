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
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseApplicationCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.Tenant.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication.Status;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.ApplicationUserDTO.ApplicationUser;
import com.propertyvista.dto.LeaseApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.util.LeaseManager;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class LeaseApplicationCrudServiceImpl extends LeaseCrudServiceBaseImpl<LeaseApplicationDTO> implements LeaseApplicationCrudService {

    public LeaseApplicationCrudServiceImpl() {
        super(LeaseApplicationDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceRetrieved(in, dto);

        enhanceListRetrieved(in, dto);

        Persistence.service().retrieve(dto.leaseApplication().onlineApplication());
        dto.masterApplicationStatus().set(ApplicationManager.calculateStatus(dto.leaseApplication().onlineApplication()));
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseApplicationDTO dto) {
        super.enhanceListRetrieved(in, dto);

        dto.numberOfOccupants().setValue(dto.version().tenants().size());
        dto.numberOfGuarantors().setValue(dto.version().guarantors().size());
        dto.numberOfCoApplicants().setValue(0);

        for (Tenant tenantInLease : dto.version().tenants()) {
            Persistence.service().retrieve(tenantInLease);

            if (tenantInLease.role().getValue() == Role.Applicant) {
                dto.mainApplicant().set(tenantInLease.customer());
            } else if (tenantInLease.role().getValue() == Role.CoApplicant) {
                dto.numberOfCoApplicants().setValue(dto.numberOfCoApplicants().getValue() + 1);
            }

            TenantInLeaseRetriever tr = new TenantInLeaseRetriever(tenantInLease.getPrimaryKey(), true);
            dto.tenantInfo().add(createTenantInfoDTO(tr));
            dto.tenantFinancials().add(createTenantFinancialDTO(tr));
        }
    }

    @Override
    public void startOnlineApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void retrieveUsers(AsyncCallback<Vector<ApplicationUserDTO>> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId.asDraftKey());
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        Vector<ApplicationUserDTO> users = new Vector<ApplicationUserDTO>();

        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenant : lease.version().tenants()) {
            Persistence.service().retrieve(tenant);
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                ApplicationUserDTO user = EntityFactory.create(ApplicationUserDTO.class);

                user.person().set(tenant.customer().person());
                user.user().set(tenant.customer());
                user.userType().setValue(tenant.role().getValue() == Role.Applicant ? ApplicationUser.Applicant : ApplicationUser.CoApplicant);

                users.add(user);
            }
        }

        Persistence.service().retrieve(lease.version().guarantors());
        for (Guarantor guarantor : lease.version().guarantors()) {
            Persistence.service().retrieve(guarantor);
            ApplicationUserDTO user = EntityFactory.create(ApplicationUserDTO.class);

            user.person().set(guarantor.customer().person());
            user.user().set(guarantor);
            user.userType().setValue(ApplicationUser.Guarantor);

            users.add(user);
        }

        callback.onSuccess(users);
    }

    @Override
    public void inviteUsers(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<ApplicationUserDTO> users) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId.asDraftKey());
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        MasterOnlineApplication app = lease.leaseApplication().onlineApplication();
        Persistence.service().retrieve(app);

        for (ApplicationUserDTO user : users) {
            if (user.user().isValueDetached()) {
                Persistence.service().retrieve(user.user());
            }
            switch (user.userType().getValue()) {
            case Applicant:
                ApplicationManager.inviteUser(app, user.user(), user.person(), VistaCustomerBehavior.ProspectiveApplicant);
                break;
            case CoApplicant:
                ApplicationManager.inviteUser(app, user.user(), user.person(), VistaCustomerBehavior.ProspectiveCoApplicant);
                break;
            case Guarantor:
                ApplicationManager.inviteUser(app, user.user(), user.person(), VistaCustomerBehavior.Guarantor);
                break;
            }
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO) {
        Lease lease = Persistence.service().retrieve(Lease.class, actionDTO.leasePk().getValue());

        // TODO this is wrong!
        Status currentStatus = lease.leaseApplication().onlineApplication().status().getValue();

        //TODO set status base on action.
        lease.leaseApplication().decidedBy().set(CrmAppContext.getCurrentUserEmployee());
        lease.leaseApplication().decisionReason().setValue(actionDTO.decisionReason().getValue());
        lease.leaseApplication().decisionDate().setValue(new LogicalDate());
        Persistence.secureSave(lease);

        switch (actionDTO.action().getValue()) {
        case Approve:
            Lease approvedLease = new LeaseManager().approveApplication(lease.getPrimaryKey());
            if (currentStatus != MasterOnlineApplication.Status.Incomplete) {
                ApplicationManager.sendApproveDeclineApplicationEmail(approvedLease, true);
            }
            break;
        case Decline:
            Lease declinedLease = new LeaseManager().declineApplication(lease.getPrimaryKey());
            if (currentStatus != MasterOnlineApplication.Status.Incomplete) {
                ApplicationManager.sendApproveDeclineApplicationEmail(declinedLease, false);
            }
            break;
        case Cancel:
            new LeaseManager().cancelApplication(lease.getPrimaryKey());
            break;
        }
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    // internal helpers:
    private TenantInfoDTO createTenantInfoDTO(TenantInLeaseRetriever tr) {
        TenantInfoDTO tiDTO = new TenantConverter.Tenant2TenantInfo().createDTO(tr.getTenant());
        new TenantConverter.TenantScreening2TenantInfo().copyDBOtoDTO(tr.tenantScreening, tiDTO);
        return tiDTO;
    }

    private TenantFinancialDTO createTenantFinancialDTO(TenantInLeaseRetriever tr) {
        TenantFinancialDTO tfDTO = new TenantConverter.TenantFinancialEditorConverter().createDTO(tr.tenantScreening);
        tfDTO.person().set(tr.getPerson());
        return tfDTO;
    }
}
