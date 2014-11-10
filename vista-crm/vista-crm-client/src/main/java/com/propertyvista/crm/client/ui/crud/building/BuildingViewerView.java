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

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.backoffice.ui.prime.form.IViewer;

import com.propertyvista.crm.client.visor.communityevent.CommunityEventVisorController;
import com.propertyvista.crm.client.visor.dashboard.IDashboardVisorController;
import com.propertyvista.crm.client.visor.maintenance.MaintenanceRequestVisorController;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;

public interface BuildingViewerView extends IViewer<BuildingDTO> {

    public interface BuildingViewerPresenter extends BuildingPresenterCommon, IViewer.Presenter {

        void setMerchantAccount(AsyncCallback<VoidSerializable> callback, MerchantAccount merchantAccountStub);

        MaintenanceRequestVisorController getMaintenanceRequestVisorController();

        CommunityEventVisorController getCommunityEventVisorController();

        IDashboardVisorController getDashboardController(DashboardMetadata dashboardMetadata, List<Building> buildings);

        void updateFromYardi();

        void importBuildingData();

        void exportBuildingData();
    }

    FloorplanLister getFloorplanListerView();

    UnitLister getUnitListerView();

    ElevatorLister getElevatorListerView();

    BoilerLister getBoilerListerView();

    RoofLister getRoofListerView();

    ParkingLister getParkingListerView();

    LockerAreaLister getLockerAreaListerView();

    ServiceLister getServiceListerView();

    FeatureLister getFeatureListerView();

    ConcessionLister getConcessionListerView();

    BillingCycleLister getBillingCycleListerView();

    void selectMerchantAccount();
}
