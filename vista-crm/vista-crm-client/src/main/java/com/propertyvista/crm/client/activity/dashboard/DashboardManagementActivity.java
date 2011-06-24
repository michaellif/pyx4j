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

import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.dashboard.DashboardManagementView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.DashboardCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementActivity extends ListerActivityBase<DashboardMetadata> {

    @SuppressWarnings("unchecked")
    public DashboardManagementActivity(Place place) {
        super((DashboardManagementView) DashboardViewFactory.instance(DashboardManagementView.class), (AbstractCrudService<DashboardMetadata>) GWT
                .create(DashboardCrudService.class), DashboardMetadata.class);
        withPlace(place);
    }
}
