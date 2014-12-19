/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 15, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.List;

import com.propertyvista.crm.client.ui.board.events.HasBuildingSelectionChangedEventHandlers;
import com.propertyvista.domain.property.asset.building.Building;

public interface IBuildingFilterContainer extends HasBuildingSelectionChangedEventHandlers {

    /**
     * @return the stubs of the buildings that this view was set up to display, can't be <code>null</code>, empty list denotes all buildings.
     */
    List<Building> getSelectedBuildingsStubs();

}
