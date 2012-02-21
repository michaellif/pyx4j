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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataService;
import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.common.security.VistaContext;

abstract class AbstractMetadataServiceImpl implements AbstractMetadataService {

    private final static I18n i18n = I18n.get(AbstractMetadataServiceImpl.class);

    abstract void addTypeCriteria(EntityQueryCriteria<DashboardMetadata> criteria);

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {

        // Load shared dashboards:
        CrmUser anyUser = EntityFactory.create(CrmUser.class);
        //anyUser.setPrimaryKey(ISharedUserEntity.DORMANT_KEY);
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        //criteria.add(PropertyCriterion.eq(criteria.proto().user(), anyUser));
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

        callback.onSuccess(dm);
    }

    @Override
    public void saveMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        if (!dm.id().isNull()) {
            //Assert Permission
            Persistence.secureRetrieve(DashboardMetadata.class, dm.getPrimaryKey());
        }

        if (dm.user().getPrimaryKey() != ISharedUserEntity.DORMANT_KEY) {
            dm.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }

        for (GadgetMetadata gm : dm.gadgets()) {
            persistGadgetMetadata(gm);
        }

        Persistence.secureSave(dm);

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
        if (gadgetMetadata.user().getPrimaryKey() != ISharedUserEntity.DORMANT_KEY) {
            gadgetMetadata.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }
        Persistence.secureSave(gadgetMetadata);
    }
}
