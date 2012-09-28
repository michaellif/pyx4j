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
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.dto.dashboard.GadgetDescriptorDTO;
import com.propertyvista.crm.rpc.services.dashboard.AbstractMetadataService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.base.BuildingGadget;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.server.common.gadgets.GadgetMetadataRepository;

abstract class AbstractMetadataServiceImpl implements AbstractMetadataService {

    private final static I18n i18n = I18n.get(AbstractMetadataServiceImpl.class);

    @Override
    public void listMetadata(AsyncCallback<Vector<DashboardMetadata>> callback) {
        EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
        criteria.or().left(PropertyCriterion.eq(criteria.proto().user(), CrmAppContext.getCurrentUserPrimaryKey()))
                .right(PropertyCriterion.eq(criteria.proto().isShared(), true));
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

        DashboardColumnLayoutFormat format = new DashboardColumnLayoutFormat(dm.encodedLayout().getValue());
        for (String id : format.gadgetIds()) {
            GadgetMetadata gm = gadgetStorage().load(id);
            if (gm != null) {
                dm.gadgetMetadataList().add(gm);
            } else {
                throw new Error("gadget metadtata '" + id + "' was not found");
            }
        }

        callback.onSuccess(dm);
    }

    /**
     * calcualte the difference of gadgets new/deleted and save the result
     */
    @Override
    public void saveDashboardMetadata(AsyncCallback<DashboardMetadata> callback, DashboardMetadata dm) {
        // TODO refactor
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

        CustomizationPersistenceHelper<GadgetMetadata> gadgetStorage = gadgetStorage();

        // delete deleted gadgets
        for (String gadgetId : deletedGadgetIds) {
            gadgetStorage.delete(gadgetId);
        }
        // TODO add gadget -> dashboard reference

        Persistence.service().commit();

        callback.onSuccess(dm);
    }

    @Override
    public void createGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata proto) {
        callback.onSuccess(GadgetMetadataRepository.get().createGadgetMetadata(proto));
    }

    @Override
    public void retrieveGadgetMetadata(AsyncCallback<GadgetMetadata> callback, Key gadgetMetadataId) {
        throw new Error("Not Implemented"); // actually this method shouldn't exist: must be refactored
    }

    @Override
    public void saveGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata gadgetMetadata) {
        if (gadgetMetadata == null) {
            throw new Error("Got null instead of gadget metadata");
        }
        if (gadgetMetadata.gadgetId().isNull()) {
            throw new Error("got gadget metadata with no defined id:" + gadgetMetadata.toString());
        }
        gadgetStorage().save(gadgetMetadata.gadgetId().getValue(), gadgetMetadata, true);
        callback.onSuccess(gadgetMetadata);
        Persistence.service().commit();
    }

    @Override
    public void listAvailableGadgets(AsyncCallback<Vector<GadgetDescriptorDTO>> callback, DashboardType boardType) {
        Vector<GadgetDescriptorDTO> descriptors = new Vector<GadgetDescriptorDTO>();

        for (Class<? extends GadgetMetadata> klass : GadgetMetadataRepository.get().getGadgetMetadataClasses()) {
            GadgetDescription gadgetDescription = klass.getAnnotation(GadgetDescription.class);
            // TODO add translation
            if ((boardType == DashboardType.building & BuildingGadget.class.isAssignableFrom(klass))
                    | (boardType != DashboardType.building & !BuildingGadget.class.isAssignableFrom(klass))) {
                GadgetMetadata proto = EntityFactory.getEntityPrototype(klass);
                descriptors.add(new GadgetDescriptorDTO(//@formatter:off
                        proto.getEntityMeta().getCaption(),
                        gadgetDescription != null ? gadgetDescription.description() : i18n.tr(""),
                        Arrays.asList(gadgetDescription != null ? gadgetDescription.keywords() : new String[0]),
                        proto
                ));//@formatter:on
            }
        }
        callback.onSuccess(descriptors);
    }

    protected abstract DashboardMetadata retrieveDefaultMetadata();

    private static CustomizationPersistenceHelper<GadgetMetadata> gadgetStorage() {
        return new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class, GadgetMetadata.class);
    }

}
