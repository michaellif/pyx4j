/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.board.BoardViewActivity;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.BoardMetadataServiceBase;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class DashboardViewActivity extends BoardViewActivity<DashboardView> implements DashboardView.Presenter {

    private final DashboardMetadataService service = GWT.create(DashboardMetadataService.class);

    public DashboardViewActivity(Place place) {
        this((DashboardView) DashboardViewFactory.instance(DashboardView.class), place);
    }

    public DashboardViewActivity(DashboardView view, Place place) {
        super(view, place);
    }

    @Override
    public DashboardViewActivity withPlace(Place place) {
        entityId = null;
        dashboardType = null;

        String id;
        if ((id = ((AppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(id);
        } else if (place instanceof CrmSiteMap.Dashboard.System) {
            dashboardType = DashboardType.system;
        } else if (place instanceof CrmSiteMap.Dashboard.Building) {
            dashboardType = DashboardType.building;
        }

        assert (entityId != null || dashboardType != null);
        return this;
    }

    @Override
    protected BoardMetadataServiceBase getService() {
        return service;
    }
}