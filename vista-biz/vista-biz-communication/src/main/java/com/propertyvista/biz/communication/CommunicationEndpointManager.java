/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class CommunicationEndpointManager {

    private static class SingletonHolder {
        public static final CommunicationEndpointManager INSTANCE = new CommunicationEndpointManager();
    }

    static CommunicationEndpointManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SystemEndpointCacheKey {
        static String getCacheKey(String seName) {
            return String.format("%s_%s", SystemEndpoint.class.getName(), seName);
        }
    }

    private CommunicationEndpointManager() {
        cacheSystemEndpoints();
    }

    private void cacheSystemEndpoints() {
        EntityQueryCriteria<SystemEndpoint> criteria = EntityQueryCriteria.create(SystemEndpoint.class);
        List<SystemEndpoint> predefinedEps = Persistence.service().query(criteria);
        if (predefinedEps != null) {
            for (SystemEndpoint ep : predefinedEps)
                CacheService.put(SystemEndpointCacheKey.getCacheKey(ep.name().getValue()), ep);
        }
    }

    public SystemEndpoint getSystemEndpointFromCache(SystemEndpointName sepName) {
        SystemEndpoint ep = CacheService.get(SystemEndpointCacheKey.getCacheKey(sepName.toString()));
        if (ep == null) {
            cacheSystemEndpoints();
            return CacheService.get(SystemEndpointCacheKey.getCacheKey(sepName.toString()));
        }
        return ep;
    }

    public String extractEndpointName(CommunicationEndpoint entity) {
        if (entity == null) {
            return null;
        }

        if (entity.getInstanceValueClass().equals(SystemEndpoint.class)) {
            SystemEndpoint e = entity.cast();
            return e.name().getValue();
        } else if (entity.getInstanceValueClass().equals(Employee.class)) {
            Employee e = entity.cast();
            return e.name().getStringView();
        } else if (entity.getInstanceValueClass().equals(Tenant.class)) {
            Tenant e = entity.cast();
            return e.customer().person().name().getStringView();
        } else if (entity.getInstanceValueClass().equals(Guarantor.class)) {
            Guarantor e = entity.cast();
            return e.customer().person().name().getStringView();
        } else if (entity.getInstanceValueClass().equals(CommunicationGroup.class)) {
            CommunicationGroup cg = entity.cast();
            if (cg.portfolio() != null && !cg.portfolio().isNull() && !cg.portfolio().isEmpty()) {
                Portfolio e = cg.portfolio().cast();
                return e.name().getStringView();
            } else {
                Building e = cg.building().cast();
                return e.propertyCode().getStringView();
            }
        }
        return null;
    }

    public DeliveryHandle createDeliveryHandle(CommunicationEndpoint endpoint, boolean generatedFromGroup) {
        DeliveryHandle dh = EntityFactory.create(DeliveryHandle.class);
        dh.isRead().setValue(false);
        dh.star().setValue(false);
        if (endpoint.getInstanceValueClass().equals(CommunicationGroup.class)) {
            dh.recipient().set(getSystemEndpointFromCache(SystemEndpointName.Group));
            dh.communicationGroup().set(endpoint);
        } else {
            dh.recipient().set(endpoint);
        }
        dh.generatedFromGroup().setValue(generatedFromGroup);
        return dh;
    }

    public CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity) {
        if (entity == null) {
            return null;
        }
        CommunicationEndpointDTO rec = EntityFactory.create(CommunicationEndpointDTO.class);
        rec.endpoint().set(entity);

        if (entity.getInstanceValueClass().equals(SystemEndpoint.class)) {
            SystemEndpoint e = entity.cast();
            rec.name().setValue(e.name().getValue());
            rec.type().setValue(ContactType.System);
        } else if (entity.getInstanceValueClass().equals(Employee.class)) {
            Employee e = entity.cast();
            rec.name().setValue(e.name().getStringView());
            rec.type().setValue(ContactType.Employee);
        } else if (entity.getInstanceValueClass().equals(Tenant.class)) {
            Tenant e = entity.cast();
            rec.name().setValue(e.customer().person().name().getStringView());
            rec.type().setValue(ContactType.Tenant);
        } else if (entity.getInstanceValueClass().equals(Guarantor.class)) {
            Guarantor e = entity.cast();
            rec.name().setValue(e.customer().person().name().getStringView());
            rec.type().setValue(ContactType.Tenant);
        } else if (entity.getInstanceValueClass().equals(CommunicationGroup.class)) {
            CommunicationGroup cg = entity.cast();
            if (cg.portfolio() != null && !cg.portfolio().isNull() && !cg.portfolio().isEmpty()) {
                Portfolio e = cg.portfolio().cast();
                rec.name().set(e.name());
                rec.type().setValue(ContactType.Portfolio);
            } else {
                Building e = cg.building().cast();
                rec.name().set(e.propertyCode());
                rec.type().setValue(ContactType.Building);
            }
        }
        return rec;
    }

    public String sendersAsStringView(ListOrderedSet<CommunicationEndpoint> senders) {
        if (senders == null || senders.size() < 1) {
            return "";
        }
        if (senders.size() == 1) {
            return senders.get(0).getStringView();
        }
        if (senders.size() == 2) {
            return senders.get(0).getStringView() + ", " + senders.get(1).getStringView();
        }
        return senders.get(0).getStringView() + " ... " + senders.get(senders.size() - 1).getStringView();
    }

    public void buildRecipientList(Message bo, MessageDTO to) {
        HashMap<IEntity, Boolean> visited = new HashMap<IEntity, Boolean>();
        for (CommunicationEndpointDTO todep : to.to()) {
            IEntity epEntity = todep.endpoint();
            if (CommunicationGroup.class.equals(epEntity.getInstanceValueClass())) {
                CommunicationGroup group = epEntity.cast();
                epEntity = group.portfolio() != null && !group.portfolio().isNull() && !group.portfolio().isEmpty() ? group.portfolio() : group.building();
            }
            if (!Tenant.class.equals(epEntity.getInstanceValueClass()) && !Guarantor.class.equals(epEntity.getInstanceValueClass())) {
                if (visited.containsKey(epEntity)) {
                    Boolean currentValue = visited.get(epEntity);
                    visited.put(epEntity, currentValue.booleanValue() && (currentValue == null ? false : currentValue.booleanValue()));
                } else {
                    visited.put(epEntity, false);
                }
            }
            expandCommunicationEndpoint(visited, todep);
        }
        for (Entry<IEntity, Boolean> todep : visited.entrySet()) {
            if (todep.getKey().getInstanceValueClass().equals(SystemEndpoint.class)) {
                SystemEndpoint e = todep.getKey().cast();
                bo.recipients().add(createDeliveryHandle(e, todep.getValue()));
            } else if (todep.getKey().getInstanceValueClass().equals(Employee.class)) {
                Employee e = todep.getKey().cast();
                bo.recipients().add(createDeliveryHandle(e, todep.getValue()));
            } else if (todep.getKey().getInstanceValueClass().equals(Tenant.class)) {
                Tenant e = todep.getKey().cast();
                bo.recipients().add(createDeliveryHandle(e, todep.getValue()));
            } else if (todep.getKey().getInstanceValueClass().equals(Guarantor.class)) {
                Guarantor e = todep.getKey().cast();
                bo.recipients().add(createDeliveryHandle(e, todep.getValue()));
            } else if (todep.getKey().getInstanceValueClass().equals(CommunicationGroup.class)) {
                CommunicationGroup e = todep.getKey().cast();
                bo.recipients().add(createDeliveryHandle(e, todep.getValue()));
            } else {
                CommunicationGroup cg = EntityFactory.create(CommunicationGroup.class);
                if (todep.getKey().getInstanceValueClass().equals(Building.class)) {
                    Building b = todep.getKey().cast();
                    cg.building().set(b);
                } else if (todep.getKey().getInstanceValueClass().equals(Portfolio.class)) {
                    Portfolio b = todep.getKey().cast();
                    cg.portfolio().set(b);
                }
                DeliveryHandle dh = createDeliveryHandle(cg, todep.getValue());
                bo.recipients().add(dh);
            }
        }
    }

    private void expandCommunicationEndpoint(HashMap<IEntity, Boolean> visited, CommunicationEndpointDTO ep) {
        EntityListCriteria<LeaseParticipant> criteria = createActiveLeaseCriteria();

        switch (ep.type().getValue()) {
        case Building: {
            CommunicationGroup cg = ep.endpoint().cast();
            criteria.eq(criteria.proto().lease().unit().building(), cg.building());
            break;
        }

        case Portfolio: {
            CommunicationGroup cg = ep.endpoint().cast();
            EntityListCriteria<Portfolio> buildingCriteria = EntityListCriteria.create(Portfolio.class);
            buildingCriteria.in(buildingCriteria.proto().id(), cg.portfolio());
            Vector<Portfolio> ps = Persistence.secureQuery(buildingCriteria, AttachLevel.Attached);
            ArrayList<Building> bs = new ArrayList<Building>();
            if (ps == null || ps.isEmpty()) {
                return;
            }
            for (Portfolio p : ps) {
                bs.addAll(p.buildings());
            }

            if (bs.isEmpty()) {
                return;
            }
            criteria.in(criteria.proto().lease().unit().building(), bs);
            break;
        }
        case Tenant: {
            criteria.eq(criteria.proto().id(), ep.endpoint().id());
            break;
        }
        case Employee:
        default: {
            return;
        }
        }

        Vector<LeaseParticipant> tenants = Persistence.secureQuery(criteria, AttachLevel.IdOnly);
        if (tenants != null) {
            for (LeaseParticipant t : tenants) {
                if (visited.containsKey(t)) {
                    Boolean currentValue = visited.get(t).booleanValue();

                    visited.put(t, currentValue && !ContactType.Tenant.equals(ep.type().getValue()));
                } else {
                    visited.put(t, !ContactType.Tenant.equals(ep.type().getValue()));
                }
            }
        }
    }

    private EntityListCriteria<LeaseParticipant> createActiveLeaseCriteria() {
        EntityListCriteria<LeaseParticipant> criteria = EntityListCriteria.create(LeaseParticipant.class);
        criteria.eq(criteria.proto().lease().status(), Lease.Status.Active);
        return criteria;
    }
}
