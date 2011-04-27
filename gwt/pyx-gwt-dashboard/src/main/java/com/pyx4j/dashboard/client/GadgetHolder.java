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
 * Created on 2011-04-17
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.DashboardPanel.StyleSuffix;
import com.pyx4j.dashboard.client.IGadget.ISetup;

final class GadgetHolder extends SimplePanel {

    private final IGadget holdedGadget;

    private final DashboardPanel dashboardPanel;

    private final VerticalPanel frame = new VerticalPanel();

    private final Label title = new Label();

    private final Image maximizer;

    protected boolean inSetup = false;

    // public interface:
    public IGadget getIWidget() {

        return holdedGadget;
    }

    // internals:
    public GadgetHolder(IGadget widget, DashboardPanel dashboardPanel) {
        this.holdedGadget = widget;
        this.dashboardPanel = dashboardPanel;
        this.addStyleName(DashboardPanel.BASE_NAME + StyleSuffix.Holder);

        // create caption with title and menu:
        final HorizontalPanel caption = new HorizontalPanel();

        title.setText(holdedGadget.getName());
        title.addStyleName(DashboardPanel.BASE_NAME + StyleSuffix.HolderHeading);
        caption.addStyleName(DashboardPanel.BASE_NAME + StyleSuffix.HolderCaption);
        caption.add(title);
        caption.setCellWidth(caption.getWidget(caption.getWidgetCount() - 1), "98%");

        caption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        maximizer = new Image(this.dashboardPanel.images.WindowMaximize());
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

        this.getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
        this.getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        this.getElement().getStyle().setProperty("boxSizing", "border-box");
        this.setWidth("auto");

        // don't forget about vertical spacing:
        setVerticalSpacing(this.dashboardPanel.layout.getVerticalSpacing());

        // make the widget place holder draggable by its title:
        this.dashboardPanel.widgetDragController.makeDraggable(this, title);
        this.dashboardPanel.widgetDragController.addDragHandler(new DragHandler() {

            @Override
            public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
            }

            @Override
            public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
            }

            @Override
            public void onDragStart(DragStartEvent event) {
                ((GadgetHolder) event.getSource()).setWidth(((GadgetHolder) event.getSource()).getOffsetWidth() + "px");
            }

            @Override
            public void onDragEnd(DragEndEvent event) {
                ((GadgetHolder) event.getSource()).setWidth("auto");
            }
        });
    }

    public boolean isFullWidth() {
        return holdedGadget.isFullWidth();
    }

    private Widget createWidgetMenu() {
        final Image btn = new Image(this.dashboardPanel.images.WindowMenu());
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
                menu.addStyleName(DashboardPanel.BASE_NAME + StyleSuffix.HolderMenu);

                if (holdedGadget.isMinimizable()) {
                    menu.addItem((isMinimized() ? "Expand" : "Minimize"), cmdMinimize);
                }

                menu.addItem("Delete", cmdDelete);

                if (holdedGadget.isSetupable() && !inSetup) {
                    menu.addSeparator();
                    menu.addItem("Setup", cmdSetup);
                }

                pp.setWidget(menu);
                pp.setPopupPositionAndShow(new PositionCallback() {
                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        pp.setPopupPosition(btn.getAbsoluteLeft() + btn.getOffsetWidth() - offsetWidth, btn.getAbsoluteTop() + btn.getOffsetHeight());
                    }
                });

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
        if (holdedGadget.isFullWidth()) {
            this.getElement().getStyle().setMargin(spacing, Unit.PX);
            this.getElement().getStyle().setMarginLeft(0, Unit.PX);
            this.getElement().getStyle().setMarginRight(0, Unit.PX);
        }
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
            maximizer.setResource(this.dashboardPanel.images.WindowMaximize());
            maximizer.setTitle("Maximize");

            maximizeData.restoreWidgetPosition(this);
            dashboardPanel.setWidget(maximizeData.boundaryPanel);
            maximizeData.clear();

            this.dashboardPanel.widgetDragController.makeDraggable(this, title);
            holdedGadget.onMaximize(false);
            this.dashboardPanel.setRefreshAllowed(true);
        } else { // maximize:
            maximizer.setResource(this.dashboardPanel.images.WindowRestore());
            maximizer.setTitle("Restore");

            maximizeData.saveWidgetPosition(this);
            maximizeData.boundaryPanel = dashboardPanel.getWidget();
            dashboardPanel.setWidget(this);

            this.dashboardPanel.widgetDragController.makeNotDraggable(this);
            holdedGadget.onMaximize(true);
            this.dashboardPanel.setRefreshAllowed(false);
        }
    }

    private boolean isMaximized() {
        return (maximizeData.boundaryPanel != null);
    }

    private class MaximizeData {
        private InsertPanel hostPanel;

        private int widgetIndex;

        public Widget boundaryPanel;

        public void saveWidgetPosition(GadgetHolder widget) {
            Widget parent = getParent();
            while (!(parent instanceof InsertPanel)) {
                parent = parent.getParent();
            }

            hostPanel = (InsertPanel) parent;
            widgetIndex = hostPanel.getWidgetIndex(widget);
            widget.setVerticalSpacing(0);
        }

        public void restoreWidgetPosition(GadgetHolder widget) {
            widget.setVerticalSpacing(GadgetHolder.this.dashboardPanel.layout.getVerticalSpacing());
            hostPanel.insert(widget, widgetIndex);
        }

        public void clear() {
            boundaryPanel = null;
            hostPanel = null;
        }
    }

    private final MaximizeData maximizeData = new MaximizeData();

    // --------------------------------------------------------------

    private void delete() {
        holdedGadget.onDelete();
        this.removeFromParent();
    }

    // --------------------------------------------------------------

    private void setup() {
        final ISetup setupGadget = holdedGadget.getSetup();

        // create main gadget setup panel: 
        final FlowPanel setup = new FlowPanel();
        setup.addStyleName(DashboardPanel.BASE_NAME + StyleSuffix.HolderSetup);
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
        inSetup = true;
    }

    private void switchViewTo(Widget view) {
        frame.remove(frame.getWidgetCount() - 1);
        frame.add(view);
    }

    private void switchViewToNormal() {
        switchViewTo(holdedGadget.getWidget());
        inSetup = false;
    }
}