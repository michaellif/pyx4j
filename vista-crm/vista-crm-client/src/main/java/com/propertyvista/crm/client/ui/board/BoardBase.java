/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.board;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.BoardEvent.Reason;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class BoardBase extends DockLayoutPanel implements BoardView {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    public static enum StyleSuffix implements IStyleName {
        filtersPanel, actionsPanel, filtersDescription
    }

    private static final I18n i18n = I18n.get(BoardBase.class);

    private final HorizontalPanel actionsPanel;

    private final Button btnSave = new Button(i18n.tr("Save"));

    private CDatePicker datePicker;

    private final boolean showSaveButton = false; // true; // Save button used in test purpose only!..

    private final ScrollPanel scroll = new ScrollPanel();

    private IBoard board;

    protected Presenter presenter;

    private DashboardMetadata dashboardMetadata;

    private boolean filling;

    private final boolean readOnly;

    public BoardBase() {
        this(false);
    }

    public BoardBase(boolean readOnly) {
        super(Unit.EM);
        this.readOnly = readOnly;

        actionsPanel = new HorizontalPanel();
        actionsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
        actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actionsPanel.setWidth("100%");
        actionsPanel.add(new HTML()); // just for %-tage cells alignment...

        actionsPanel.add(createActionsWidget());

        addNorth(actionsPanel, 2);

        if (showSaveButton) {
            addAction(btnSave);
            btnSave.setEnabled(false);
            btnSave.addStyleName(btnSave.getStylePrimaryName() + CrmTheme.StyleSuffixEx.SaveButton);
            btnSave.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.save();
                }
            });
        }

        add(scroll);

        addAction(createDateStatusSelectWidget());

        setSize("100%", "100%");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(DashboardMetadata metadata) {
        dashboardMetadata = metadata;

        filling = true; // inhibit event processing while filling the dashboard

        board = createBoard();
        board.addEventHandler(new BoardEvent() {
            @Override
            public void onEvent(final Reason reason) {
                if (!filling) {
                    // use a deferred command so that the actual event processing unlinked from event!
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            procesDashboardEvent(reason);
                        }
                    });
                }
            }
        });

        // actual board filling: 
        if (dashboardMetadata != null && !dashboardMetadata.isEmpty()) {
            setLayout(dashboardMetadata.layoutType().getValue());
            // fill the dashboard with gadgets:
            for (GadgetMetadata gmd : dashboardMetadata.gadgets()) {
                IGadgetInstance gadget = Directory.createGadget(gmd);
                if (gadget != null) {
                    addGadget(gadget, gmd.docking().column().getValue());
                }
            }
        }

        scroll.setWidget(board);

        filling = false; // ok, filled already..
        btnSave.setEnabled(false);

        IGadgetIterator it = board.getGadgetIterator();
        while (it.hasNext()) {
            it.next().start(); // allow gadget execution... 
        }
    }

    @Override
    public void setBuildings(List<Building> buildings) {
        if (board != null && getDashboardMetadata() != null && getDashboardMetadata().type().getValue() == DashboardType.building) {
            List<Key> keys = new ArrayList<Key>(buildings.size());
            for (Building b : buildings) {
                keys.add(b.getPrimaryKey());
            }
            propagateBulidngsFiltering(keys);
        }
    }

    @Override
    public void setStatusDate(LogicalDate statusDate) {
        datePicker.setValue(statusDate);
    }

    public void addGadget(IGadgetInstance gadget) {
        bindGadget(gadget);
        board.addGadget(gadget);
    }

    public void addGadget(IGadgetInstance gadget, int column) {
        bindGadget(gadget);
        board.addGadget(gadget, column);
    }

    private void bindGadget(IGadgetInstance gadget) {
        gadget.setPresenter(presenter);
    }

    @Override
    public void stop() {
        if (board != null) {
            IGadgetIterator i = board.getGadgetIterator();
            while (i.hasNext()) {
                i.next().stop();
            }
        }
    }

    protected void propagateStatusDate() {
        if (board != null && getDashboardMetadata() != null) {
            IGadgetIterator i = board.getGadgetIterator();
            LogicalDate date = new LogicalDate(datePicker.getValue());
            while (i.hasNext()) {
                // FIXME merge IGadget and IGadgetInstance
                ((IGadgetInstance) i.next()).setStatusDate(date);
            }
        }
    }

    protected void propagateBulidngsFiltering(List<Key> buildings) {
        if (getDashboardMetadata() != null) {
            if (board != null) {
                IGadgetIterator i = board.getGadgetIterator();
                while (i.hasNext()) {
                    ((IBuildingBoardGadgetInstance) i.next()).setBuildings(buildings);
                }
            }
        }
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public DashboardMetadata getDashboardMetadata() {
        if (dashboardMetadata != null) {
            dashboardMetadata.layoutType().setValue(translateLayout(board.getLayout()));

            dashboardMetadata.gadgets().clear();
            IGadgetIterator it = board.getGadgetIterator();
            while (it.hasNext()) {
                IGadget gadget = it.next();
                if (gadget instanceof IGadgetInstance) {
                    GadgetMetadata gmd = ((IGadgetInstance) gadget).getMetadata(); // gadget meta should be up to date!.. 
                    gmd.docking().column().setValue(it.getColumn()); // update current gadget column...
                    dashboardMetadata.gadgets().add(gmd);
                }
            }
        }

        return dashboardMetadata;
    }

    public IBoard getBoard() {
        return board;
    }

    @Override
    public void onSaveSuccess() {
        btnSave.setEnabled(false);
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        // TODO Auto-generated method stub
        return false;
    }

    public void addAction(Widget action) {
        actionsPanel.setVisible(true);
        actionsPanel.insert(action, 1);
        actionsPanel.setCellWidth(action, "1%");
        action.getElement().getStyle().setMarginRight(1, Unit.EM);
    }

    public void remAction(Widget action) {
        actionsPanel.remove(action);
    }

    //
// Internals:
//

    protected Widget createDateStatusSelectWidget() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.setWidth("30em");
        datePicker = new CDatePicker();
        datePicker.setWidth("10em");
        datePicker.setValue(new LogicalDate());
        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                propagateStatusDate();
            }
        });
        panel.add(datePicker);

        return panel;
    }

    protected void procesDashboardEvent(Reason reason) {
        boolean save = true;

        switch (reason) {
        case addGadget:
            break;
        case removeGadget:
            break;
        case repositionGadget:
            save = isGadgetRepositioned();
            break;
        case updateGadget:
            break;
        case newLayout:
            save = (BoardBase.this.dashboardMetadata.layoutType().getValue() != translateLayout(board.getLayout()));
            break;
        }

        fireResizeRequests();

        if (save && !isReadOnly()) {
            if (showSaveButton) {
                btnSave.setEnabled(true); // user should manually save the state... 
            } else {
                presenter.save(); // automatic state saving...
            }
        }
    }

    private void fireResizeRequests() {
        IGadgetIterator i = board.getGadgetIterator();
        while (i.hasNext()) {
            i.next().onResize();
        }
    }

    protected boolean isGadgetRepositioned() {
        IGadgetIterator it = board.getGadgetIterator();
        for (GadgetMetadata gmd : this.dashboardMetadata.gadgets()) { // iterate on meta-data... 
            if (it.hasNext()) {
                IGadget gadget = it.next(); // get corresponding gadget from dashboard...
                // FIXME REALLY evil casting... need to do something about the IGagdget interface: add an ID and compare them or something like that...
                if (!((IGadgetInstance) gadget).getMetadata().equals(gmd) | it.getColumn() != gmd.docking().column().getValue()) {
                    return true; // not the same gadget!?.
                }
            } else {
                return true; // quantity doesn't match!?.
            }
        }
        return false; // all clear - nothing has changed...
    }

    /*
     * Implement those meaningful in derived classes:
     */
    protected abstract IBoard createBoard();

    protected abstract Widget createActionsWidget();

    protected abstract LayoutType translateLayout(BoardLayout layout);

    protected abstract void setLayout(LayoutType layout);
}