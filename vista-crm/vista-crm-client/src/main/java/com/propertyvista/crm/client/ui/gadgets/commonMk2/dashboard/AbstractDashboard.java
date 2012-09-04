/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.board.BoardBase.StyleSuffix;
import com.propertyvista.crm.client.ui.gadgets.addgadgetdialog.GadgetDirectoryDialog;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public abstract class AbstractDashboard extends Composite {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    private static final I18n i18n = I18n.get(AbstractDashboard.class);

    private final ScrollPanel scrollPanel;

    private final DockLayoutPanel dashboardPanel;

    private final HorizontalPanel actionsPanel;

    private final IGadgetDirectory gadgetDirectory;

    private final ICommonGadgetSettingsContainer commonGadgetSettingsContainer;

    private IBoard board;

    private DashboardMetadata dashboardMetadata;

    private HashMap<ILayoutManager, Image> layoutButtons;

    public AbstractDashboard(ICommonGadgetSettingsContainer container, IGadgetDirectory gadgetDirectory, List<ILayoutManager> layoutManagers) {
        this.commonGadgetSettingsContainer = container;
        this.gadgetDirectory = gadgetDirectory;

        this.dashboardPanel = new DockLayoutPanel(Unit.EM);
        this.dashboardPanel.setSize("100%", "100%");

        this.actionsPanel = new HorizontalPanel();
        this.actionsPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
        this.actionsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        this.actionsPanel.setWidth("100%");
        this.actionsPanel.add(new HTML()); // just for %-tage cells alignment...        
        this.actionsPanel.add(createActionsWidget(layoutManagers));

        this.dashboardPanel.addNorth(actionsPanel, 2);

        this.scrollPanel = new ScrollPanel();
        this.scrollPanel.setSize("100%", "100%");
        this.dashboardPanel.add(scrollPanel);

        initWidget(this.dashboardPanel);
    }

    public void setDashboardMetatdata(DashboardMetadata dashboardMetadata) {
        this.dashboardMetadata = dashboardMetadata;

        placeGadgets();
        startGadgets();
    }

    public DashboardMetadata getDashboardMetadata() {
        return this.dashboardMetadata;
    }

    protected abstract void onDashboardMetadataChanged();

    protected abstract void onPrintRequested();

    private void placeGadgets() {
        board = new Dashboard();
        if (dashboardMetadata != null) {
            List<IGadgetInstance> gadgets = new ArrayList<IGadgetInstance>();
            for (GadgetMetadata metadata : dashboardMetadata.gadgets()) {
                IGadgetInstance gadget = gadgetDirectory.createGadgetInstance(metadata);
                // TODO stupid way this stupid list is needed to separate layout from dashboard, but the implementation of segregation is not well done, review
                if (gadget != null) {
                    gadgets.add(gadget);
                    commonGadgetSettingsContainer.bindGadget(gadget);
                } else {
                    throw new Error("gadget factory doesn't know how to instantiate gadget type '" + metadata.getInstanceValueClass().getName() + "'");
                }
            }

            for (ILayoutManager layoutManager : layoutButtons.keySet()) {
                if (layoutManager.canHandle(dashboardMetadata)) {
                    redrawLayoutButtons(layoutManager);
                    layoutManager.restoreLayout(dashboardMetadata, gadgets.iterator(), board);
                    break;
                }
            }

        } else {
            throw new Error("DashboardMetadata cannot be null");
        }

        board.addEventHandler(new BoardEvent() {
            @Override
            public void onEvent(final Reason reason) {
                // use a deferred command so that the actual event processing unlinked from event!
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        AbstractDashboard.this.proccessDashboardEvent(reason);
                    }
                });
            }
        });

        scrollPanel.setWidget(board);
    }

    private void startGadgets() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                IGadgetIterator it = board.getGadgetIterator();
                while (it.hasNext()) {
                    it.next().start();
                }
            }
        });
    }

    private void proccessDashboardEvent(BoardEvent.Reason reason) {
        switch (reason) {
        case addGadget:
            break;
        case newLayout:
            break;
        case removeGadget:
            break;
        case repositionGadget:
            break;
        case updateGadget:
            // gadget settings were changed: IMHO not supposed to affect the dashboard metadata and be managed internally by the gadget
            break;
        }

        onDashboardMetadataChanged();
    }

    private Widget createActionsWidget(List<ILayoutManager> layoutManagers) {
        HorizontalPanel actionsWidget = new HorizontalPanel();

        layoutButtons = new HashMap<ILayoutManager, Image>();
        for (final ILayoutManager layoutManager : layoutManagers) {
            final Image layoutButton = new Image();
            layoutButton.getElement().getStyle().setCursor(Cursor.POINTER);
            layoutButton.setResource(layoutManager.getResources().layoutIcon());
            layoutButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    redrawLayoutButtons(layoutManager);
                    layoutManager.saveLayout(dashboardMetadata, board);
                }

            });

            layoutButtons.put(layoutManager, layoutButton);
            actionsWidget.add(layoutButton);
        }

        final Image addGadget = new Image(CrmImages.INSTANCE.dashboardAddGadget());
        addGadget.getElement().getStyle().setCursor(Cursor.POINTER);
        addGadget.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                addGadget.setResource(CrmImages.INSTANCE.dashboardAddGadgetHover());
            }
        });
        addGadget.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                addGadget.setResource(CrmImages.INSTANCE.dashboardAddGadget());
            }
        });
        addGadget.setTitle(i18n.tr("Add Gadget..."));
        addGadget.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new GadgetDirectoryDialog(gadgetDirectory) {
                    @Override
                    protected void addGadget(IGadgetInstance gadget) {
                        commonGadgetSettingsContainer.bindGadget(gadget);
                        board.addGadget(gadget);
                    }
                }.show();
            }
        });

        final Image print = new Image(CrmImages.INSTANCE.dashboardPrint());
        print.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                print.setResource(CrmImages.INSTANCE.dashboardPrintHover());
            }
        });
        print.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                print.setResource(CrmImages.INSTANCE.dashboardPrint());
            }
        });
        print.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onPrintRequested();
            }
        });
        print.getElement().getStyle().setCursor(Cursor.POINTER);

        actionsWidget.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
        actionsWidget.add(addGadget);

        actionsWidget.add(print);
        actionsWidget.setSpacing(4);

        return actionsWidget;
    }

    private void redrawLayoutButtons(ILayoutManager layoutManager) {
        for (Map.Entry<ILayoutManager, Image> entry : layoutButtons.entrySet()) {
            ImageResource imageResource = entry.getKey().equals(layoutManager) ? entry.getKey().getResources().layoutIconSelected() : entry.getKey()
                    .getResources().layoutIcon();
            entry.getValue().setResource(imageResource);
        }
    }
}
