/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.domain.property.asset.ParkingSpot;

public interface ParkingView {

    interface Presenter {

        DashboardView.Presenter getDashboardPresenter();

        IListerView.Presenter getSpotPresenter();

    }

    DashboardView getDashboardView();

    IListerView<ParkingSpot> getSpotView();
}
