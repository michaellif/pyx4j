/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.viewfactories;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.crud.building.BuildingEditorView;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerView;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaEditorView;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaViewerView;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerAreaViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerEditorView;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerListerView;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerViewerView;
import com.propertyvista.crm.client.ui.crud.building.lockers.LockerViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerEditorView;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerViewerView;
import com.propertyvista.crm.client.ui.crud.building.mech.BoilerViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorEditorView;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorViewerView;
import com.propertyvista.crm.client.ui.crud.building.mech.ElevatorViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofEditorView;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofViewerView;
import com.propertyvista.crm.client.ui.crud.building.mech.RoofViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingEditorView;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotEditorView;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotListerView;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotViewerView;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingSpotViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingViewerView;
import com.propertyvista.crm.client.ui.crud.building.parking.ParkingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.complex.ComplexEditorView;
import com.propertyvista.crm.client.ui.crud.complex.ComplexEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.complex.ComplexListerView;
import com.propertyvista.crm.client.ui.crud.complex.ComplexListerViewImpl;
import com.propertyvista.crm.client.ui.crud.complex.ComplexViewerView;
import com.propertyvista.crm.client.ui.crud.complex.ComplexViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanEditorView;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanViewerView;
import com.propertyvista.crm.client.ui.crud.floorplan.FloorplanViewerViewImpl;

public class BuildingViewFactory extends ViewFactoryBase {

    public static IView<? extends IEntity> instance(Class<? extends IView<? extends IEntity>> type) {
        if (!map.containsKey(type)) {
            if (BuildingListerView.class.equals(type)) {
                map.put(type, new BuildingListerViewImpl());
            } else if (BuildingEditorView.class.equals(type)) {
                map.put(type, new BuildingEditorViewImpl());
            } else if (BuildingViewerView.class.equals(type)) {
                map.put(type, new BuildingViewerViewImpl());

            } else if (ElevatorEditorView.class.equals(type)) {
                map.put(type, new ElevatorEditorViewImpl());
            } else if (ElevatorViewerView.class.equals(type)) {
                map.put(type, new ElevatorViewerViewImpl());

            } else if (BoilerViewerView.class.equals(type)) {
                map.put(type, new BoilerViewerViewImpl());
            } else if (BoilerEditorView.class.equals(type)) {
                map.put(type, new BoilerEditorViewImpl());

            } else if (RoofViewerView.class.equals(type)) {
                map.put(type, new RoofViewerViewImpl());
            } else if (RoofEditorView.class.equals(type)) {
                map.put(type, new RoofEditorViewImpl());

            } else if (ParkingViewerView.class.equals(type)) {
                map.put(type, new ParkingViewerViewImpl());
            } else if (ParkingEditorView.class.equals(type)) {
                map.put(type, new ParkingEditorViewImpl());

            } else if (ParkingSpotListerView.class.equals(type)) {
                map.put(type, new ParkingSpotListerViewImpl());
            } else if (ParkingSpotViewerView.class.equals(type)) {
                map.put(type, new ParkingSpotViewerViewImpl());
            } else if (ParkingSpotEditorView.class.equals(type)) {
                map.put(type, new ParkingSpotEditorViewImpl());

            } else if (LockerAreaViewerView.class.equals(type)) {
                map.put(type, new LockerAreaViewerViewImpl());
            } else if (LockerAreaEditorView.class.equals(type)) {
                map.put(type, new LockerAreaEditorViewImpl());

            } else if (LockerListerView.class.equals(type)) {
                map.put(type, new LockerListerViewImpl());
            } else if (LockerViewerView.class.equals(type)) {
                map.put(type, new LockerViewerViewImpl());
            } else if (LockerEditorView.class.equals(type)) {
                map.put(type, new LockerEditorViewImpl());

            } else if (FloorplanViewerView.class.equals(type)) {
                map.put(type, new FloorplanViewerViewImpl());
            } else if (FloorplanEditorView.class.equals(type)) {
                map.put(type, new FloorplanEditorViewImpl());

            } else if (ComplexListerView.class.equals(type)) {
                map.put(type, new ComplexListerViewImpl());
            } else if (ComplexViewerView.class.equals(type)) {
                map.put(type, new ComplexViewerViewImpl());
            } else if (ComplexEditorView.class.equals(type)) {
                map.put(type, new ComplexEditorViewImpl());
            }
        }
        return map.get(type);
    }
}
