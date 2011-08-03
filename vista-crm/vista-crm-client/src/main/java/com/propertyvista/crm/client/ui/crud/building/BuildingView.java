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
import com.propertyvista.domain.financial.offeringnew.Concession;
import com.propertyvista.domain.financial.offeringnew.Feature;
import com.propertyvista.domain.financial.offeringnew.Service;
import com.propertyvista.dto.AptUnitDTO;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public interface BuildingView {

    interface Presenter {

        DashboardView.Presenter getDashboardPresenter();

        IListerView.Presenter getFloorplanPresenter();

        IListerView.Presenter getUnitPresenter();

        IListerView.Presenter getElevatorPresenter();

        IListerView.Presenter getBoilerPresenter();

        IListerView.Presenter getRoofPresenter();

        IListerView.Presenter getParkingPresenter();

        IListerView.Presenter getLockerAreaPresenter();

        IListerView.Presenter getServicePresenter();

        IListerView.Presenter getFeaturePresenter();

        IListerView.Presenter getConcessionPresenter();
    }

    DashboardView getDashboardView();

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
}
