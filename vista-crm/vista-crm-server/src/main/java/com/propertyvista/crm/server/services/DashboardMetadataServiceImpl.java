/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.crm.rpc.services.DashboardMetadataService;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.server.common.security.VistaContext;

public class DashboardMetadataServiceImpl implements DashboardMetadataService {

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);

// TODO VladS - something is wrong with User key while serialising?..
//  com.pyx4j.rpc.shared.UnRecoverableRuntimeException: Unknown member DashboardMetadata/user/id/ in com.propertyvista.domain.dashboard.DashboardMetadata
//        criteria.add(PropertyCriterion.eq(criteria.proto().user().id(), Key.DORMANT_KEY));

        Vector<DashboardMetadata> rc = EntityServicesImpl.secureQuery(criteria);

//        criteria = EntityQueryCriteria.create(DashboardMetadata.class);
//        criteria.add(PropertyCriterion.eq(criteria.proto().user().id(), Context.getVisit().getUserVisit().getPrincipalPrimaryKey()));

//        rc.addAll(EntityServicesImpl.secureQuery(criteria));

        callback.onSuccess(rc);
    }

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId) {
        DashboardMetadata dm = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(DashboardMetadata.class, entityId));
        callback.onSuccess(dm);
    }

    @Override
    public void saveMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        if (!dm.id().isNull()) {
            //Assert Permission
            EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(DashboardMetadata.class, dm.getPrimaryKey()));
        }

        if (!Key.DORMANT_KEY.equals(dm.user().getPrimaryKey())) {
            dm.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
        }

        //??? Set  gadgets  settingsClass
        for (GadgetMetadata gm : dm.gadgets()) {
            if (!Key.DORMANT_KEY.equals(gm.user().getPrimaryKey())) {
                gm.user().setPrimaryKey(VistaContext.getCurrentUserPrimaryKey());
            }

            if (gm.settings().isNull()) {
                gm.settingsClass().setValue(null);
            } else {
                gm.settingsClass().setValue(gm.settings().getInstanceValueClass().getName());
            }
        }

        EntityServicesImpl.secureSave(dm);
        callback.onSuccess(dm);
    }

    @Override
    public void retrieveSettings(AsyncCallback<AbstractGadgetSettings> callback, Key gadgetMetadataId) {
        GadgetMetadata gm = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(GadgetMetadata.class, gadgetMetadataId));

        if (gm.settingsClass().isNull()) {
            callback.onSuccess(null);
        } else {
            Class<? extends AbstractGadgetSettings> settingsClass = ServerEntityFactory.entityClass(gm.settingsClass().getValue());
            callback.onSuccess(EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(settingsClass, gadgetMetadataId)));
        }
    }

    @Override
    public void saveSettings(AsyncCallback<AbstractGadgetSettings> callback, Key gadgetMetadataId, AbstractGadgetSettings settings) {
        GadgetMetadata gm = EntityServicesImpl.secureRetrieve(EntityCriteriaByPK.create(GadgetMetadata.class, settings.getPrimaryKey()));
        if (!settings.getInstanceValueClass().getName().equals(gm.settingsClass().getValue())) {
            gm.settingsClass().setValue(settings.getInstanceValueClass().getName());
            EntityServicesImpl.secureSave(gm);
        }
        EntityServicesImpl.secureSave(settings);
        callback.onSuccess(settings);
    }
}
