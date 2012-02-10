/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.board.events;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import com.propertyvista.domain.property.asset.building.Building;

public class BuildingSelectionChangedEvent extends GwtEvent<BuildingSelectionChangedEventHandler> {

    public static final Type<BuildingSelectionChangedEventHandler> TYPE = new Type<BuildingSelectionChangedEventHandler>();

    private final List<Building> buildings;

    public BuildingSelectionChangedEvent(List<Building> buildings) {
        this.buildings = Collections.unmodifiableList(buildings);
    }

    @Override
    public Type<BuildingSelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(BuildingSelectionChangedEventHandler handler) {
        handler.onBuildingSelectionChanged(this);
    }

    public List<Building> getBuildings() {

        return buildings;
    }

}
