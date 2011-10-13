/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.parking;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.ParkingSpot;
import com.propertyvista.dto.ParkingDTO;

public class ParkingViewerViewImpl extends CrmViewerViewImplBase<ParkingDTO> implements ParkingViewerView {

    private final DashboardView dashboardView;

    private final IListerView<ParkingSpot> spotLister;

    public ParkingViewerViewImpl() {
        super(CrmSiteMap.Properties.Parking.class);

        dashboardView = new DashboardPanel();
        spotLister = new ListerInternalViewImplBase<ParkingSpot>(new ParkingSpotLister(/* readOnly */));

        // create/init/set main form here: 
        ParkingEditorForm form = new ParkingEditorForm(new CrmViewersComponentFactory(), this);
        form.initContent();
        setForm(form);
    }

    @Override
    public DashboardView getDashboardView() {
        return dashboardView;
    }

    @Override
    public IListerView<ParkingSpot> getSpotView() {
        return spotLister;
    }
}