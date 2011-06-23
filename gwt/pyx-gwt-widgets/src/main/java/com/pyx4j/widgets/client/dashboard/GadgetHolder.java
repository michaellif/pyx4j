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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dashboard.CSSNames.StyleSuffix;
import com.pyx4j.widgets.client.dashboard.DashboardEvent.Reason;
import com.pyx4j.widgets.client.dashboard.IGadget.ISetup;
import com.pyx4j.widgets.client.dashboard.images.DashboardImages;

final class GadgetHolder extends SimplePanel {

    private static I18n i18n = I18nFactory.getI18n(GadgetHolder.class);

    private static DashboardImages images = (DashboardImages) GWT.create(DashboardImages.class);

    private static String STR_MAXIMIZE = i18n.tr("Maximize");

    private static String STR_RESTORE = i18n.tr("Restore");

    private final IGadget holdedGadget;

    private final IBoardRoot root;

    private final PickupDragController gadgetDragController;

    private final HorizontalPanel caption = new HorizontalPanel();

    private final ScrollPanel scroll = new ScrollPanel();

    private final Label title = new Label();

    private final Image maximizer;

    private final Widget gadgetMmenu;

    private boolean inSetup = false;

    private boolean inMenu = false;

    // public interface:
    public IGadget getGadget() {
        return holdedGadget;
    }

    // internals:
    public GadgetHolder(IGadget gadget, PickupDragController gadgetDragController, IBoardRoot root) {

        this.holdedGadget = gadget;
        this.gadgetDragController = gadgetDragController;
        this.root = root;
        this.addStyleName(CSSNames.BASE_NAME + StyleSuffix.Holder);

        FlowPanel content = new FlowPanel();

        setWidget(content);

        // create caption with title and menu:
        title.setText(holdedGadget.getName());
        title.addStyleName(CSSNames.BASE_NAME + StyleSuffix.HolderHeading);
        caption.add(title);

        caption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        maximizer = new Image(images.WindowMaximize());
        maximizer.setTitle(STR_MAXIMIZE);
        maximizer.getElement().getStyle().setCursor(Cursor.POINTER);
        maximizer.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                maximize();
            }
        });
        caption.add(maximizer);
        caption.setCellWidth(maximizer, "1%");

        caption.add(gadgetMmenu = createGadgetMenu());
        caption.setCellWidth(gadgetMmenu, "1%");

        caption.addStyleName(CSSNames.BASE_NAME + StyleSuffix.HolderCaption);
        caption.getElement().getStyle().setProperty("minHeight", "20px");
        caption.setWidth("100%");

        // put it together:
        content.add(caption);

        scroll.setWidget(holdedGadget.asWidget());
        content.add(scroll);

        this.getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
        this.getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        this.getElement().getStyle().setProperty("boxSizing", "border-box");

        // handle mouse over UI tweaking:
        setIconsVisible(false);
        this.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                setIconsVisible(true);
            }
        }, MouseOverEvent.getType());
        this.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                setIconsVisible(false);
            }
        }, MouseOutEvent.getType());

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

    private Widget createGadgetMenu() {
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
                    menu.addItem((isMinimized() ? i18n.tr("Expand") : i18n.tr("Minimize")), cmdMinimize);
                }

                menu.addItem(i18n.tr("Delete"), cmdDelete);

                if (holdedGadget.isSetupable() && !inSetup) {
                    menu.addSeparator();
                    menu.addItem(i18n.tr("Setup"), cmdSetup);
                }

                pp.setWidget(menu);
                pp.setPopupPositionAndShow(new PositionCallback() {
                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        pp.setPopupPosition(btn.getAbsoluteLeft() + btn.getOffsetWidth() - offsetWidth, btn.getAbsoluteTop() + btn.getOffsetHeight());
                        GadgetHolder.this.inMenu = true;
                    }
                });
                pp.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {
                        GadgetHolder.this.inMenu = false;
                    }
                });
                pp.show();
            } // onClick button event handler...
        }); // ClickHandler class...

        btn.setTitle(i18n.tr("Options"));
        return btn;
    }

    private void setIconsVisible(boolean visible) {
        if (!inMenu) {
            maximizer.setVisible(visible);
            gadgetMmenu.setVisible(visible);
        }
    }

    private void minimize() {
        if (isMinimized()) {
            scroll.setVisible(true);
            holdedGadget.onMinimize(false);
        } else { // minimize:
            scroll.setVisible(false);
            holdedGadget.onMinimize(true);
        }
    }

    private boolean isMinimized() {
        return !scroll.isVisible();
    }

    private void maximize() {
        if (root.isMaximized(this)) {
            root.showNormal(this);

            removeStyleDependentName(CSSNames.StyleDependent.maximized.name());
            maximizer.setResource(images.WindowMaximize());
            maximizer.setTitle(STR_MAXIMIZE);
            gadgetDragController.makeDraggable(this, title);
            holdedGadget.onMaximize(false);
            // this.dashboardPanel.setRefreshAllowed(true);
        } else { // maximize:
            if (isMinimized()) {
                minimize(); // expand when minimized!..
            }

            root.showMaximized(this);

            addStyleDependentName(CSSNames.StyleDependent.maximized.name());
            maximizer.setResource(images.WindowRestore());
            maximizer.setTitle(STR_RESTORE);
            gadgetDragController.makeNotDraggable(this);
            holdedGadget.onMaximize(true);
            // this.dashboardPanel.setRefreshAllowed(false);
        }
    }

    // --------------------------------------------------------------

    private void delete() {
        gadgetDragController.makeNotDraggable(this);
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
        buttons.add(new Button(i18n.tr("OK"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setupGadget.onOk();
                switchViewToNormal();
                root.onEvent(Reason.updateGadget);
            }
        }));
        buttons.add(new Button(i18n.tr("Cancel"), new ClickHandler() {
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
        scroll.setWidget(view);
    }

    private void switchViewToNormal() {
        switchViewTo(holdedGadget.asWidget());
        inSetup = false;
    }
}