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
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.lease.Lease;
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
        } else if (entity.getInstanceValueClass().equals(CrmUser.class)) {
            CrmUser e = entity.cast();
            return e.name().getValue();
        } else if (entity.getInstanceValueClass().equals(CustomerUser.class)) {
            CustomerUser e = entity.cast();
            return e.name().getValue();
        } else if (entity.getInstanceValueClass().equals(Building.class)) {
            Building e = entity.cast();
            return e.propertyCode().getValue();
        } else if (entity.getInstanceValueClass().equals(AptUnit.class)) {
            AptUnit e = entity.cast();
            return e.getStringView();
        }
        return null;
    }

    public DeliveryHandle createDeliveryHandle(CommunicationEndpoint endpoint, boolean generatedFromGroup) {
        DeliveryHandle dh = EntityFactory.create(DeliveryHandle.class);
        dh.isRead().setValue(false);
        dh.star().setValue(false);
        dh.recipient().set(endpoint);
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
        } else if (entity.getInstanceValueClass().equals(CrmUser.class)) {
            CrmUser e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Employee);
        } else if (entity.getInstanceValueClass().equals(CustomerUser.class)) {
            CustomerUser e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Tenant);
        } else if (entity.getInstanceValueClass().equals(Building.class)) {
            Building e = entity.cast();
            rec.name().set(e.propertyCode());
            rec.type().setValue(ContactType.Building);
        } else if (entity.getInstanceValueClass().equals(Portfolio.class)) {
            Portfolio e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Portfolio);
        } else if (entity.getInstanceValueClass().equals(AptUnit.class)) {
            AptUnit e = entity.cast();
            rec.name().setValue(e.getStringView());
            rec.type().setValue(ContactType.Unit);
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
        HashMap<CommunicationEndpoint, Boolean> visited = new HashMap<CommunicationEndpoint, Boolean>();
        for (CommunicationEndpointDTO todep : to.to()) {
            if (!Tenant.class.equals(todep.endpoint().getInstanceValueClass())) {
                if (visited.containsKey(todep.endpoint())) {
                    Boolean currentValue = visited.get(todep.endpoint());
                    visited.put(todep.endpoint(), visited.get(todep.endpoint()).booleanValue() && (currentValue == null ? false : currentValue.booleanValue()));
                } else {
                    visited.put(todep.endpoint(), false);
                }

            }
            expandCommunicationEndpoint(visited, todep);
        }
        for (Entry<CommunicationEndpoint, Boolean> todep : visited.entrySet()) {
            bo.recipients().add(createDeliveryHandle(todep.getKey(), todep.getValue()));
        }
    }

    private void expandCommunicationEndpoint(HashMap<CommunicationEndpoint, Boolean> visited, CommunicationEndpointDTO ep) {
        EntityListCriteria<Tenant> criteria = createActiveLeaseCriteria();

        switch (ep.type().getValue()) {
        case Building: {
            criteria.eq(criteria.proto().lease().unit().building(), ep.endpoint());
            break;
        }
        case Unit: {
            criteria.eq(criteria.proto().lease().unit(), ep.endpoint());
            break;
        }
        case Portfolio: {
            EntityListCriteria<Portfolio> buildingCriteria = EntityListCriteria.create(Portfolio.class);
            buildingCriteria.in(buildingCriteria.proto().id(), ep.endpoint());
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

        Vector<Tenant> tenants = Persistence.secureQuery(criteria, AttachLevel.IdOnly);
        if (tenants != null) {
            for (Tenant t : tenants) {
                Persistence.ensureRetrieve(t.customer(), AttachLevel.Attached);
                if (visited.containsKey(t.customer().user())) {
                    Boolean currentValue = visited.get(t.customer().user()).booleanValue();

                    visited.put(t.customer().user(), currentValue && !ContactType.Tenant.equals(ep.type().getValue()));
                } else {
                    visited.put(t.customer().user(), !ContactType.Tenant.equals(ep.type().getValue()));
                }
            }
        }
    }

    private EntityListCriteria<Tenant> createActiveLeaseCriteria() {
        EntityListCriteria<Tenant> criteria = EntityListCriteria.create(Tenant.class);
        criteria.eq(criteria.proto().lease().status(), Lease.Status.Active);
        return criteria;
    }
}
