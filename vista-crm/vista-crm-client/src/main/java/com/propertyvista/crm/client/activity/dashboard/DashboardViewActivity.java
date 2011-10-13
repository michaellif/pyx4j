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

import com.propertyvista.crm.client.activity.board.CrmBoardViewActivity;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.dashboard.BoardMetadataServiceBase;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;

public class DashboardViewActivity extends CrmBoardViewActivity<DashboardView> implements DashboardView.Presenter {

    private final DashboardMetadataService service = GWT.create(DashboardMetadataService.class);

    public DashboardViewActivity(Place place) {
        this((DashboardView) DashboardViewFactory.instance(DashboardView.class), place);
    }

    public DashboardViewActivity(DashboardView view) {
        this(view, null);
    }

    public DashboardViewActivity(DashboardView view, Place place) {
        super(view, place);
    }

    @Override
    protected BoardMetadataServiceBase getService() {
        return service;
    }
}