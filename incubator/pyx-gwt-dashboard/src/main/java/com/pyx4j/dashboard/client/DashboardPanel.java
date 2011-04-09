/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-02-14
 * @author VladLL
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.IGadget.ISetup;
import com.pyx4j.dashboard.client.images.DashboardImages;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

/**
 * Dashboard panel.
 */
public class DashboardPanel extends SimplePanel {

    // CSS style names: 
    public static String BASE_NAME = "pyx4j_DashboardPanel";

    public static enum StyleSuffix implements IStyleSuffix {
        Column, ColumnHeading, ColumnSpacer, Holder, HolderSetup, HolderCaption, HolderHeading, HolderMenu, DndPositioner
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, selected, hover
    }

    // resources:
    protected DashboardImages images = (DashboardImages) GWT.create(DashboardImages.class);

    // internal data:	
    protected Layout layout;

    protected PickupDragController widgetDragController;

    protected FlowPanel columnsContainerPanel; // holds columns (as vertical panels).

    private boolean isRefreshAllowed;

    // construction:
    public DashboardPanel() {
        this.layout = new Layout();
        init();
    }

    public DashboardPanel(Layout layout) {
        this.layout = layout;
        init();
    }

    // Layout manipulation:
    public Layout getLayout() {
        return this.layout;
    }

    public boolean setLayout(Layout layout) {
        this.layout = layout; // accept new layout
        return refresh();
    }

    public boolean refresh() {
        if (!isRefreshAllowed) {
            return false;
        }

        int gadgetsCount = 0;
        // hold the current widgets for a while:
        Vector<VerticalPanelWithSpacer> columnWidgetsPanels = new Vector<VerticalPanelWithSpacer>(columnsContainerPanel.getWidgetCount());
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
            VerticalPanelWithSpacer cwp = getColumnWidgetsPanel(i);
            gadgetsCount += cwp.getWidgetCount();
            columnWidgetsPanels.add(cwp);
        }

        initColumns(); // initialize new columns according to the (new) layout

        // if new columns count the same as previous one - just move gadgets one to one:
        if (columnsContainerPanel.getWidgetCount() == columnWidgetsPanels.size()) {
            for (int i = 0; i < columnWidgetsPanels.size(); ++i) {
                while (columnWidgetsPanels.get(i).getWidgetCount() > 0) {
                    getColumnWidgetsPanel(i).add(columnWidgetsPanels.get(i).getWidget(0));
                }
            }
        } else { // 'equalize' gadgets per columns:
            Vector<Widget> allGadgets = new Vector<Widget>(gadgetsCount);
            for (int i = 0; i < columnWidgetsPanels.size(); ++i) {
                for (int j = 0; j < columnWidgetsPanels.get(i).getWidgetCount(); ++j) {
                    allGadgets.add(columnWidgetsPanels.get(i).getWidget(j));
                }
            }

            int gadgetsPerColumn = gadgetsCount / columnsContainerPanel.getWidgetCount();
            for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
                int size = (i == columnsContainerPanel.getWidgetCount() - 1 ? allGadgets.size() : Math.min(gadgetsPerColumn, allGadgets.size()));
                for (int j = 0; j < size; ++j) {
                    getColumnWidgetsPanel(i).add(allGadgets.firstElement());
                    allGadgets.remove(0);
                }
            }
        }

        return true;
    }

    // Widget manipulation:	
    public boolean addGadget(IGadget widget) {
        return insertGadget(widget, 0, 0);
    }

    public boolean addGadget(IGadget widget, int column) {
        return insertGadget(widget, column, -1);
    }

    public boolean insertGadget(IGadget widget, int column, int row) {
        if (checkIndexes(column, row, true)) {
            // create holder for supplied widget and insert it into specified column,row:
            GadgetHolder gh = new GadgetHolder(widget, this);

            if (row > 0) {
                getColumnWidgetsPanel(column).insert(gh, row);
            } else {
                // if row is negative - just add at the end:
                getColumnWidgetsPanel(column).add(gh);
            }

            return true;
        }

        return false;
    }

    public boolean removeGadget(int column, int row) {
        return (checkIndexes(column, row, false) && getColumnWidgetsPanel(column).remove(row));
    }

    public void removeAllGadgets() {
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i) {
            getColumnWidgetsPanel(i).clear();
        }
    }

    // initializing:
    protected void init() {
        addStyleName(BASE_NAME);
        isRefreshAllowed = true;

        // use the boundary panel as this composite's widget:
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        // initialize horizontal panel to hold our columns:
        columnsContainerPanel = new FlowPanel();
        columnsContainerPanel.setWidth("100%");

        boundaryPanel.add(columnsContainerPanel);

        // initialize our widget drag controller:
        widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);

        initColumns();
    }

    protected void initColumns() {
        columnsContainerPanel.clear();
        widgetDragController.unregisterDropControllers();

        for (int col = 0; col < layout.getColumns(); ++col) {
            // vertical panel to hold the heading and a second vertical panel for widgets:
            FlowPanel columnCompositePanel = new FlowPanel();
            columnCompositePanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            columnCompositePanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            columnCompositePanel.getElement().getStyle().setMarginLeft(layout.getHorizontalSpacing(), Unit.PCT);
            columnCompositePanel
                    .setWidth(((layout.isColumnWidths() ? layout.getCoumnWidth(col) : 100.0 / layout.getColumns()) - layout.getHorizontalSpacing() - layout
                            .getHorizontalSpacing() / layout.getColumns())
                            + "%");

            // put column name if necessary:
            if (layout.isColumnNames()) {
                Label heading = new Label(layout.getCoumnName(col));
                heading.addStyleName(BASE_NAME + StyleSuffix.ColumnHeading);
                heading.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                heading.setWidth("100%");

                columnCompositePanel.add(heading);
            }

            // inner vertical panel to hold individual widgets:
            VerticalPanelWithSpacer columnPanel = new VerticalPanelWithSpacer();
            columnPanel.addStyleName(BASE_NAME + StyleSuffix.Column);
            columnPanel.setWidth("100%");

            // widget drop controller for the current column:
            CustomFlowPanelDropController widgetDropController = new CustomFlowPanelDropController(columnPanel, layout.getVerticalSpacing());
            widgetDragController.registerDropController(widgetDropController);

            columnCompositePanel.add(columnPanel);
            columnsContainerPanel.add(columnCompositePanel);
        }
    }

    // internals:	
    protected VerticalPanelWithSpacer getColumnWidgetsPanel(int column) {
        ComplexPanel columnCompositePanel = (ComplexPanel) columnsContainerPanel.getWidget(column);
        return (VerticalPanelWithSpacer) columnCompositePanel.getWidget(columnCompositePanel.getWidgetCount() - 1);
        // first element is label with column name, so always get last one!..
    }

    protected boolean checkIndexes(int column, int row, boolean insert) {
        if (column >= columnsContainerPanel.getWidgetCount()) {
            return false;
        }

        if (insert) {
            if (row > getColumnWidgetsPanel(column).getWidgetCount()) {
                return false;
            }
        } else if (row >= getColumnWidgetsPanel(column).getWidgetCount()) {
            return false;
        }

        return true;
    }

    protected final class GadgetHolder extends SimplePanel {
        private final IGadget holdedGadget;

        private final DashboardPanel dashboardPanel;

        private final VerticalPanel frame = new VerticalPanel();

        private final Label title = new Label();

        private final Image maximizer;

        // public interface:
        public IGadget getIWidget() {
            return holdedGadget;
        }

        // internals:
        public GadgetHolder(IGadget widget, DashboardPanel mainPanel) {
            this.holdedGadget = widget;
            this.dashboardPanel = mainPanel;
            this.addStyleName(BASE_NAME + StyleSuffix.Holder);

            // create caption with title and menu:
            final HorizontalPanel caption = new HorizontalPanel();

            title.setText(holdedGadget.getName());
            title.addStyleName(BASE_NAME + StyleSuffix.HolderHeading);
            caption.addStyleName(BASE_NAME + StyleSuffix.HolderCaption);
            caption.add(title);
            caption.setCellWidth(caption.getWidget(caption.getWidgetCount() - 1), "98%");

            caption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

            maximizer = new Image(images.WindowMaximize());
            maximizer.setTitle("Maximize");
            maximizer.getElement().getStyle().setCursor(Cursor.POINTER);
            maximizer.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    maximize();
                }
            });
            caption.add(maximizer);
            caption.setCellWidth(caption.getWidget(caption.getWidgetCount() - 1), "1%");
            caption.setCellVerticalAlignment(caption.getWidget(caption.getWidgetCount() - 1), HasVerticalAlignment.ALIGN_MIDDLE);

            caption.add(createWidgetMenu());
            caption.setCellWidth(caption.getWidget(caption.getWidgetCount() - 1), "1%");
            caption.setCellVerticalAlignment(caption.getWidget(caption.getWidgetCount() - 1), HasVerticalAlignment.ALIGN_MIDDLE);

            caption.setWidth("100%");

            // put it together:
            frame.add(caption);
            frame.add(holdedGadget.getWidget());
            frame.setWidth("100%");
            frame.getElement().getStyle().setOverflow(Overflow.HIDDEN);

            this.setWidget(frame);
            this.setWidth("auto");

            // don't forget about vertical spacing:
            setVerticalSpacing(layout.getVerticalSpacing());

            // make the widget place holder draggable by its title:
            widgetDragController.makeDraggable(this, title);
            widgetDragController.addDragHandler(new DragHandler() {

                @Override
                public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
                }

                @Override
                public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
                }

                @Override
                public void onDragStart(DragStartEvent event) {
                    if (event.getContext().draggable.equals(GadgetHolder.this)) {
                        GadgetHolder.this.setWidth("100%"); // prevent draggable gadget from collapsing!.. 
                    }
                }

                @Override
                public void onDragEnd(DragEndEvent event) {
                    if (event.getContext().draggable.equals(GadgetHolder.this)) {
                        GadgetHolder.this.setWidth("auto"); // restore automatic width calculation...
                    }
                }
            });
        }

        private Widget createWidgetMenu() {
            final Image btn = new Image(images.WindowMenu());
            btn.getElement().getStyle().setCursor(Cursor.POINTER);
            btn.addClickHandler(new ClickHandler() {
                private final PopupPanel pp = new PopupPanel(true);

                @Override
                public void onClick(ClickEvent event) {
                    // menu items command processors go here:
                    Command cmdMinimize = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            minimize();
                        }
                    };

                    Command cmdSetup = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            setup();
                        }
                    };

                    Command cmdDelete = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            delete();
                        }
                    };

                    // create the menu:
                    MenuBar menu = new MenuBar(true);
                    menu.addStyleName(BASE_NAME + StyleSuffix.HolderMenu);

                    if (holdedGadget.isMinimizable()) {
                        menu.addItem((isMinimized() ? "Expand" : "Minimize"), cmdMinimize);
                    }

                    menu.addItem("Delete", cmdDelete);

                    if (holdedGadget.isSetupable()) {
                        menu.addSeparator();
                        menu.addItem("Setup", cmdSetup);
                    }

                    pp.setWidget(menu);
                    pp.setPopupPosition(btn.getAbsoluteLeft(), btn.getAbsoluteTop() + btn.getOffsetHeight());
                    pp.show();
                } // onClick button event handler...
            }); // ClickHandler class...

            btn.setTitle("Options");
            return btn;
        }

        private void setVerticalSpacing(int spacing) {
            /**
             * Note: dnd tricks with margin and uses DOM.getStyleAttribute(w,"margin")
             * (com.allen_sauer.gwt.dnd.client.PickupDragController.
             * saveSelectedWidgetsLocationAndStyle())
             * to retrieve and save current widget margin, but... it doesn't read
             * attributes set by getStyle().setMarginTop/Bottom methods!!??
             * Thus using instead such combination:
             */
            // this.getElement().getStyle().setProperty("margin", layout.getVerticalSpacing() + "px" + " 0px");
            this.getElement().getStyle().setMargin(spacing, Unit.PX);
            this.getElement().getStyle().setMarginLeft(0, Unit.PX);
            this.getElement().getStyle().setMarginRight(0, Unit.PX);
        }

        // --------------------------------------------------------------

        private void minimize() {
            if (isMinimized()) {
                frame.add(minimizedWidget);
                minimizedWidget = null;
                holdedGadget.onMinimize(false);
            } else { // minimize:
                minimizedWidget = frame.getWidget(frame.getWidgetCount() - 1);
                frame.remove(minimizedWidget);
                holdedGadget.onMinimize(true);
            }
        }

        private boolean isMinimized() {
            return (minimizedWidget != null);
        }

        private Widget minimizedWidget;

        // --------------------------------------------------------------

        private void maximize() {
            if (isMaximized()) {
                maximizer.setResource(images.WindowMaximize());
                maximizer.setTitle("Maximize");

                maximizeData.restoreWidgetPosition(this);
                dashboardPanel.setWidget(maximizeData.boundaryPanel);
                maximizeData.clear();

                widgetDragController.makeDraggable(this, title);
                holdedGadget.onMaximize(false);
                isRefreshAllowed = true;
            } else { // maximize:
                maximizer.setResource(images.WindowRestore());
                maximizer.setTitle("Restore");

                maximizeData.saveWidgetPosition(this);
                maximizeData.boundaryPanel = dashboardPanel.getWidget();
                dashboardPanel.setWidget(this);

                widgetDragController.makeNotDraggable(this);
                holdedGadget.onMaximize(true);
                isRefreshAllowed = false;
            }
        }

        private boolean isMaximized() {
            return (maximizeData.boundaryPanel != null);
        }

        private class MaximizeData {
            private FlowPanel columnPanel;

            private int widgetIndex;

            public Widget boundaryPanel;

            public void saveWidgetPosition(GadgetHolder widget) {
                columnPanel = (FlowPanel) getParent();
                widgetIndex = maximizeData.columnPanel.getWidgetIndex(widget);
                widget.setVerticalSpacing(0);
            }

            public void restoreWidgetPosition(GadgetHolder widget) {
                widget.setVerticalSpacing(layout.getVerticalSpacing());
                columnPanel.insert(widget, widgetIndex);
            }

            public void clear() {
                boundaryPanel = null;
                columnPanel = null;
            }
        }

        private final MaximizeData maximizeData = new MaximizeData();

        // --------------------------------------------------------------

        private void delete() {
            holdedGadget.onDelete();
            ((FlowPanel) getParent()).remove(this);
        }

        // --------------------------------------------------------------

        private void setup() {
            final ISetup setupGadget = holdedGadget.getSetup();

            // create main gadget setup panel: 
            final FlowPanel setup = new FlowPanel();
            setup.addStyleName(BASE_NAME + StyleSuffix.HolderSetup);
            setup.add(setupGadget.getWidget());

            // create panel with Ok/Cancel buttons:
            HorizontalPanel buttons = new HorizontalPanel();
            buttons.add(new Button("OK", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setupGadget.onOk();
                    switchViewToNormal();
                }
            }));
            buttons.add(new Button("Cancel", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setupGadget.onCancel();
                    switchViewToNormal();
                }
            }));
            buttons.setSpacing(10);
            setup.add(buttons);

            // switch displayed widget with setup one:
            switchViewTo(setup);
        }

        private void switchViewTo(Widget view) {
            frame.remove(frame.getWidgetCount() - 1);
            frame.add(view);
        }

        private void switchViewToNormal() {
            switchViewTo(holdedGadget.getWidget());
        }
    } // WidgetHolder
} // DashboardPanel class...
