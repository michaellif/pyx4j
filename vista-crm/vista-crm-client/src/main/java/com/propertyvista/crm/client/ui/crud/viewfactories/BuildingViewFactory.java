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

import com.propertyvista.crm.client.ui.crud.building.BoilerEditorView;
import com.propertyvista.crm.client.ui.crud.building.BoilerEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BoilerViewerView;
import com.propertyvista.crm.client.ui.crud.building.BoilerViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorView;
import com.propertyvista.crm.client.ui.crud.building.BuildingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerView;
import com.propertyvista.crm.client.ui.crud.building.BuildingListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerView;
import com.propertyvista.crm.client.ui.crud.building.BuildingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ElevatorEditorView;
import com.propertyvista.crm.client.ui.crud.building.ElevatorEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ElevatorViewerView;
import com.propertyvista.crm.client.ui.crud.building.ElevatorViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaEditorView;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaViewerView;
import com.propertyvista.crm.client.ui.crud.building.LockerAreaViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerEditorView;
import com.propertyvista.crm.client.ui.crud.building.LockerEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerListerView;
import com.propertyvista.crm.client.ui.crud.building.LockerListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.LockerViewerView;
import com.propertyvista.crm.client.ui.crud.building.LockerViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingEditorView;
import com.propertyvista.crm.client.ui.crud.building.ParkingEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotEditorView;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotListerView;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotListerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotViewerView;
import com.propertyvista.crm.client.ui.crud.building.ParkingSpotViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.ParkingViewerView;
import com.propertyvista.crm.client.ui.crud.building.ParkingViewerViewImpl;
import com.propertyvista.crm.client.ui.crud.building.RoofEditorView;
import com.propertyvista.crm.client.ui.crud.building.RoofEditorViewImpl;
import com.propertyvista.crm.client.ui.crud.building.RoofViewerView;
import com.propertyvista.crm.client.ui.crud.building.RoofViewerViewImpl;
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
            }
        }
        return map.get(type);
    }
}
