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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.gwt.commons.Print;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.EmptyBoard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.dashboard.printing.DashboardPrintHelper;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
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

    private Image printButton;

    private HorizontalPanel actionsWidget;

    private boolean isReadOnly;

    public AbstractDashboard(ICommonGadgetSettingsContainer container, IGadgetFactory gadgetDirectory, List<ILayoutManager> layoutManagers) {
        this.isReadOnly = false;
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

    /**
     * Fills the dashboard with gadgets.
     * 
     * @param dashboardMetadata
     *            a valid dashboard metadata with well defined {@link DashboardMetadata#encodedLayout()} and gadget metadata inside
     *            {@link DashboardMetadata#gadgetMetadataList()}.
     *            Must not be <code>null</code>.
     */
    public void setDashboardMetatdata(DashboardMetadata dashboardMetadata) {
        assert dashboardMetadata != null : "DashboardMetadata cannot be null";

        this.dashboardMetadata = dashboardMetadata;
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

        // set the active layout manager
        for (ILayoutManager layoutManager : layoutButtons.keySet()) {
            if (layoutManager.canHandle(dashboardMetadata.encodedLayout().getValue())) {
                activeLayoutManger = layoutManager;
                break;
            }
        }
        updateLayoutButtons();
        arrangeGadgets(gadgets);
        startGadgets();
    }

    /**
     * Defines if the layout can be changed by user interaction.
     * 
     * @param isReadOnly
     *            when <code>true</code> doesn't allow to rearrange or to add new gadgets interactively.
     */
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        setLayoutModifyingControlsVisible(!isReadOnly);
        if (board != null) {
            board.setReadOnly(isReadOnly);
        }
    }

    public DashboardMetadata getDashboardMetadata() {
        return this.dashboardMetadata;
    }

    protected abstract void onDashboardMetadataChanged();

    protected final void onPrintRequested() {
        Print.preview(DashboardPrintHelper.makePrintLayout(DOM.clone(board.asWidget().getElement(), true)));
    }

    private void setLayoutManager(ILayoutManager layoutManager) {
        // the following action must be done with the old layout manager and old board
        List<IGadgetInstance> gadgets = extractGadgetsFromBoard();
        activeLayoutManger = layoutManager;
        updateLayoutButtons();
        arrangeGadgets(gadgets);
        propagateLayoutToMetadata();
    }

    private void propagateLayoutToMetadata() {
        dashboardMetadata.encodedLayout().setValue(activeLayoutManger.encodeLayout(board));
        // TODO not quiet sure this has to resize notification has to be here
        fireResizeRequests();
        onDashboardMetadataChanged();
    }

    private List<IGadgetInstance> extractGadgetsFromBoard() {
        dashboardMetadata.encodedLayout().setValue(activeLayoutManger.encodeLayout(board));
        List<IGadgetInstance> gadgets = new ArrayList<IGadgetInstance>();
        IGadgetIterator i = board.getGadgetIterator();
        while (i.hasNext()) {
            IGadget g = i.next();
            if (g instanceof IGadgetInstance) {
                gadgets.add((IGadgetInstance) g);
            }
            i.remove();
        }
        return gadgets;
    }

    private void arrangeGadgets(List<IGadgetInstance> gadgets) {
        board = activeLayoutManger.arrange(dashboardMetadata.encodedLayout().getValue(), gadgets);
        board.setReadOnly(isReadOnly);
        board.addEventHandler(new BoardEvent() {
            @Override
            public void onEvent(Reason reason) {
                if (reason == Reason.removeGadget | reason == Reason.repositionGadget) {
                    propagateLayoutToMetadata();
                }
            }
        });
        if (gadgets.size() > 0) {
            scrollPanel.setWidget(board);
        } else {
            scrollPanel.setWidget(new EmptyBoard(i18n.tr("No Gadgets to show. To add gadgets use '+' action button.")));
        }
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

    private Widget createActionsWidget(List<ILayoutManager> layoutManagers) {
        actionsWidget = new HorizontalPanel();

        layoutButtons = new HashMap<ILayoutManager, Image>();
        for (final ILayoutManager layoutManager : layoutManagers) {
            final Image layoutButton = new Image();
            layoutButton.getElement().getStyle().setCursor(Cursor.POINTER);
            layoutButton.setResource(layoutManager.getResources().layoutIcon());
            layoutButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    setLayoutManager(layoutManager);
                }

            });

            layoutButtons.put(layoutManager, layoutButton);
            actionsWidget.add(layoutButton);
        }

        final Image addGadget = new Image(CrmImages.INSTANCE.dashboardAddGadget());
        addGadget.setTitle(i18n.tr("Add Gadget..."));
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
        addGadget.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new AddGadgetDialog(AbstractDashboard.this.getDashboardMetadata().type().getValue()) {
                    @Override
                    protected void onAddGadget(GadgetMetadata gadgetMetadata) {
                        // if this is the 1st gadget then EmptyBoard was shown - replace it with a real thing
                        if (!board.getGadgetIterator().hasNext()) {
                            scrollPanel.setWidget(board);
                        }
                        IGadgetInstance gadget = gadgetFactory.createGadget(gadgetMetadata);
                        commonGadgetSettingsContainer.bindGadget(gadget);
                        board.addGadget(gadget);
                        gadget.start();

                        propagateLayoutToMetadata();
                    };
                }.show();
            }
        });

        printButton = new Image(CrmImages.INSTANCE.dashboardPrint());
        printButton.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                printButton.setResource(CrmImages.INSTANCE.dashboardPrintHover());
            }
        });
        printButton.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                printButton.setResource(CrmImages.INSTANCE.dashboardPrint());
            }
        });
        printButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onPrintRequested();
            }
        });
        printButton.getElement().getStyle().setCursor(Cursor.POINTER);

        actionsWidget.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
        actionsWidget.add(addGadget);

        actionsWidget.add(printButton);
        actionsWidget.setSpacing(4);

        return actionsWidget;
    }

    private void setLayoutModifyingControlsVisible(boolean isVisible) {
        for (Widget w : actionsWidget) {
            if (!w.equals(printButton)) {
                w.setVisible(isVisible);
            }
        }
    }

    private void updateLayoutButtons() {
        for (Map.Entry<ILayoutManager, Image> entry : layoutButtons.entrySet()) {
            ImageResource imageResource = entry.getKey().equals(activeLayoutManger) ? entry.getKey().getResources().layoutIconSelected() : entry.getKey()
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
