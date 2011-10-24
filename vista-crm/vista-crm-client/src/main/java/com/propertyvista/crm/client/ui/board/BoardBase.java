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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.BoardEvent.Reason;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.gadgets.GadgetsFactory;
import com.propertyvista.crm.client.ui.gadgets.IGadgetBase;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public abstract class BoardBase extends DockLayoutPanel implements BoardView {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    public static enum StyleSuffix implements IStyleName {
        filtersPanel, actionsPanel, filtersDescription
    }

    private static I18n i18n = I18n.get(BoardBase.class);

    private final HorizontalPanel actionsPanel;

    private final Button btnSave = new Button(i18n.tr("Save"));

    private final boolean showSaveButton = false; // true; // Save button used in test purpose only!..

    private final ScrollPanel scroll = new ScrollPanel();

    private IBoard board;

    private Presenter presenter;

    private DashboardMetadata dashboardMetadata;

    private boolean filling;

    public BoardBase() {
        super(Unit.EM);

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
            btnSave.addStyleName(btnSave.getStylePrimaryName() + VistaCrmTheme.StyleSuffixEx.SaveButton);
            btnSave.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.save();
                }
            });
        }

        add(scroll);

        setSize("100%", "100%");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void fill(DashboardMetadata metadata) {
        dashboardMetadata = metadata;

        filling = true; // inhibit event processing while filling the dashboard

        board = createBoard();
        board.addEventHandler(new BoardEvent() {
            @Override
            public void onEvent(final Reason reason) {
                // use a deferred command so that the actual event processing unlinked from event!
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (!filling) {
                            procesDashboardEvent(reason);
                        }
                    }
                });
            }
        });

        // actual board filling: 
        if (dashboardMetadata != null && !dashboardMetadata.isEmpty()) {
            setLayout(dashboardMetadata.layoutType().getValue());
            // fill the dashboard with gadgets:
            for (GadgetMetadata gmd : dashboardMetadata.gadgets()) {
                IGadgetBase gadget = GadgetsFactory.createGadget(gmd.type().getValue(), gmd);
                if (gadget != null) {
                    gadget.setPresenter(presenter);
                    board.addGadget(gadget, gmd.column().getValue());
                    gadget.start(); // allow gadget execution... 
                }
            }
        }

        scroll.setWidget(board);

        // use a deferred command so that browser actually build board DOM and we do not process unnecessary board events!
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                filling = false; // ok, filled already..
                btnSave.setEnabled(false);
            }
        });
    }

    @Override
    public DashboardMetadata getData() {
        if (dashboardMetadata != null) {
            dashboardMetadata.layoutType().setValue(translateLayout(board.getLayout()));
            dashboardMetadata.gadgets().clear();

            IGadgetIterator it = board.getGadgetIterator();
            while (it.hasNext()) {
                IGadget gadget = it.next();
                if (gadget instanceof IGadgetBase) {
                    GadgetMetadata gmd = ((IGadgetBase) gadget).getGadgetMetadata(); // gadget meta should be up to date!.. 
                    gmd.column().setValue(it.getColumn()); // update current gadget column...
                    dashboardMetadata.gadgets().add(gmd);
                }
            }
        }

        return dashboardMetadata;
    }

    public IBoard getBoard() {
        return board;
    }

    public DashboardMetadata getDashboardMetadata() {
        return dashboardMetadata;
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
    protected void procesDashboardEvent(Reason reason) {
        boolean save = true;

        switch (reason) {
        case addGadget:
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

        if (save) {
            if (showSaveButton) {
                btnSave.setEnabled(true); // user should manually save the state... 
            } else {
                presenter.save(); // automatic state saving...
            }
        }
    }

    protected boolean isGadgetRepositioned() {
        IGadgetIterator it = board.getGadgetIterator();
        for (GadgetMetadata gmd : this.dashboardMetadata.gadgets()) { // iterate on meta-data... 
            if (it.hasNext()) {
                IGadget gadget = it.next(); // get corresponding gadget from dashboard...
                if (gadget.getName().compareTo(gmd.name().getValue()) != 0 || it.getColumn() != gmd.column().getValue()) {
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