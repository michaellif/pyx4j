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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.dashboard.DashboardMetadata;

public class BoardViewImpl extends DockLayoutPanel implements BoardView {

    private static final I18n i18n = I18n.get(BoardViewImpl.class);

    protected BoardBase board = null;

    public BoardViewImpl() {
        super(Unit.EM);
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    protected void setBoard(BoardBase board) {

        if (getCenter() == null) { // finalise UI here:
            add(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (this.board == board) {
            return; // already!?.
        }

        this.board = board;

        LayoutPanel center = (LayoutPanel) getCenter();
        center.clear(); // remove current board...
        center.add(board.asWidget());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        assert (board != null);
        board.setPresenter(presenter);
    }

    @Override
    public void populate(DashboardMetadata dashboardMetadata) {
        assert (board != null);
        board.populate(dashboardMetadata);
    }

    @Override
    public DashboardMetadata getData() {
        assert (board != null);
        return board.getData();
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
}
