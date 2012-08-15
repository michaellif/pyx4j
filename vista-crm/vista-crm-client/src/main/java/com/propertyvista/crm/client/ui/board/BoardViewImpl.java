/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.board;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.site.client.ui.crud.misc.IMemento;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class BoardViewImpl extends ViewImplBase implements BoardView {

    protected BoardBase board = null;

    public BoardViewImpl() {
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setBoard(BoardBase board) {

        if (getContentPane() == null) { // finalise UI here:
            setContentPane(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (this.board == board) {
            return; // already!?.
        }

        this.board = board;

        LayoutPanel center = (LayoutPanel) getContentPane();
        center.clear(); // remove current board...
        center.add(board.asWidget());
    }

    @Override
    public void setPresenter(BoardView.Presenter presenter) {
        assert (board != null);
        board.setPresenter(presenter);
    }

    @Override
    public void populate(DashboardMetadata dashboardMetadata) {
        assert (board != null);
        board.populate(dashboardMetadata);
    }

    @Override
    public DashboardMetadata getDashboardMetadata() {
        assert (board != null);
        return board.getDashboardMetadata();
    }

    @Override
    public void onSaveSuccess() {
        board.onSaveSuccess();
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        return board.onSaveFail(caught);
    }

    @Override
    public void stop() {
        board.stop();
    }

    @Override
    public HandlerRegistration addBuildingSelectionChangedEventHandler(BuildingSelectionChangedEventHandler handler) {
        return board.addBuildingSelectionChangedEventHandler(handler);
    }

    @Override
    public void setBuildings(List<Building> buildings, boolean fireEvent) {
        board.setBuildings(buildings, fireEvent);
    }

    @Override
    public List<Building> getSelectedBuildingsStubs() {
        return board.getSelectedBuildingsStubs();
    }

    @Override
    public List<Building> getSelectedBuildings() {
        return board.getSelectedBuildings();
    }

    @Override
    public EventBus getEventBus() {
        return board.getEventBus();
    }

    @Override
    public IMemento getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeState(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void restoreState() {
        // TODO Auto-generated method stub

    }

}
