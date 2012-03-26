/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.tenant.application;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.dto.MasterApplicationActionDTO;
import com.propertyvista.crm.rpc.services.tenant.application.MasterApplicationCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.PersonGuarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.ApplicationUserDTO;
import com.propertyvista.dto.ApplicationUserDTO.ApplicationUser;
import com.propertyvista.dto.MasterApplicationDTO;
import com.propertyvista.dto.TenantFinancialDTO;
import com.propertyvista.dto.TenantInfoDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.util.LeaseManager;
import com.propertyvista.server.common.util.TenantConverter;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class MasterApplicationCrudServiceImpl extends AbstractCrudServiceDtoImpl<MasterApplication, MasterApplicationDTO> implements
        MasterApplicationCrudService {

    public MasterApplicationCrudServiceImpl() {
        super(MasterApplication.class, MasterApplicationDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceListRetrieved(MasterApplication in, MasterApplicationDTO dto) {

        Persistence.service().retrieve(dto.lease());
        Persistence.service().retrieve(dto.lease().unit());
        Persistence.service().retrieve(dto.lease().unit().belongsTo());
        Persistence.service().retrieve(dto.lease().version().tenants());

        dto.numberOfOccupants().setValue(dto.lease().version().tenants().size());
        dto.numberOfCoApplicants().setValue(0);
        dto.numberOfGuarantors().setValue(0);

        for (TenantInLease tenantInLease : dto.lease().version().tenants()) {
            Persistence.service().retrieve(tenantInLease);

            if (tenantInLease.role().getValue() == Role.Applicant) {
                dto.mainApplicant().set(tenantInLease.tenant());
            } else if (tenantInLease.role().getValue() == Role.CoApplicant) {
                dto.numberOfCoApplicants().setValue(dto.numberOfCoApplicants().getValue() + 1);
            }

            TenantInLeaseRetriever tr = new TenantInLeaseRetriever(tenantInLease.getPrimaryKey(), true);
            dto.tenantInfo().add(createTenantInfoDTO(tr));
            TenantFinancialDTO tf = createTenantFinancialDTO(tr);
            dto.numberOfGuarantors().setValue(dto.numberOfGuarantors().getValue() + tf.guarantors().size());
            dto.tenantFinancials().add(tf);
        }

        calculatePrices(in, dto);

        // TODO: currently - just some mockup stuff:
        dto.deposit().setValue(new BigDecimal(100 + RandomUtil.randomDouble(1000)));
    }

    @Override
    protected void enhanceRetrieved(MasterApplication in, MasterApplicationDTO dto) {
        enhanceListRetrieved(in, dto);
        dto.masterApplicationStatus().set(ApplicationManager.calculateStatus(in));
    }

    @Override
    public void action(AsyncCallback<MasterApplicationDTO> callback, MasterApplicationActionDTO actionDTO) {
        MasterApplication dbo = Persistence.service().retrieve(dboClass, actionDTO.getPrimaryKey());

        dbo.status().setValue(actionDTO.status().getValue());
        dbo.decidedBy().set(CrmAppContext.getCurrentUserEmployee());
        dbo.decisionReason().setValue(actionDTO.decisionReason().getValue());
        dbo.decisionDate().setValue(new LogicalDate());
        Persistence.service().merge(dbo);

        switch (actionDTO.status().getValue()) {
        case Approved:
            Lease approvedLease = new LeaseManager().approveApplication(dbo.lease().getPrimaryKey());
            ApplicationManager.sendApproveDeclineApplicationEmail(approvedLease, true);
            break;
        case Declined:
            Lease declinedLease = new LeaseManager().declineApplication(dbo.lease().getPrimaryKey());
            ApplicationManager.sendApproveDeclineApplicationEmail(declinedLease, false);
            break;
        case Cancelled:
            new LeaseManager().cancelApplication(dbo.lease().getPrimaryKey());
            break;
        }
        Persistence.service().commit();

        // return data for view
        retrieve(callback, actionDTO.getPrimaryKey(), RetrieveTraget.View);
        Persistence.service().commit();
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

    private void calculatePrices(MasterApplication in, MasterApplicationDTO dto) {
        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.lease().version().leaseProducts().serviceItem());

        dto.rentPrice().setValue(dto.lease().version().leaseProducts().serviceItem()._currentPrice().getValue());
        dto.parkingPrice().setValue(new BigDecimal(0));
        dto.otherPrice().setValue(new BigDecimal(0));
        dto.deposit().setValue(new BigDecimal(0));

        for (BillableItem item : dto.lease().version().leaseProducts().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item); // calculate price adjustments
            if (item.item().product() instanceof Feature) {
                switch (((Feature) item.item().product()).version().type().getValue()) {
                case parking:
                    dto.parkingPrice().setValue(dto.parkingPrice().getValue().add(item._currentPrice().getValue()));
                    break;

                default:
                    dto.otherPrice().setValue(dto.otherPrice().getValue().add(item._currentPrice().getValue()));
                }
            }
        }

        dto.discounts().setValue(!dto.lease().version().leaseProducts().concessions().isEmpty());
    }

    @Override
    public void retrieveUsers(AsyncCallback<Vector<ApplicationUserDTO>> callback, Key entityId) {
        MasterApplication entity = Persistence.service().retrieve(dboClass, entityId);
        if ((entity == null) || (entity.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        Vector<ApplicationUserDTO> users = new Vector<ApplicationUserDTO>();

        Persistence.service().retrieve(entity.lease());
        Persistence.service().retrieve(entity.lease().version().tenants());

        for (TenantInLease tenantInLease : entity.lease().version().tenants()) {
            Persistence.service().retrieve(tenantInLease);
            switch (tenantInLease.role().getValue()) {
            case Applicant:
            case CoApplicant:
                ApplicationUserDTO tenant = EntityFactory.create(ApplicationUserDTO.class);

                tenant.person().set(tenantInLease.tenant().person());
                tenant.user().set(tenantInLease.tenant());
                tenant.userType().setValue(tenantInLease.role().getValue() == Role.Applicant ? ApplicationUser.Applicant : ApplicationUser.CoApplicant);

                users.add(tenant);

                // process Guarantors:

                EntityQueryCriteria<PersonScreening> criteriaPS = EntityQueryCriteria.create(PersonScreening.class);
                criteriaPS.add(PropertyCriterion.eq(criteriaPS.proto().screene(), tenantInLease.tenant()));
                PersonScreening tenantScreenings = Persistence.service().retrieve(criteriaPS);
                if (tenantScreenings != null) {
                    Persistence.service().retrieve(tenantScreenings.guarantors());
                    for (PersonGuarantor pg : tenantScreenings.guarantors()) {
                        ApplicationUserDTO guarantor = EntityFactory.create(ApplicationUserDTO.class);

                        guarantor.person().set(pg.guarantor().person());
                        guarantor.user().set(pg.guarantor());
                        guarantor.userType().setValue(ApplicationUser.Guarantor);

                        users.add(guarantor);
                    }
                }
            }
        }

        callback.onSuccess(users);
    }

    @Override
    public void inviteUsers(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<ApplicationUserDTO> users) {
        MasterApplication entity = Persistence.service().retrieve(dboClass, entityId);
        if ((entity == null) || (entity.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }

        for (ApplicationUserDTO user : users) {
            if (user.user().isValueDetached()) {
                Persistence.service().retrieve(user.user());
            }
            switch (user.userType().getValue()) {
            case Applicant:
                ApplicationManager.inviteUser(entity, user.user(), user.person(), VistaTenantBehavior.ProspectiveApplicant);
                break;
            case CoApplicant:
                ApplicationManager.inviteUser(entity, user.user(), user.person(), VistaTenantBehavior.ProspectiveCoApplicant);
                break;
            case Guarantor:
                ApplicationManager.inviteUser(entity, user.user(), user.person(), VistaTenantBehavior.Guarantor);
                break;
            }
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }
}
