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

import com.pyx4j.site.client.ui.crud.form.IViewerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView;

import com.propertyvista.crm.client.ui.crud.building.dashboard.BuildingDashboardView;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public interface BuildingViewerView extends IViewerView<BuildingDTO> {

    interface Presenter extends IViewerView.Presenter {

        DashboardView.Presenter getDashboardPresenter();

        IListerView.Presenter<FloorplanDTO> getFloorplanPresenter();

        IListerView.Presenter<AptUnitDTO> getUnitPresenter();

        IListerView.Presenter<ElevatorDTO> getElevatorPresenter();

        IListerView.Presenter<BoilerDTO> getBoilerPresenter();

        IListerView.Presenter<RoofDTO> getRoofPresenter();

        IListerView.Presenter<ParkingDTO> getParkingPresenter();

        IListerView.Presenter<LockerAreaDTO> getLockerAreaPresenter();

        IListerView.Presenter<Service> getServicePresenter();

        IListerView.Presenter<Feature> getFeaturePresenter();

        IListerView.Presenter<Concession> getConcessionPresenter();

        IListerView.Presenter<BillingRun> getBillingRunPresenter();

        void runBill();
    }

    BuildingDashboardView getDashboardView();

    IListerView<FloorplanDTO> getFloorplanListerView();

    IListerView<AptUnitDTO> getUnitListerView();

    IListerView<ElevatorDTO> getElevatorListerView();

    IListerView<BoilerDTO> getBoilerListerView();

    IListerView<RoofDTO> getRoofListerView();

    IListerView<ParkingDTO> getParkingListerView();

    IListerView<LockerAreaDTO> getLockerAreaListerView();

    IListerView<Service> getServiceListerView();

    IListerView<Feature> getFeatureListerView();

    IListerView<Concession> getConcessionListerView();

    IListerView<BillingRun> getBillingRunListerView();
}
