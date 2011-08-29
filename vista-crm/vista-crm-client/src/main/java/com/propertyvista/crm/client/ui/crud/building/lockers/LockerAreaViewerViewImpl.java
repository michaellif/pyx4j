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
package com.propertyvista.crm.client.ui.crud.building.lockers;

import com.pyx4j.site.client.ui.crud.IListerView;
import com.pyx4j.site.client.ui.crud.ListerInternalViewImplBase;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaViewerViewImpl extends CrmViewerViewImplBase<LockerAreaDTO> implements LockerAreaViewerView {

    private final DashboardView dashboardView;

    private final IListerView<Locker> lockerLister;

    public LockerAreaViewerViewImpl() {
        super(CrmSiteMap.Properties.LockerArea.class);

        dashboardView = new DashboardPanel();
        lockerLister = new ListerInternalViewImplBase<Locker>(new LockerLister(/* readOnly */));

        // create/init/set main form here: 
        LockerAreaEditorForm form = new LockerAreaEditorForm(new CrmViewersComponentFactory(), this);
        form.initialize();
        setForm(form);
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