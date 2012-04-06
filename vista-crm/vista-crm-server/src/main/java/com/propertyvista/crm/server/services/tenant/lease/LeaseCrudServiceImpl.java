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
package com.propertyvista.crm.server.services.tenant.lease;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractVersionedCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.crm.rpc.dto.LeaseApplicationActionDTO;
import com.propertyvista.crm.rpc.services.tenant.lease.LeaseCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.ptapp.OnlineMasterApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineMasterApplication.Status;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.mail.MessageTemplates;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.security.AccessKey;
import com.propertyvista.server.common.util.LeaseManager;
import com.propertyvista.server.domain.security.TenantUserCredential;
import com.propertyvista.server.financial.productcatalog.ProductCatalogFacade;

public class LeaseCrudServiceImpl extends AbstractVersionedCrudServiceDtoImpl<Lease, LeaseDTO> implements LeaseCrudService {

    public LeaseCrudServiceImpl() {
        super(Lease.class, LeaseDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseDTO dto) {
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        // load detached entities:
        Persistence.service().retrieve(dto.version().tenants());
        Persistence.service().retrieve(dto.application());
//        Persistence.service().retrieve(dto.documents());
        if (!dto.unit().isNull()) {
            // fill selected building by unit:
            dto.selectedBuilding().set(dto.unit().belongsTo());
            syncBuildingProductCatalog(dto.selectedBuilding());
        }

        // calculate price adjustments:
        PriceCalculationHelpers.calculateChargeItemAdjustments(dto.version().leaseProducts().serviceItem());
        for (BillableItem item : dto.version().leaseProducts().featureItems()) {
            PriceCalculationHelpers.calculateChargeItemAdjustments(item);
        }
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseDTO dto) {
        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        // TODO this should be part of EntityQueryCriteria.finalizedOrDraft
        if (in.version().isNull()) {
            Lease draft = Persistence.service().retrieve(entityClass, in.getPrimaryKey().asDraftKey());
            dto.version().set(draft.version());
        }

        // place here versioned detached item retrieve: 
        Persistence.service().retrieve(dto.version().tenants());
    }

    @Override
    protected void persist(Lease dbo, LeaseDTO in) {
        boolean isApproveFinal = dbo.saveAction().getValue() == SaveAction.saveAsFinal;

        // save extra data:
        for (BillableItem item : dbo.version().leaseProducts().featureItems()) {
            if (!item.extraData().isNull()) {
                Persistence.service().merge(item.extraData());
            }
        }

        updateAdjustments(dbo);

        new LeaseManager().save(dbo);

        int no = 0;
        for (TenantInLease item : dbo.version().tenants()) {
            item.leaseV().set(dbo.version());
            item.orderInLease().setValue(no++);
            Persistence.service().merge(item);
        }

        // update unit rent price here:
        if (isApproveFinal) {
            ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(in);
        }
    }

    private void updateAdjustments(Lease lease) {
        // ServiceItem Adjustments:
        updateAdjustments(lease.version().leaseProducts().serviceItem());

        // BillableItem Adjustments:
        for (BillableItem ci : lease.version().leaseProducts().featureItems()) {
            updateAdjustments(ci);
        }

        // Lease Financial Adjustments:
        updateAdjustments(lease.billingAccount());
    }

    private void updateAdjustments(BillableItem item) {
        for (BillableItemAdjustment adj : item.adjustments()) {
            // set creator:
            if (adj.createdWhen().isNull()) {
                adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
            }
            // set adjustment expiration date:
            if (ExecutionType.oneTime == adj.executionType().getValue()) {
                adj.expirationDate().setValue(item.effectiveDate().getValue());
            }
        }
    }

    private void updateAdjustments(BillingAccount billingAccount) {
        for (LeaseAdjustment adj : billingAccount.adjustments()) {
            // set creator:
            if (adj.createdWhen().isNull()) {
                adj.createdBy().set(CrmAppContext.getCurrentUserEmployee());
            }
            // set adjustment expiration date:
            // (to the same date as effective - one time adjustment)
            adj.expirationDate().setValue(adj.effectiveDate().getValue());
        }
    }

    @Override
    public void setSelectededUnit(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId);
        Persistence.service().retrieve(unit.belongsTo());
        syncBuildingProductCatalog(unit.belongsTo());
        callback.onSuccess(unit);
    }

    private Building syncBuildingProductCatalog(Building building) {
        if (building == null || building.isNull()) {
            return null;
        }

        // load detached entities:
        Persistence.service().retrieve(building.productCatalog());
        Persistence.service().retrieve(building.productCatalog().services());

        // load detached service eligibility matrix data:
        for (Service item : building.productCatalog().services()) {
            Persistence.service().retrieve(item.version().items());
            Persistence.service().retrieve(item.version().features());
            for (Feature fi : item.version().features()) {
                Persistence.service().retrieve(fi.version().items());
            }
            Persistence.service().retrieve(item.version().concessions());
        }
//      
//  Currently not used here:        
//
//        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
//        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Feature> features = Persistence.service().query(featureCriteria);
//        building.serviceCatalog().features().clear();
//        building.serviceCatalog().features().addAll(features);
//        for (Feature item : features) {
//            Persistence.service().retrieve(item.items());
//        }
//
//        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
//        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Concession> concessions = Persistence.service().query(concessionCriteria);
//        building.serviceCatalog().concessions().clear();
//        building.serviceCatalog().concessions().addAll(concessions);

        return building;
    }

    @Override
    public void calculateChargeItemAdjustments(AsyncCallback<BigDecimal> callback, BillableItem item) {
        callback.onSuccess(PriceCalculationHelpers.calculateChargeItemAdjustments(item));
    }

    @Override
    public void startApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.secureRetrieveDraft(dboClass, entityId);
        OnlineMasterApplication ma = ApplicationManager.createMasterApplication(lease);
        ApplicationManager.sendMasterApplicationEmail(ma);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void applicationAction(AsyncCallback<VoidSerializable> callback, LeaseApplicationActionDTO actionDTO) {
        Lease lease = Persistence.service().retrieve(Lease.class, actionDTO.leasePk().getValue());

        // TODO this is wrong!
        Status currentStatus = lease.application().status().getValue();

        //TODO set status base on action.
        lease.leaseApplication().decidedBy().set(CrmAppContext.getCurrentUserEmployee());
        lease.leaseApplication().decisionReason().setValue(actionDTO.decisionReason().getValue());
        lease.leaseApplication().decisionDate().setValue(new LogicalDate());
        Persistence.secureSave(lease);

        switch (actionDTO.action().getValue()) {
        case Approve:
            Lease approvedLease = new LeaseManager().approveApplication(lease.getPrimaryKey());
            if (currentStatus != OnlineMasterApplication.Status.Incomplete) {
                ApplicationManager.sendApproveDeclineApplicationEmail(approvedLease, true);
            }
            break;
        case Decline:
            Lease declinedLease = new LeaseManager().declineApplication(lease.getPrimaryKey());
            if (currentStatus != OnlineMasterApplication.Status.Incomplete) {
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

    @Override
    public void notice(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut) {
        new LeaseManager().notice(entityId, date, moveOut);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelNotice(AsyncCallback<VoidSerializable> callback, Key entityId) {
        new LeaseManager().cancelNotice(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);

    }

    @Override
    public void evict(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate date, LogicalDate moveOut) {
        new LeaseManager().evict(entityId, date, moveOut);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelEvict(AsyncCallback<VoidSerializable> callback, Key entityId) {
        new LeaseManager().cancelEvict(entityId);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void sendMail(AsyncCallback<VoidSerializable> callback, Key entityId, Vector<TenantInLease> tenants, EmailTemplateType emailType) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, entityId);
        for (TenantInLease tenant : tenants) {
            tenant = Persistence.service().retrieve(TenantInLease.class, tenant.getPrimaryKey());
            TenantUser user = tenant.tenant().user();
            if (user.isValueDetached()) {
                Persistence.service().retrieve(user);
            }
            switch (tenant.role().getValue()) {
            case Applicant:
                ApplicationManager.ensureProspectiveTenantUser(tenant.tenant(), tenant.tenant().person(), VistaTenantBehavior.TenantPrimary);
                break;
            case CoApplicant:
                ApplicationManager.ensureProspectiveTenantUser(tenant.tenant(), tenant.tenant().person(), VistaTenantBehavior.TenantSecondary);
                break;
            }

            String token = AccessKey.createAccessToken(user, TenantUserCredential.class, 10);
            if (token == null) {
                throw new UserRuntimeException("Invalid user account");
            }
            MailMessage m = MessageTemplates.createTenantInvitationEmail(user, lease, emailType, token);
            if (MailDeliveryStatus.Success != Mail.send(m)) {
                throw new UserRuntimeException("Mail delivery failed: " + user.email().getValue());
            }
        }
        callback.onSuccess(null);
    }
}