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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.building.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class CrmBoardViewImpl extends BoardViewImpl implements CrmBoardView {

    private final static I18n i18n = I18n.get(CrmBoardViewImpl.class);

    private final CrmTitleBar header;

    private final BoardViewActionsBar actionsBar;

    private final Button selectBuildingsButton;

    private Presenter presenter;

    private Button clearBuildingsButton;

    public CrmBoardViewImpl(BoardBase board) {

        addNorth(header = new CrmTitleBar(), CrmTheme.defaultHeaderHeight);
        header.setHeight("100%");

        addNorth(actionsBar = new BoardViewActionsBar(), CrmTheme.defaultHeaderHeight);
        actionsBar.add(new Button(i18n.tr("Refresh"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.save();
            }
        }));
        actionsBar.add(new Button(i18n.tr("Set Date..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new SelectDateDialog().show();
            }
        }));

        actionsBar.add(clearBuildingsButton = new Button(i18n.tr("All Buildings"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getData().buildings().clear();
                presenter.save();
            }
        }));
        clearBuildingsButton.setVisible(false); // don't show this button while dashboard waits for population

        actionsBar.add(selectBuildingsButton = new Button(i18n.tr("Add Buildings..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new BuildingSelectorDialog(new ArrayList<Building>(1)) {
                    @Override
                    public boolean onClickOk() {
                        DashboardMetadata metadata = getData();
                        for (Building building : getSelectedItems()) {
                            metadata.buildings().add(building.getPrimaryKey());
                        }
                        presenter.save();
                        return true;
                    }

                    @Override
                    protected void setFilters(List<DataTableFilterData> filters) {
                        for (Key pk : getData().buildings()) {
                            filters.add(new DataTableFilterData(proto().id().getPath(), Operators.isNot, pk));
                        }
                        super.setFilters(filters);
                    }
                }.show();
            }
        }));
        selectBuildingsButton.setVisible(false); // don't show this button while dashboard waits for population

        setBoard(board);
    }

    @Override
    public void populate(DashboardMetadata dashboardMetadata) {
        super.populate(dashboardMetadata);
        if (dashboardMetadata != null) {
            header.setCaption(asBoardCaption(dashboardMetadata));
            boolean isBuildingDashboard = dashboardMetadata.type().getValue() == DashboardType.building;
            selectBuildingsButton.setVisible(isBuildingDashboard);
            clearBuildingsButton.setVisible(isBuildingDashboard);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        super.setPresenter(presenter);
        this.presenter = presenter;
    }

    protected void onSetStatusDate(LogicalDate statusDate) {
        getData().statusDate().setValue(statusDate);
        presenter.save();
    }

    /**
     * @param dashboardMetadata
     *            <code>null<code> must be handled in appropriate way.
     * @return
     */
    protected String asBoardCaption(DashboardMetadata dashboardMetadata) {
        if (dashboardMetadata == null) {
            return i18n.tr("No Dashboard");
        } else {
            String name = !dashboardMetadata.name().isNull() ? dashboardMetadata.name().getValue() : i18n.tr("Anoymous Dashboard");
            LogicalDate date = dashboardMetadata.statusDate().isNull() ? new LogicalDate() : dashboardMetadata.statusDate().getValue();
            String buildingsView = dashboardMetadata.buildingsStringView().getValue();
            if (dashboardMetadata.type().getValue() == DashboardType.building) {
                return SimpleMessageFormat.format(i18n.tr("{0} on {1} for the following buildings: {2}"), name, date, buildingsView);
            } else {
                return SimpleMessageFormat.format(i18n.tr("{0} on {1}"), name, date);
            }
        }
    }

    protected static class BoardViewActionsBar implements IsWidget {

        private final HorizontalPanel barContainer;

        private final HorizontalPanel bar;

        public BoardViewActionsBar() {
            bar = new HorizontalPanel();
            bar.setSpacing(5);

            barContainer = new HorizontalPanel();
            barContainer.setWidth("100%");
            barContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            // add a dummy widget so that new added buttons will be placed at the right            
            barContainer.add(bar);
        }

        @Override
        public Widget asWidget() {
            return barContainer;
        }

        public void add(Button button) {
            bar.add(button);
        }

    }

    protected class SelectDateDialog extends OkCancelDialog {

        private final CDatePicker date = new CDatePicker();

        private final RadioButton now = new RadioButton("date", i18n.tr("Now"));

        private final RadioButton select = new RadioButton("date", i18n.tr("Select:"));

        public SelectDateDialog() {
            super(i18n.tr("Select Date"));
            setBody(createDateSelectBody());
        }

        private Widget createDateSelectBody() {
            FormFlexPanel body = new FormFlexPanel();
            int row = -1;
            body.setWidget(++row, 0, now);
            now.setValue(getData().statusDate().isNull());
            body.setWidget(++row, 0, select);
            body.setWidget(row, 1, date);
            body.getFlexCellFormatter().getElement(row, 1).getStyle().setPaddingLeft(1, Unit.EM);

            if (!getData().statusDate().isNull()) {
                select.setValue(true);
                date.setValue(getData().statusDate().getValue());
            } else {
                select.setValue(false);
                date.setValue(new LogicalDate());
            }
            return body;
        }

        @Override
        public boolean onClickOk() {
            if (now.getValue()) {
                onSetStatusDate(null);
            } else {
                LogicalDate oldDate = getData().statusDate().getValue();
                LogicalDate newDate = new LogicalDate(date.getValue());
                if (!EqualsHelper.equals(oldDate, newDate)) {
                    onSetStatusDate(newDate);
                }
            }
            return true;
        }

    }

}
