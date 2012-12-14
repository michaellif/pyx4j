/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.ListerActivityBase;

import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementListerView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementListerActivity extends ListerActivityBase<DashboardMetadata> {

    @SuppressWarnings("unchecked")
    public DashboardManagementListerActivity(Place place) {
        super(place, (DashboardManagementListerView) DashboardViewFactory.instance(DashboardManagementListerView.class), (AbstractCrudService<DashboardMetadata>) GWT
                .create(DashboardMetadataCrudService.class), DashboardMetadata.class);

    }

    @Override
    protected void onDeleted(Key itemID, boolean isSuccessful) {
        super.onDeleted(itemID, isSuccessful);
        if (isSuccessful) {
            AppSite.instance();
            AppSite.getEventBus().fireEvent(new BoardUpdateEvent());
        }
    }
}
