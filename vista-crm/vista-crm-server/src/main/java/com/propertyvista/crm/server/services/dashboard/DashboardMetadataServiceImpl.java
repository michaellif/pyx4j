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
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat.Builder;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.AccessDeniedGagetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class DashboardMetadataServiceImpl implements DashboardMetadataService {

    private static final I18n i18n = I18n.get(DashboardMetadataServiceImpl.class);

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key entityId) {
        DashboardMetadata dm;
        if (entityId == null) {
            dm = null;
        } else if (entityId.asLong() == -1) {
            dm = retrieveDefaultDashboardMetadata();
        } else {
            dm = Persistence.secureRetrieve(DashboardMetadata.class, entityId);
        }
        if (dm == null) {
            callback.onSuccess(null);
            return;
        }

        DashboardColumnLayoutFormat format = new DashboardColumnLayoutFormat(dm.encodedLayout().getValue());
        List<String> lostIds = new ArrayList<String>();
        for (String id : format.gadgetIds()) {
            GadgetMetadata gm = null;
            // for shared dashboards we use a shadow of gadget metadata with effective-id = id + :userKey (but on server side it's transparent)
            if (dm.isShared().isBooleanTrue() & !CrmAppContext.getCurrentUserPrimaryKey().equals(dm.ownerUser().getPrimaryKey())) {
                String shadowId = Util.makeShadowId(id);
                gm = Util.gadgetStorage().load(shadowId);
            }
            if (gm == null) {
                gm = Util.gadgetStorage().load(id);
            }
            if (gm != null) {
                dm.gadgetMetadataList().add(enforcePermissions(gm));
            } else {
                lostIds.add(id);
            }
        }

        // actually this is not supposted to happen but for percation (i.e. bad db migration or something) 
        if (!lostIds.isEmpty()) {
            Builder updatedFormatBuilder = new DashboardColumnLayoutFormat.Builder(format.getLayoutType());
            for (String id : format.gadgetIds()) {
                if (!lostIds.contains(id)) {
                    updatedFormatBuilder.bind(id, format.getGadgetColumn(id));
                }
            }
            dm.encodedLayout().setValue(updatedFormatBuilder.build().getSerializedForm());
            throw new Error("gadgets with IDs: " + lostIds + "could not be loaded and were removed from the dashboard");
        }

        callback.onSuccess(dm);
    }

    /**
     * Compute the difference of gadgets new/deleted and save the result
     */
    @Override
    public void saveDashboardMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {

        // this function should not be used to create new dashboards/reports (new dashboards/reports should be created via CRUD service) 
        if (dm.getPrimaryKey() == null) {
            throw new Error("trying to save new dashboard metadata");
        }

        DashboardMetadata oldDm = Persistence.secureRetrieve(DashboardMetadata.class, dm.getPrimaryKey());
        DashboardColumnLayoutFormat oldLayout = new DashboardColumnLayoutFormat(oldDm.encodedLayout().getValue());
        DashboardColumnLayoutFormat newLayout = new DashboardColumnLayoutFormat(dm.encodedLayout().getValue());

        // find deleted gadgets
        Collection<String> deletedGadgetIds = new ArrayList<String>();
        for (String oldGadgetId : oldLayout.gadgetIds()) {
            boolean isContained = false;
            contained: for (String incomingGadgetId : newLayout.gadgetIds()) {
                if (oldGadgetId.equals(incomingGadgetId)) {
                    isContained = true;
                    break contained;
                }
            }
            if (!isContained) {
                deletedGadgetIds.add(oldGadgetId);
            }
        }

        Persistence.secureSave(dm);

        CustomizationPersistenceHelper<GadgetMetadata> gadgetStorage = Util.gadgetStorage();

        // delete deleted gadgets
        for (String gadgetId : deletedGadgetIds) {
            gadgetStorage.delete(gadgetId);
        }
        // TODO add gadget -> dashboard reference

        Persistence.service().commit();

        callback.onSuccess(dm);
    }

    private DashboardMetadata retrieveDefaultDashboardMetadata() {
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.system));
        return Persistence.secureRetrieve(criteria);
    }

    /**
     * @param gm
     * @return if provided gadgetMetadata is not accessible by current user, returns instance of {@link AccessDeniedGagetMetadata }
     */
    private GadgetMetadata enforcePermissions(GadgetMetadata gm) {
        GadgetDescription description = gm.getInstanceValueClass().getAnnotation(GadgetDescription.class);
        if (SecurityController.checkAnyBehavior(description.allowedBehaviors())) {
            return gm;
        } else {
            AccessDeniedGagetMetadata accessDenied = EntityFactory.create(AccessDeniedGagetMetadata.class);
            accessDenied.gadgetId().setValue(gm.gadgetId().getValue());
            accessDenied.gadgetName().setValue(i18n.translate(null, description.name()));
            return accessDenied;
        }

    }
}
