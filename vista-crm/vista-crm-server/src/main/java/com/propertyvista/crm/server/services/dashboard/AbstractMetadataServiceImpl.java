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
import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

abstract class AbstractMetadataServiceImpl implements AbstractMetadataService {

    private final static I18n i18n = I18n.get(AbstractMetadataServiceImpl.class);

    abstract void addTypeCriteria(EntityQueryCriteria<DashboardMetadata> criteria);

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.or().left(PropertyCriterion.eq(criteria.proto().user(), CrmAppContext.getCurrentUserPrimaryKey()))
                .right(PropertyCriterion.eq(criteria.proto().isShared(), true));
        addTypeCriteria(criteria);
        Vector<DashboardMetadata> dashboardMetadataList = Persistence.secureQuery(criteria);
        callback.onSuccess(dashboardMetadataList);
    }

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId) {
        DashboardMetadata dm;
        if (entityId == null) {
            dm = null;
        } else if (entityId.asLong() == -1) {
            dm = retrieveDefaultMetadata();
        } else {
            dm = Persistence.secureRetrieve(DashboardMetadata.class, entityId);
        }
        for (String id : dm.gadgetIds()) {
            GadgetMetadata gm = new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class, GadgetMetadata.class).load(id);
            dm.gadgets().add(gm);
        }

        callback.onSuccess(dm);
    }

    @Override
    public void saveDashboardMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        // this function should not be used to create new dashboards/reports (new dashboards/reports should be created via CRUD service) 
        if (dm.getPrimaryKey() == null) {
            throw new Error("trying to save new dashboard metadata");
        }

        DashboardMetadata oldDm = Persistence.secureRetrieve(DashboardMetadata.class, dm.getPrimaryKey());

        // find deleted gadgets
        Collection<GadgetMetadata> deletedGadgets = new ArrayList<GadgetMetadata>();
        Collection<GadgetMetadata> remainingGadgets = new ArrayList<GadgetMetadata>();
        for (GadgetMetadata oldGadgetMetadata : oldDm.gadgets()) {
            boolean isContained = false;
            contained: for (GadgetMetadata inGadgetMetadata : dm.gadgets()) {
                if (inGadgetMetadata.getPrimaryKey().equals(oldGadgetMetadata.getPrimaryKey())) {
                    isContained = true;
                    break contained;
                }
            }
            if (!isContained) {
                deletedGadgets.add(oldGadgetMetadata);
            } else {
                remainingGadgets.add(oldGadgetMetadata);
            }
        }

        // find new gadgets
        Collection<GadgetMetadata> addedGadgets = new ArrayList<GadgetMetadata>();
        for (GadgetMetadata gadgetMetadata : dm.gadgets()) {
            if (gadgetMetadata.getPrimaryKey() == null) {
                addedGadgets.add(gadgetMetadata);
            }
        }

        dm.gadgetIds().clear();
        for (GadgetMetadata gadget : remainingGadgets) {
            dm.gadgetIds().add(gadget.gadgetId().getValue());
        }

        // delete deleted gadgets
        for (GadgetMetadata gadget : deletedGadgets) {
            new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class, GadgetMetadata.class).delete(gadget.getPrimaryKey().toString(),
                    gadget);
        }

        // persist new gadgets        
        for (GadgetMetadata gadget : addedGadgets) {
            gadget.setPrimaryKey(new Key(1l));
            gadget.gadgetId().setValue(UUID.randomUUID().toString());
            dm.gadgetIds().add(gadget.gadgetId().getValue());
            new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class, GadgetMetadata.class).save(gadget.gadgetId().getValue(), gadget,
                    true);
        }

        Persistence.secureSave(dm);
        Persistence.service().commit();
        callback.onSuccess(dm);
    }

    @Override
    public void retrieveGadgetMetadata(AsyncCallback<GadgetMetadata> callback, Key gadgetMetadataId) {
        throw new Error("Not Implemented"); // actually this method shouldn't exist: must be refactored
    }

    @Override
    public void saveGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata gadgetMetadata) {
        // TODO refactor all this crap: make it be called only when owner is set...
        if (gadgetMetadata != null) {
            Persistence.service().persist(gadgetMetadata);
            Persistence.service().commit();
            callback.onSuccess(gadgetMetadata);
        } else {
            throw new Error("Got null instead of gadget metadata");
        }
    }

    protected abstract DashboardMetadata retrieveDefaultMetadata();
}
