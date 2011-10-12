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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class BoardViewImpl extends DockLayoutPanel implements BoardView {

    protected static I18n i18n = I18n.get(BoardViewImpl.class);

    protected BoardBase board = null;

    protected final CrmTitleBar header = new CrmTitleBar("");

    protected final SimplePanel filter = new SimplePanel(new HTML("... Building filters go here ..."));

    public BoardViewImpl() {
        super(Unit.EM);
    }

    public BoardViewImpl(BoardBase board) {
        this();

        addNorth(header, VistaCrmTheme.defaultHeaderHeight);
        header.setHeight("100%"); // fill all that defaultHeaderHeight!..

        addNorth(filter, 0);
        filter.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersPanel);

        setBoard(board);
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
    public void fill(DashboardMetadata dashboardMetadata) {
        assert (board != null);
        board.fill(dashboardMetadata);

        if (dashboardMetadata != null) {
            header.setCaption(dashboardMetadata.name().getStringView());

            setWidgetSize(filter, 0);
            if (dashboardMetadata.type().getValue() == DashboardType.building) {
                setWidgetSize(filter, VistaCrmTheme.defaultTabHeight);
            }
        }
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
}
