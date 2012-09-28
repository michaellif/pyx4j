/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.rpc.server.LocalService;
import com.pyx4j.site.server.services.customization.CustomizationPersistenceHelper;

import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat.Builder;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadataHolder;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.generator.DashboardGenerator;

public class DashboardPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        DashboardGenerator generator = new DashboardGenerator();

        persistDashboards(generator.systemDashboards);
        persistDashboards(generator.buildingDashboards);

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(generator.systemDashboards.size() + generator.buildingDashboards.size()).append(" dashboards");
        return sb.toString();
    }

    @Override
    public String delete() {
        return deleteAll(DashboardMetadata.class);
    }

    void persistDashboards(Collection<DashboardMetadata> metadatas) {
        for (DashboardMetadata dm : metadatas) {
            persistDashboard(dm);
        }
    }

    void persistDashboard(DashboardMetadata dm) {
        // simulate creation of gadgets and dashboard:
        // first create a dashboard without any gadgets,
        // then save gadgets separately
        // then bind them to dashboard via service
        List<GadgetMetadata> gadgetMetadataList = new ArrayList<GadgetMetadata>(dm.gadgetMetadataList());
        dm.gadgetMetadataList().clear();
        Persistence.service().persist(dm);

        // save gadgets
        CustomizationPersistenceHelper<GadgetMetadata> gadgetStorage = new CustomizationPersistenceHelper<GadgetMetadata>(GadgetMetadataHolder.class,
                GadgetMetadata.class);
        for (GadgetMetadata gadgetMetadata : gadgetMetadataList) {
            gadgetStorage.save(gadgetMetadata.gadgetId().getValue(), gadgetMetadata, true);
        }

        // add gadgets in dashboard metadata as 'new gadgets' (without parent dashboardId), define basic layout and save them in traditional way via service
        Builder layoutBuilder = new DashboardColumnLayoutFormat.Builder(LayoutType.One);
        for (GadgetMetadata gadgetMetadata : gadgetMetadataList) {
            layoutBuilder.bind(gadgetMetadata.gadgetId().getValue(), 0);
        }
        dm.encodedLayout().setValue(layoutBuilder.build().getSerializedForm());

        LocalService.create(DashboardMetadataService.class).saveDashboardMetadata(new AsyncCallback<DashboardMetadata>() {

            @Override
            public void onSuccess(DashboardMetadata result) {

            }

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

        }, dm);

    }
}
