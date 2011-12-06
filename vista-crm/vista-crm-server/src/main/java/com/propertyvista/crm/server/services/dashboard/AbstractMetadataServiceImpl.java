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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataService;
import com.propertyvista.domain.User;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.server.common.security.VistaContext;

abstract class AbstractMetadataServiceImpl implements AbstractMetadataService {

    abstract void addTypeCriteria(EntityQueryCriteria<DashboardMetadata> criteria);

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {

        // Load shared dashboards:
        User anyUser = EntityFactory.create(User.class);
        anyUser.setPrimaryKey(Key.DORMANT_KEY);
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), anyUser));
        addTypeCriteria(criteria);
        Vector<DashboardMetadata> vdm = EntityServicesImpl.secureQuery(criteria);

        // Load current user's dashboards:
        User user = EntityFactory.create(User.class);
        user.setPrimaryKey(Context.getVisit().getUserVisit().getPrincipalPrimaryKey());
        criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        addTypeCriteria(criteria);
        vdm.addAll(EntityServicesImpl.secureQuery(criteria));

        callback.onSuccess(vdm);
    }

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId) {
        DashboardMetadata dm;
        if (entityId.asLong() == -1) {
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.system));
            dm = EntityServicesImpl.secureRetrieve(criteria);
        } else {
            dm = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(DashboardMetadata.class, entityId));
        }
        callback.onSuccess(dm);
    }

    @Override
    public void saveMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        if (!dm.id().isNull()) {
            //Assert Permission
            EntityServicesImpl.secureRetrieve(DashboardMetadata.class, dm.getPrimaryKey());
        }

        if (!Key.DORMANT_KEY.equals(dm.user().getPrimaryKey())) {
            dm.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }

        for (GadgetMetadata gm : dm.gadgets()) {
            persistGadgetMetadata(gm);
        }

        EntityServicesImpl.secureSave(dm);
        callback.onSuccess(dm);
    }

    @Override
    public void retrieveSettings(AsyncCallback<GadgetMetadata> callback, Key gadgetMetadataId) {
        GadgetMetadata gm = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(GadgetMetadata.class, gadgetMetadataId));
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
        EntityServicesImpl.secureSave(gadgetMetadata);
    }
}
