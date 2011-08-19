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

import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.BoardEvent.Reason;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.decorations.CrmActionsBarDecorator;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.gadgets.GadgetsFactory;
import com.propertyvista.crm.client.ui.gadgets.IGadgetBase;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public abstract class BoardViewImpl extends DockLayoutPanel implements BoardView {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    public static enum StyleSuffix implements IStyleSuffix {
        actionsPanel
    }

    protected static I18n i18n = I18nFactory.getI18n(BoardViewImpl.class);

    protected final VistaHeaderBar header;

    protected final HorizontalPanel actionsPanel;

    protected final Button btnSave = new Button(i18n.tr("Save"));

    protected final boolean showSaveButton = false; // true; // Save button used in test purpose only!..

    protected final ScrollPanel scroll = new ScrollPanel();

    protected IBoard board;

    protected Presenter presenter;

    protected DashboardMetadata dashboardMetadata;

    protected boolean filling;

    public BoardViewImpl(String caption) {
        this(caption, false);
    }

    public BoardViewImpl(String caption, boolean internal) {
        super(Unit.EM);

        if (internal) {
            addNorth(header = new CrmActionsBarDecorator(caption, createHeaderWidget()), VistaCrmTheme.defaultActionBarHeight);
        } else {
            addNorth(header = new CrmHeaderDecorator(caption, createHeaderWidget()), VistaCrmTheme.defaultHeaderHeight);
        }

        actionsPanel = new HorizontalPanel();
        actionsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
        actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        actionsPanel.setWidth("100%");
        actionsPanel.add(new HTML()); // just for %-tage cells alignment...

        if (showSaveButton) {
            addActionButton(btnSave);
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

        header.setHeight("100%"); // fill all that defaultHeaderHeight!..
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
            header.setCaption(dashboardMetadata.name().getStringView());
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

    protected void addActionButton(Button action) {
        actionsPanel.setVisible(true);
        actionsPanel.insert(action, 1);
        actionsPanel.setCellWidth(action, "1%");
        action.getElement().getStyle().setMarginRight(1, Unit.EM);
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
            save = (BoardViewImpl.this.dashboardMetadata.layoutType().getValue() != translateLayout(board.getLayout()));
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

    protected abstract Widget createHeaderWidget();

    protected abstract LayoutType translateLayout(BoardLayout layout);

    protected abstract void setLayout(LayoutType layout);
}
