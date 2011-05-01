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
package com.pyx4j.widgets.client.dashboard;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dashboard.CSSNames.StyleSuffix;
import com.pyx4j.widgets.client.dashboard.IGadget.ISetup;
import com.pyx4j.widgets.client.dashboard.images.DashboardImages;

final class GadgetHolder extends SimplePanel {

    // resources:
    protected static DashboardImages images = (DashboardImages) GWT.create(DashboardImages.class);

    private final IGadget holdedGadget;

    private final PickupDragController gadgetDragController;

    private final SimplePanel root;

    private final VerticalPanel frame = new VerticalPanel();

    private final Label title = new Label();

    private final Image maximizer;

    protected boolean inSetup = false;

    // public interface:
    public IGadget getGadget() {
        return holdedGadget;
    }

    // internals:
    public GadgetHolder(IGadget gadget, PickupDragController gadgetDragController, SimplePanel root) {
        this.holdedGadget = gadget;
        this.gadgetDragController = gadgetDragController;
        this.root = root;
        this.addStyleName(CSSNames.BASE_NAME + StyleSuffix.Holder);

        // create caption with title and menu:
        final HorizontalPanel caption = new HorizontalPanel();

        title.setText(holdedGadget.getName());
        title.addStyleName(CSSNames.BASE_NAME + StyleSuffix.HolderHeading);
        caption.addStyleName(CSSNames.BASE_NAME + StyleSuffix.HolderCaption);
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
        frame.add(holdedGadget.asWidget());
        frame.setWidth("100%");
        frame.getElement().getStyle().setOverflow(Overflow.HIDDEN);
        this.setWidget(frame);

        this.getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
        this.getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        this.getElement().getStyle().setProperty("boxSizing", "border-box");
//        this.setWidth("auto");

        // make the widget place holder draggable by its title:
        this.gadgetDragController.makeDraggable(this, title);
        this.gadgetDragController.addDragHandler(new DragHandler() {

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
                menu.addStyleName(CSSNames.BASE_NAME + StyleSuffix.HolderMenu);

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
            root.setWidget(maximizeData.layoutPanel);
            maximizeData.clear();

            removeStyleDependentName(CSSNames.StyleDependent.maximized.name());
            gadgetDragController.makeDraggable(this, title);
            holdedGadget.onMaximize(false);
            // this.dashboardPanel.setRefreshAllowed(true);
        } else { // maximize:
            maximizer.setResource(images.WindowRestore());
            maximizer.setTitle("Restore");

            maximizeData.saveWidgetPosition(this);
            maximizeData.layoutPanel = root.getWidget();
            root.setWidget(this);

            addStyleDependentName(CSSNames.StyleDependent.maximized.name());
            gadgetDragController.makeNotDraggable(this);
            holdedGadget.onMaximize(true);
            // this.dashboardPanel.setRefreshAllowed(false);
        }
    }

    private boolean isMaximized() {
        return (maximizeData.layoutPanel != null);
    }

    private class MaximizeData {

        private SimplePanel hostPanel;

//        private int widgetIndex;

        public Widget layoutPanel;

        public void saveWidgetPosition(GadgetHolder widget) {
            Widget parent = getParent();
            while (!(parent instanceof SimplePanel)) {
                parent = parent.getParent();
            }

            hostPanel = (SimplePanel) parent;
//            widgetIndex = hostPanel.getWidgetIndex(widget);
        }

        public void restoreWidgetPosition(GadgetHolder widget) {
            // widget.setVerticalSpacing(GadgetHolder.this.dashboardPanel.layout.getVerticalSpacing());
//            hostPanel.insert(widget, widgetIndex);
            hostPanel.setWidget(widget);
        }

        public void clear() {
            layoutPanel = null;
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
        setup.addStyleName(CSSNames.BASE_NAME + StyleSuffix.HolderSetup);
        setup.add(setupGadget.asWidget());

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
        setupGadget.onStart();
        inSetup = true;
    }

    private void switchViewTo(Widget view) {
        frame.remove(frame.getWidgetCount() - 1);
        frame.add(view);
    }

    private void switchViewToNormal() {
        switchViewTo(holdedGadget.asWidget());
        inSetup = false;
    }
}