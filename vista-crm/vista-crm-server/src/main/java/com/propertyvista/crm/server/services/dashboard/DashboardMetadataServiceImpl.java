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
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat.Builder;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.AccessDeniedGagetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class DashboardMetadataServiceImpl implements DashboardMetadataService {

    private static final I18n i18n = I18n.get(DashboardMetadataServiceImpl.class);

    private final Logger logger = LoggerFactory.getLogger(DashboardMetadataServiceImpl.class);

    @Override
    public void retrieveMetadata(AsyncCallback<DashboardMetadata> callback, Key dashboardId) {
        assert (dashboardId != null);
        DashboardMetadata dashboardMetadata;
        if (dashboardId.asLong() != -1) {
            dashboardMetadata = Persistence.secureRetrieve(DashboardMetadata.class, dashboardId);
        } else {
            dashboardMetadata = retrieveDefaultDashboardMetadata();
        }
        if (dashboardMetadata == null) {
            callback.onSuccess(null);
            logger.warn("Dashboard '" + dashboardId + "' that was requested by user '" + CrmAppContext.getCurrentUser().getPrimaryKey() + "' was not found");
            return;
        }

        List<String> gadgetIds = new LinkedList<String>(new DashboardColumnLayoutFormat(dashboardMetadata.encodedLayout().getValue()).gadgetIds());
        dashboardMetadata.gadgetMetadataList().addAll(secureRetrieveGadgets(gadgetIds));
        if (!gadgetIds.isEmpty()) {
            logger.warn("gadgets with IDs '" + gadgetIds + "' were not found, and they are going to be removed form dashboard layout of dashboard '"
                    + dashboardMetadata.getPrimaryKey() + "'");
            cleanupLostGadgets(gadgetIds, dashboardMetadata);
        }

        callback.onSuccess(dashboardMetadata);
    }

    @Override
    public void saveDashboardMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        if (dm.getPrimaryKey() == null) {
            throw new Error("trying to save new dashboard metadata: '" + DashboardMetadataCrudService.class.getSimpleName()
                    + "' must be used to create new dashboards");
        }

        // Compute the difference of gadgets: new vs. deleted and save the result        
        DashboardMetadata oldDashboardMetadata = Persistence.secureRetrieve(DashboardMetadata.class, dm.getPrimaryKey());
        DashboardColumnLayoutFormat oldLayout = new DashboardColumnLayoutFormat(oldDashboardMetadata.encodedLayout().getValue());
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

        // delete gadgets that were deleted from the dashboad
        CustomizationPersistenceHelper<GadgetMetadata> gadgetStorage = Util.gadgetStorage();
        for (String gadgetId : deletedGadgetIds) {
            gadgetStorage.delete(gadgetId);
        }

        Persistence.service().commit();

        callback.onSuccess(dm);
    }

    // FIXME: prone to race conditions (because we are working with transactions are in read-committed mode)
    @Override
    public void takeOwnership(AsyncCallback<VoidSerializable> callback, DashboardMetadata dashboardMetadataStub) {

        SecurityController.assertBehavior(VistaCrmBehavior.DashboardManager);
        DashboardMetadata dashboardMetadata = Persistence.service().retrieve(DashboardMetadata.class, dashboardMetadataStub.getPrimaryKey());
        if (dashboardMetadata == null) {
            throw new Error("Dashboard Metadata was not found");
        }

        Key managersPk = CrmAppContext.getCurrentUserPrimaryKey();

        for (String gadgetId : new DashboardColumnLayoutFormat(dashboardMetadata.encodedLayout().getValue()).gadgetIds()) {
            GadgetMetadata gadgetMetadata = Util.gadgetStorage().load(gadgetId);
            if (gadgetMetadata != null) {
                gadgetMetadata.ownerUser().setPrimaryKey(managersPk);
                Util.gadgetStorage().save(gadgetId, gadgetMetadata, true, true);
            }
        }
        dashboardMetadata.ownerUser().setPrimaryKey(managersPk);
        Persistence.service().persist(dashboardMetadata);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    private DashboardMetadata retrieveDefaultDashboardMetadata() {
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.system));
        return Persistence.secureRetrieve(criteria);
    }

    /**
     * This is a destructive function that changes <code>gadgetIds</code> parameter.
     * If used doesn't have access permissions for a retrieved gadgets, it gets substituted by a special {@link AccessDeniedGagetMetadata}.
     * 
     * @param gadgetIds
     *            gadgets that should be retrieved, when upon return it all the gadgets that were retrieved will be removed
     * @return list of retrieved gadgets
     */
    private List<GadgetMetadata> secureRetrieveGadgets(Collection<String> gadgetIds) {
        java.util.Iterator<String> gadgetIterator = gadgetIds.iterator();
        List<GadgetMetadata> gadgetMetadataList = new ArrayList<GadgetMetadata>();
        while (gadgetIterator.hasNext()) {
            GadgetMetadata gadgetMetadata = Util.gadgetStorage().load(gadgetIterator.next());
            if (gadgetMetadata != null) {
                gadgetMetadataList.add(enforcePermissions(gadgetMetadata));
                gadgetIterator.remove();
            }
        }
        return gadgetMetadataList;
    }

    /**
     * @param gadgetMetadata
     * @return if provided gadgetMetadata is not accessible by current user, returns instance of {@link AccessDeniedGagetMetadata }
     */
    private GadgetMetadata enforcePermissions(GadgetMetadata gadgetMetadata) {
        GadgetDescription description = gadgetMetadata.getInstanceValueClass().getAnnotation(GadgetDescription.class);
        if (SecurityController.checkAnyBehavior(description.allowedBehaviors())) {
            return gadgetMetadata;
        } else {
            AccessDeniedGagetMetadata accessDenied = EntityFactory.create(AccessDeniedGagetMetadata.class);
            accessDenied.gadgetId().setValue(gadgetMetadata.gadgetId().getValue());
            accessDenied.gadgetName().setValue(i18n.translate(null, accessDenied.getEntityMeta().getCaption()));
            return accessDenied;
        }

    }

    /**
     * Removes gadgets that coulnd't be loaded from dashboard layout.
     * Actually this is not supposed to happen but for precaution (i.e. bad db migration or something)
     * 
     * @param lostIds
     * @param dashboardMetadata
     */
    private void cleanupLostGadgets(List<String> lostIds, DashboardMetadata dashboardMetadata) {
        DashboardColumnLayoutFormat layoutFormat = new DashboardColumnLayoutFormat(dashboardMetadata.encodedLayout().getValue());
        Builder updatedFormatBuilder = new DashboardColumnLayoutFormat.Builder(layoutFormat.getLayoutType());
        for (String id : layoutFormat.gadgetIds()) {
            if (!lostIds.contains(id)) {
                updatedFormatBuilder.bind(id, layoutFormat.getGadgetColumn(id));
            }
        }
        dashboardMetadata.encodedLayout().setValue(updatedFormatBuilder.build().getSerializedForm());
        Persistence.secureSave(dashboardMetadata);
        Persistence.service().commit();
    }

}
