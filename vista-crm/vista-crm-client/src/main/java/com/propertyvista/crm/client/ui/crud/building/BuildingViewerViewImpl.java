/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import com.pyx4j.site.client.ui.crud.IListerView;

import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offeringnew.Concession;
import com.propertyvista.domain.financial.offeringnew.Feature;
import com.propertyvista.domain.financial.offeringnew.Service;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingViewerViewImpl extends CrmViewerViewImplBase<BuildingDTO> implements BuildingViewerView {

    private final BuildingViewDelegate delegate;

    public BuildingViewerViewImpl() {
        super(CrmSiteMap.Properties.Building.class);

        delegate = new BuildingViewDelegate(true);

        // create/init/set main form here: 
        CrmEntityForm<BuildingDTO> form = new BuildingEditorForm(new CrmViewersComponentFactory(), this);
        form.initialize();
        setForm(form);
    }

    @Override
    public DashboardView getDashboardView() {
        return delegate.getDashboardView();
    }

    @Override
    public IListerView<FloorplanDTO> getFloorplanListerView() {
        return delegate.getFloorplanListerView();
    }

    @Override
    public IListerView<AptUnitDTO> getUnitListerView() {
        return delegate.getUnitListerView();
    }

    @Override
    public IListerView<ElevatorDTO> getElevatorListerView() {
        return delegate.getElevatorListerView();
    }

    @Override
    public IListerView<BoilerDTO> getBoilerListerView() {
        return delegate.getBoilerListerView();
    }

    @Override
    public IListerView<RoofDTO> getRoofListerView() {
        return delegate.getRoofListerView();
    }

    @Override
    public IListerView<ParkingDTO> getParkingListerView() {
        return delegate.getParkingListerView();
    }

    @Override
    public IListerView<LockerAreaDTO> getLockerAreaListerView() {
        return delegate.getLockerAreaListerView();
    }

    @Override
    public IListerView<Service> getServiceListerView() {
        return delegate.getServiceListerView();
    }

    @Override
    public IListerView<Feature> getFeatureListerView() {
        return delegate.getFeatureListerView();
    }

    @Override
    public IListerView<Concession> getConcessionListerView() {
        return delegate.getConcessionListerView();
    }
}
