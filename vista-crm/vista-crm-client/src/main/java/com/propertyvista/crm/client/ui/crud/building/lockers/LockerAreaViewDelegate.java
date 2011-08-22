/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.lockers;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.domain.property.asset.Locker;

public class LockerAreaViewDelegate implements LockerAreaView {

    private final DashboardView dashboardView;

    private final IListerView<Locker> lockerLister;

    public LockerAreaViewDelegate(boolean readOnly) {
        dashboardView = new DashboardPanel();
        lockerLister = new ListerInternalViewImplBase<Locker>(new LockerLister(/* readOnly */));
    }

    @Override
    public DashboardView getDashboardView() {
        return dashboardView;
    }

    @Override
    public IListerView<Locker> getLockerView() {
        return lockerLister;
    }
}
