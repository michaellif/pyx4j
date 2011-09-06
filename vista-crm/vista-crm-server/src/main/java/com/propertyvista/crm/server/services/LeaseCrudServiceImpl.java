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

import org.xnap.commons.i18n.I18n;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.server.mail.MailDeliveryStatus;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.Pet;
import com.propertyvista.domain.User;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.server.common.mail.MessageTemplates;
import com.propertyvista.server.common.ptapp.ApplicationMgr;
import com.propertyvista.server.common.security.UserAccessUtils;

public class LeaseCrudServiceImpl extends GenericCrudServiceDtoImpl<Lease, LeaseDTO> implements LeaseCrudService {

    private static I18n i18n = I18nFactory.getI18n();

    public LeaseCrudServiceImpl() {
        super(Lease.class, LeaseDTO.class);
    }

    @Override
    protected void enhanceDTO(Lease in, LeaseDTO dto, boolean fromList) {
        if (!fromList) {
            // load detached entities:
            Persistence.service().retrieve(in.unit());
            Persistence.service().retrieve(in.vehicles());
            Persistence.service().retrieve(in.pets());
            Persistence.service().retrieve(in.documents());

            // fill selected building by unit:
            Persistence.service().retrieve(dto.unit().belongsTo());
            dto.selectedBuilding().set(dto.unit().belongsTo());
            syncBuildingServiceCatalog(dto.selectedBuilding());

            // update Tenants double links:
            EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), in));
            dto.tenants().clear();
            dto.tenants().addAll(Persistence.service().query(criteria));
        }
    }

    @Override
    protected void persistDBO(Lease dbo, LeaseDTO dto) {
        // persist non-owned lists items:
        for (Pet item : dbo.pets()) {
            Persistence.service().merge(item);
        }
        for (Vehicle item : dbo.vehicles()) {
            Persistence.service().merge(item);
        }
        for (TenantInLease item : dbo.tenants()) {
            Persistence.service().merge(item);
        }
        Persistence.service().merge(dbo);
    }

    @Override
    public void syncBuildingServiceCatalog(AsyncCallback<Building> callback, Key entityId) {
        Building entity = Persistence.service().retrieve(Building.class, entityId);
        callback.onSuccess(syncBuildingServiceCatalog(entity));
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
        Persistence.service().retrieve(building.serviceCatalog());

        // update service catalogue double-reference lists:
        EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
        List<Service> services = Persistence.service().query(serviceCriteria);
        building.serviceCatalog().services().clear();
        building.serviceCatalog().services().addAll(services);

        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
        List<Feature> features = Persistence.service().query(featureCriteria);
        building.serviceCatalog().features().clear();
        building.serviceCatalog().features().addAll(features);

        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
        List<Concession> concessions = Persistence.service().query(concessionCriteria);
        building.serviceCatalog().concessions().clear();
        building.serviceCatalog().concessions().addAll(concessions);

        return building;
    }

    @Override
    public void createMasterApplication(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId);
        lease.status().setValue(Lease.Status.ApplicationInProgress);

        MasterApplication ma = EntityFactory.create(MasterApplication.class);
        ma.lease().set(lease);

        User user = null;
        for (TenantInLease tenantInLease : lease.tenants()) {
            if (TenantInLease.Status.Applicant == tenantInLease.status().getValue()) {
                Application a = EntityFactory.create(Application.class);
                a.steps().addAll(ApplicationMgr.createApplicationProgress());
                a.user().set(tenantInLease.tenant().user());
                user = tenantInLease.tenant().user();
                ma.applications().add(a);
                break;
            }
        }
        Persistence.service().merge(ma);
        Persistence.service().merge(lease);

        if (user != null) {
            Persistence.service().retrieve(user);
            String token = UserAccessUtils.createAccessToken(user, 5);

            MailMessage m = new MailMessage();
            m.setTo(user.email().getValue());
            m.setSender(MessageTemplates.getSender());
            m.setSubject(i18n.tr("Property Vista application"));
            m.setHtmlBody(MessageTemplates.createMasterApplicationInvitationEmail(user.name().getValue(), token));

            if (MailDeliveryStatus.Success != Mail.send(m)) {
                throw new UserRuntimeException(i18n.tr("Mail Service is temporary unavailable, try again later"));
            }
        }

        callback.onSuccess(null);
    }
}
