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

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

/** A template class for a dashboard */
public abstract class AbstractDashboard extends ResizeComposite {

    public static String DEFAULT_STYLE_PREFIX = "vista_DashboardView";

    public enum StyleSuffix implements IStyleName {
        filtersDescription, filtersPanel, actionsPanel;
    }

    private static final I18n i18n = I18n.get(AbstractDashboard.class);

    private final ScrollPanel scrollPanel;

    private final DockLayoutPanel dashboardPanel;

    private final HorizontalPanel actionsPanel;

    private final IGadgetFactory gadgetFactory;

    private final ICommonGadgetSettingsContainer commonGadgetSettingsContainer;

    private IBoard board;

    private DashboardMetadata dashboardMetadata;

    private HashMap<ILayoutManager, Image> layoutButtons;

    protected ILayoutManager activeLayoutManger;

    public AbstractDashboard(ICommonGadgetSettingsContainer container, IGadgetFactory gadgetDirectory, List<ILayoutManager> layoutManagers) {
        this.commonGadgetSettingsContainer = container;
        this.gadgetFactory = gadgetDirectory;

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
        board = new Dashboard() {
            // TODO this is a vicious hack: in this implementation we handle layouts via layout managers, and actually every change of the dashboard
            // is a change of the layout, so we wish to update the dashboard metadata with new layout on every change             
            @Override
            public void onEvent(Reason reason) {
                if (reason != BoardEvent.Reason.newLayout) {
                    super.onEvent(reason);
                }
            }
        };
        if (dashboardMetadata != null) {
            List<IGadgetInstance> gadgets = new ArrayList<IGadgetInstance>();

            // instantiate gadgets
            for (GadgetMetadata metadata : dashboardMetadata.gadgetMetadataList()) {
                IGadgetInstance gadget = gadgetFactory.createGadget(metadata);
                if (gadget != null) {
                    gadgets.add(gadget);
                    commonGadgetSettingsContainer.bindGadget(gadget);
                } else {
                    throw new Error("gadget factory doesn't know how to instantiate gadget type '" + metadata.getInstanceValueClass().getName() + "'");
                }
            }

            // place them in the correct places inside the board
            for (ILayoutManager layoutManager : layoutButtons.keySet()) {
                if (layoutManager.canHandle(dashboardMetadata.encodedLayout().getValue())) {
                    activeLayoutManger = layoutManager;
                    activeLayoutManger.restoreLayout(dashboardMetadata.encodedLayout().getValue(), gadgets.iterator(), board);
                    redrawLayoutButtons(activeLayoutManger);
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
//        case updateGadget:
//             gadget settings were changed: IMHO not supposed to affect the dashboard metadata and be managed internally by the gadget
//            break;
        }
        redrawLayoutButtons(activeLayoutManger); // emphasize the button that switches to the chosen layout manager
        String updatedEncodedLayout = activeLayoutManger.switchLayout(dashboardMetadata.encodedLayout().getValue(), board);
        dashboardMetadata.encodedLayout().setValue(updatedEncodedLayout);

        fireResizeRequests();
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
                    activeLayoutManger = layoutManager;
                    proccessDashboardEvent(BoardEvent.Reason.newLayout);
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
                new AddGadgetDialog(AbstractDashboard.this.getDashboardMetadata().type().getValue()) {
                    @Override
                    protected void addGadget(GadgetMetadata proto) {
                        GWT.<DashboardMetadataService> create(DashboardMetadataService.class).createGadgetMetadata(new DefaultAsyncCallback<GadgetMetadata>() {
                            @Override
                            public void onSuccess(GadgetMetadata gadgteMetadata) {
                                IGadgetInstance gadget = gadgetFactory.createGadget(gadgteMetadata);
                                commonGadgetSettingsContainer.bindGadget(gadget);
                                board.addGadget(gadget);
                                gadget.start();
                            }
                        }, proto);
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

    private void fireResizeRequests() {
        IGadgetIterator i = board.getGadgetIterator();
        while (i.hasNext()) {
            i.next().onResize();
        }
    }

}
