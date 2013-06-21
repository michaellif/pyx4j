/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.dashboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat.Builder;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.AccessDeniedGagetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetDescription;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata.RefreshInterval;
import com.propertyvista.misc.VistaTODO;

public class DashboardManagementFacadeImpl implements DashboardManagementFacade {

    private static final I18n i18n = I18n.get(DashboardManagementFacadeImpl.class);

    private final GadgetStorageFacade gadgetStorageFacade;

    public DashboardManagementFacadeImpl(GadgetStorageFacade gadgetStorageFacade) {
        this.gadgetStorageFacade = gadgetStorageFacade;
    }

    public DashboardManagementFacadeImpl() {
        this(ServerSideFactory.create(GadgetStorageFacade.class));
    }

    @Override
    public DashboardMetadata retrieveMetadata(DashboardMetadata dashboardMetadataStub) {
        assert (dashboardMetadataStub != null);
        DashboardMetadata dashboardMetadata;
        if (dashboardMetadataStub.getPrimaryKey().asLong() != -1) {
            dashboardMetadata = Persistence.secureRetrieve(DashboardMetadata.class, dashboardMetadataStub.getPrimaryKey());
        } else {
            dashboardMetadata = retrieveDefaultDashboardMetadata();
        }
        if (dashboardMetadata == null) {
            return null;
        }

        List<String> gadgetIds = new LinkedList<String>(new DashboardColumnLayoutFormat(dashboardMetadata.encodedLayout().getValue()).gadgetIds());
        dashboardMetadata.gadgetMetadataList().addAll(secureRetrieveGadgets(gadgetIds));
        if (!gadgetIds.isEmpty()) {
            cleanupLostGadgets(gadgetIds, dashboardMetadata);
        }
        return dashboardMetadata;
    }

    @Override
    public DashboardMetadata saveDashboardMetadata(DashboardMetadata dashboardMetadata) {
        if (dashboardMetadata.getPrimaryKey() == null) {
            throw new Error("trying to save new dashboard metadata: '" + DashboardMetadataCrudService.class.getSimpleName()
                    + "' must be used to create new dashboards");
        }

        // Compute the difference of gadgets: new vs. deleted and save the result        
        DashboardMetadata oldDashboardMetadata = Persistence.secureRetrieve(DashboardMetadata.class, dashboardMetadata.getPrimaryKey());
        DashboardColumnLayoutFormat oldLayout = new DashboardColumnLayoutFormat(oldDashboardMetadata.encodedLayout().getValue());
        DashboardColumnLayoutFormat newLayout = new DashboardColumnLayoutFormat(dashboardMetadata.encodedLayout().getValue());

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
        Persistence.secureSave(dashboardMetadata);

        for (String gadgetId : deletedGadgetIds) {
            gadgetStorageFacade.delete(gadgetId);
        }

        return dashboardMetadata;
    }

    @Override
    public void setDashboardOwner(DashboardMetadata dashboardMetadata, Key newOwnerPk) {
        for (String gadgetId : new DashboardColumnLayoutFormat(dashboardMetadata.encodedLayout().getValue()).gadgetIds()) {
            GadgetMetadata gadgetMetadata = gadgetStorageFacade.load(gadgetId);
            if (gadgetMetadata != null) {
                gadgetMetadata.ownerUser().setPrimaryKey(newOwnerPk);
                gadgetStorageFacade.save(gadgetMetadata, true);
            }
        }
        dashboardMetadata.ownerUser().setPrimaryKey(newOwnerPk);
        Persistence.service().persist(dashboardMetadata);
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
            GadgetMetadata gadgetMetadata = gadgetStorageFacade.load(gadgetIterator.next());
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
            if (VistaTODO.VISTA_3093_ARREARS_STATUS_GADGET_DEPRECATION & gadgetMetadata.getInstanceValueClass().equals(ArrearsStatusGadgetMetadata.class)) {
                ArrearsSummaryGadgetMetadata arreasSummaryHackGadget = EntityFactory.create(ArrearsSummaryGadgetMetadata.class);
                arreasSummaryHackGadget.gadgetId().setValue(gadgetMetadata.gadgetId().getValue());
                arreasSummaryHackGadget.refreshInterval().setValue(RefreshInterval.Never);
                return arreasSummaryHackGadget;
            } else {
                return gadgetMetadata;
            }
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
    }

}
