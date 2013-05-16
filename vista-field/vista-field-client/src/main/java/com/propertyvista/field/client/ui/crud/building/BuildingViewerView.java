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
package com.propertyvista.field.client.ui.crud.building;

import com.pyx4j.site.client.ui.prime.form.IViewer;
import com.pyx4j.site.client.ui.prime.lister.ILister;

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
import com.propertyvista.field.rpc.dto.billing.BillingCycleDTO;

public interface BuildingViewerView extends IViewer<BuildingDTO> {

    interface Presenter extends IViewer.Presenter {

//        IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings);
    }

    ILister<FloorplanDTO> getFloorplanListerView();

    ILister<AptUnitDTO> getUnitListerView();

    ILister<ElevatorDTO> getElevatorListerView();

    ILister<BoilerDTO> getBoilerListerView();

    ILister<RoofDTO> getRoofListerView();

    ILister<ParkingDTO> getParkingListerView();

    ILister<LockerAreaDTO> getLockerAreaListerView();

    ILister<Service> getServiceListerView();

    ILister<Feature> getFeatureListerView();

    ILister<Concession> getConcessionListerView();

    ILister<BillingCycleDTO> getBillingCycleListerView();
}
