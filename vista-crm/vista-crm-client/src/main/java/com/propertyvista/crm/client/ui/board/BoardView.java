/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.board;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;

import com.propertyvista.crm.client.ui.board.events.HasBuildingSelectionChangedEventHandlers;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public interface BoardView extends IsWidget, HasBuildingSelectionChangedEventHandlers, IBuildingFilterContainer {

    public interface Presenter {

        void populate();

        void populate(Key boardId);

        void save();

        void print();

    }

    void setPresenter(Presenter presenter);

    void populate(DashboardMetadata dashboardMetadata);

    void stop();

    void setBuildings(List<Building> buildings, boolean fireEvent);

    List<Building> getSelectedBuildings();

    DashboardMetadata getDashboardMetadata();

    EventBus getEventBus();

    void onSaveSuccess();

    // may return TRUE in case of processed event and no need to re-throw the exception further.
    // FALSE - re-throws the exception (new UnrecoverableClientError(caught)).
    boolean onSaveFail(Throwable caught);

}