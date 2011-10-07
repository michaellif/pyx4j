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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public class BoardViewImpl extends DockLayoutPanel implements BoardView {

    protected static I18n i18n = I18nFactory.getI18n(BoardViewImpl.class);

    protected BoardBase board = null;

    protected CrmTitleBar header = null;

    protected final Button setupBuildingAction;

    public BoardViewImpl() {
        super(Unit.EM);

        setupBuildingAction = new Button("Building&nbsp;Setup", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.info(i18n.tr("Setup"), i18n.tr("Selection of Building goes here!.."));
            }
        });
    }

    public BoardViewImpl(BoardBase board) {
        this();
        this.board = board;

        addNorth(header = new CrmTitleBar(""), VistaCrmTheme.defaultHeaderHeight);
        header.setHeight("100%"); // fill all that defaultHeaderHeight!..

        add(board);
        setSize("100%", "100%");
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
        board.remAction(setupBuildingAction);

        if (dashboardMetadata != null) {
            header.setCaption(dashboardMetadata.name().getStringView());
            if (dashboardMetadata.type().getValue() == DashboardType.building) {
                board.addAction(setupBuildingAction);
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
