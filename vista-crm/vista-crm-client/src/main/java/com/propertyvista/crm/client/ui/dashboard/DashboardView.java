/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.dashboard;

import java.util.Vector;

import com.pyx4j.site.client.backoffice.ui.prime.dashboard.IDashboardView;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public interface DashboardView extends IDashboardView {

    public interface Presenter extends IDashboardView.IDashboardPresenter {

    }

    void setPresenter(Presenter presenter);

    void setDashboardMetadata(DashboardMetadata dashboardMetadata);

    DashboardMetadata getDashboardMetadata();

    Vector<Building> getSelectedBuildingsStubs();

}