/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.common.security.VistaContext;

abstract class AbstractMetadataServiceImpl implements AbstractMetadataService {

    private final static I18n i18n = I18n.get(AbstractMetadataServiceImpl.class);

    abstract void addTypeCriteria(EntityQueryCriteria<DashboardMetadata> criteria);

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {

        // Load shared dashboards:
        CrmUser anyUser = EntityFactory.create(CrmUser.class);
        anyUser.setPrimaryKey(Key.DORMANT_KEY);
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), anyUser));
        addTypeCriteria(criteria);
        Vector<DashboardMetadata> vdm = Persistence.secureQuery(criteria);

        // Load current user's dashboards:
        CrmUser user = EntityFactory.create(CrmUser.class);
        user.setPrimaryKey(Context.getVisit().getUserVisit().getPrincipalPrimaryKey());
        criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        addTypeCriteria(criteria);
        vdm.addAll(Persistence.secureQuery(criteria));

        callback.onSuccess(vdm);
    }

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId) {
        DashboardMetadata dm;
        if (entityId.asLong() == -1) {
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.system));
            dm = Persistence.secureRetrieve(criteria);
        } else {
            dm = Persistence.secureRetrieve(EntityCriteriaByPK.create(DashboardMetadata.class, entityId));
        }

        // construct buildings view
        if (dm.type().getValue() == DashboardType.building) {
            dm.buildingsStringView().setValue(constructBuildingsView(dm.buildings()));
        }

        callback.onSuccess(dm);
    }

    private String constructBuildingsView(Set<Key> buildingPKs) {
        List<Building> buildings = new ArrayList<Building>(buildingPKs.size() + 1); // + 1 for an empty set
        // FIXME change to secure retrieve that does this in one request if possible
        for (Key pk : buildingPKs) {
            buildings.add(Persistence.secureRetrieve(Building.class, pk));
        }
        return constructBuildingsView(buildings);
    }

    /**
     * @return property codes of the provided buildings separated by ", "
     */
    private String constructBuildingsView(List<Building> buildings) {
        if (buildings.isEmpty()) {
            return i18n.tr("All");
        } else {
            List<String> propertyCodes = new ArrayList<String>(buildings.size());
            for (Building b : buildings) {
                propertyCodes.add(b.propertyCode().getValue());
            }
            java.util.Collections.sort(propertyCodes);

            final int last = propertyCodes.size() - 1;
            StringBuilder stringView = new StringBuilder();
            for (int i = 0; i < last; ++i) {
                stringView.append(propertyCodes.get(i)).append(", ");
            }
            stringView.append(propertyCodes.get(last));

            return stringView.toString();
        }
    }

    @Override
    public void saveMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        if (!dm.id().isNull()) {
            //Assert Permission
            Persistence.secureRetrieve(DashboardMetadata.class, dm.getPrimaryKey());
        }

        if (!Key.DORMANT_KEY.equals(dm.user().getPrimaryKey())) {
            dm.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }

        for (GadgetMetadata gm : dm.gadgets()) {
            persistGadgetMetadata(gm);
        }

        Persistence.secureSave(dm);

        // construct buildings view
        if (dm.type().getValue() == DashboardType.building) {
            dm.buildingsStringView().setValue(constructBuildingsView(dm.buildings()));
        }

        callback.onSuccess(dm);
    }

    @Override
    public void retrieveSettings(AsyncCallback<GadgetMetadata> callback, Key gadgetMetadataId) {
        GadgetMetadata gm = Persistence.secureRetrieve(EntityCriteriaByPK.create(GadgetMetadata.class, gadgetMetadataId));
        if (!gm.isNull()) {
            callback.onSuccess(gm);
        } else {
            throw new Error("There is no such gadget! " + gadgetMetadataId.toString());
        }
    }

    @Override
    public void saveSettings(AsyncCallback<GadgetMetadata> callback, GadgetMetadata gadgetMetadata) {
        if (gadgetMetadata != null) {
            persistGadgetMetadata(gadgetMetadata);
            callback.onSuccess(gadgetMetadata);
        } else {
            throw new Error("Got null instead of gadget metadata");
        }
    }

    private void persistGadgetMetadata(GadgetMetadata gadgetMetadata) {
        if (!Key.DORMANT_KEY.equals(gadgetMetadata.user().getPrimaryKey())) {
            gadgetMetadata.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }
        Persistence.secureSave(gadgetMetadata);
    }
}
