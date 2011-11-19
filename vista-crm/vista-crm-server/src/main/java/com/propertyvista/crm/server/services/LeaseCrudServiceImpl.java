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
package com.propertyvista.crm.server.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;
import com.propertyvista.server.common.ptapp.ApplicationMgr;
import com.propertyvista.server.common.util.TenantInLeaseRetriever;

public class LeaseCrudServiceImpl extends GenericCrudServiceDtoImpl<Lease, LeaseDTO> implements LeaseCrudService {

    private static I18n i18n = I18n.get(LeaseCrudServiceImpl.class);

    public LeaseCrudServiceImpl() {
        super(Lease.class, LeaseDTO.class);
    }

    @Override
    protected void enhanceDTO(Lease in, LeaseDTO dto, boolean fromList) {

        Persistence.service().retrieve(dto.unit());
        Persistence.service().retrieve(dto.unit().belongsTo());

        if (!fromList) {
            // load detached entities:
            Persistence.service().retrieve(dto.documents());
            if (!dto.unit().isNull()) {
                // fill selected building by unit:
                dto.selectedBuilding().set(dto.unit().belongsTo());
                syncBuildingServiceCatalog(dto.selectedBuilding());
            }

            // update Tenants double links:
            TenantInLeaseRetriever.UpdateLeaseTenants(dto);
//            EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
//            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), in));
//            dto.tenants().clear();
//            dto.tenants().addAll(Persistence.service().query(criteria));

            // calculate price adjustments:
            PriceCalculationHelpers.calculateChargeItemAdjustments(dto.serviceAgreement().serviceItem());
            for (ChargeItem item : dto.serviceAgreement().featureItems()) {
                PriceCalculationHelpers.calculateChargeItemAdjustments(item);
            }

            EntityQueryCriteria<MasterApplication> criteria = EntityQueryCriteria.create(MasterApplication.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), in));
            dto.application().set(Persistence.service().retrieve(criteria));
        }
    }

    @Override
    protected void persistDBO(Lease dbo, LeaseDTO in) {
        // persist non-owned lists items:
        for (TenantInLease item : dbo.tenants()) {
            Persistence.service().merge(item);
        }
        for (ChargeItem item : dbo.serviceAgreement().featureItems()) {
            if (!item.extraData().isNull()) {
                Persistence.service().merge(item.extraData());
            }
        }
        Persistence.service().merge(dbo);
    }

    @Override
    public void setSelectededUnit(AsyncCallback<AptUnit> callback, Key unitId) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId);
        syncBuildingServiceCatalog(unit.belongsTo());
        callback.onSuccess(unit);
    }

    @Override
    public void removeTenat(AsyncCallback<Boolean> callback, Key entityId) {
        Persistence.service().delete(Persistence.service().retrieve(TenantInLease.class, entityId));
        callback.onSuccess(true);
    }

    private Building syncBuildingServiceCatalog(Building building) {
        if (building == null || building.isNull()) {
            return null;
        }

        // load detached entities:
        Persistence.service().retrieve(building);
        Persistence.service().retrieve(building.serviceCatalog());

        // update service catalogue double-reference lists:
        EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
        List<Service> services = Persistence.service().query(serviceCriteria);
        building.serviceCatalog().services().clear();
        building.serviceCatalog().services().addAll(services);

        // load detached data:
        for (Service item : services) {
            Persistence.service().retrieve(item.items());
            Persistence.service().retrieve(item.features());
            for (ServiceFeature fi : item.features()) {
                Persistence.service().retrieve(fi.feature().items());
            }
            Persistence.service().retrieve(item.concessions());
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
    public void calculateChargeItemAdjustments(AsyncCallback<Double> callback, ChargeItem item) {
        callback.onSuccess(PriceCalculationHelpers.calculateChargeItemAdjustments(item));
    }

    @Override
    public void createMasterApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId);
        Persistence.service().retrieve(lease.tenants());

        MasterApplication ma = ApplicationMgr.createMasterApplication(lease);
        ma.createDate().setValue(new LogicalDate());

        Persistence.service().persist(ma);
        Persistence.service().persist(lease);

        //TODO Move sendmail to separate function
//        if ((false) && (user != null)) {
//            Persistence.service().retrieve(user);
//            String token = UserAccessUtils.createAccessToken(user, 5);
//
//            MailMessage m = new MailMessage();
//            m.setTo(user.email().getValue());
//            m.setSender(MessageTemplates.getSender());
//            m.setSubject(i18n.tr("Property Vista application"));
//            m.setHtmlBody(MessageTemplates.createMasterApplicationInvitationEmail(user.name().getValue(), token));
//
//            if (MailDeliveryStatus.Success != Mail.send(m)) {
//                throw new UserRuntimeException(i18n.tr("Mail Service is temporary unavailable, try again later"));
//            }
//        }

        callback.onSuccess(null);
    }
}
