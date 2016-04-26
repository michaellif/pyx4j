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
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.pyx4j.gwt.commons.ui.HorizontalPanel;
import com.pyx4j.gwt.commons.ui.Image;
import com.pyx4j.gwt.commons.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardEvent.Reason;
import com.pyx4j.widgets.client.dashboard.IGadget.ISetup;
import com.pyx4j.widgets.client.dashboard.images.DashboardImages;

final class GadgetHolder extends SimplePanel {

    private static final I18n i18n = I18n.get(GadgetHolder.class);

    private static DashboardImages images = (DashboardImages) GWT.create(DashboardImages.class);

    private static String STR_MAXIMIZE = i18n.tr("Maximize");

    private static String STR_RESTORE = i18n.tr("Restore");

    private final IGadget holdedGadget;

    private final IBoardRoot root;

    private final PickupDragController gadgetDragController;

    private final HorizontalPanel caption = new HorizontalPanel();

    private final SimplePanel holder = new SimplePanel();

    private final Label title = new Label();

    private final Image maximizer;

    private final Widget gadgetMmenu;

    private boolean inSetup = false;

    private boolean inMenu = false;

    protected boolean isReadOnly;

    // public interface:
    public IGadget getGadget() {
        return holdedGadget;
    }

    // internals:
    public GadgetHolder(IGadget gadget, PickupDragController gadgetDragController, IBoardRoot root) {
        this.isReadOnly = false;

        this.holdedGadget = gadget;
        this.gadgetDragController = gadgetDragController;
        this.root = root;
        this.addStyleName(DashboardTheme.StyleName.DashboardGadgetHolder.name());

        FlowPanel content = new FlowPanel();

        setWidget(content);

        // create caption with title and menu:
        title.setText(holdedGadget.getName());
        title.addStyleName(DashboardTheme.StyleName.DashboardGadgetHolderHeading.name());
        caption.add(title);

        caption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        maximizer = new Image(images.WindowMaximize());
        maximizer.setTitle(STR_MAXIMIZE);
        maximizer.getStyle().setCursor(Cursor.POINTER);
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

        caption.addStyleName(DashboardTheme.StyleName.DashboardGadgetHolderCaption.name());
        caption.getStyle().setProperty("minHeight", "20px");
        // override possible inherited style in order to avoid caption "jumping" on mouse over in IE9
        caption.getStyle().setProperty("lineHeight", "normal");
        caption.setWidth("100%");

        // put it together:
        content.add(caption);

        holder.setWidget(holdedGadget.asWidget());
        holder.getStyle().setOverflow(Overflow.AUTO);
        content.add(holder);

        this.getStyle().setProperty("WebkitBoxSizing", "border-box");
        this.getStyle().setProperty("MozBoxSizing", "border-box");
        this.getStyle().setProperty("boxSizing", "border-box");

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
                if (GadgetHolder.this.isReadOnly) {
                    throw new VetoDragException();
                }
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

    public void setReadOnly(boolean isReadOnly) {
        caption.setStyleDependentName(DashboardTheme.StyleDependent.readonly.name(), isReadOnly);
        this.isReadOnly = isReadOnly;
    }

    private Widget createGadgetMenu() {
        final Image btn = new Image(images.WindowMenu());
        btn.getStyle().setCursor(Cursor.POINTER);
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // show the menu:
                final PopupPanel pp = new PopupPanel(true);
                pp.setWidget(new GadgetMenu(GadgetHolder.this.isReadOnly) {
                    @Override
                    protected void onItemSelected() {
                        pp.hide();
                    }
                });
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
            }
        });

        btn.setTitle(i18n.tr("Options"));
        return btn;
    }

    private void setIconsVisible(boolean visible) {
        if (!inMenu) {
            maximizer.setVisible(visible);
            gadgetMmenu.setVisible(visible);
        }
    }

    // --------------------------------------------------------------

    private void minimize() {
        if (isMinimized()) {
            holder.setVisible(true);
            holdedGadget.onMinimize(false);
        } else { // minimize:
            holder.setVisible(false);
            holdedGadget.onMinimize(true);
        }
    }

    private boolean isMinimized() {
        return !holder.isVisible();
    }

    private void expand() {
        ((Reportboard) root).expandGadget(holdedGadget);
    }

    private void maximize() {
        if (isMaximized()) {
            root.showNormal(this);

            removeStyleDependentName(DashboardTheme.StyleDependent.maximized.name());
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

            addStyleDependentName(DashboardTheme.StyleDependent.maximized.name());
            maximizer.setResource(images.WindowRestore());
            maximizer.setTitle(STR_RESTORE);
            gadgetDragController.makeNotDraggable(this);
            holdedGadget.onMaximize(true);
            // this.dashboardPanel.setRefreshAllowed(false);
        }
    }

    private boolean isMaximized() {
        return root.isMaximized(this);
    }

    // --------------------------------------------------------------

    private void delete() {
        if (isMinimized() || isMaximized()) {
            return; // prevent deleting in non-standard state 
        }

        gadgetDragController.makeNotDraggable(this);
        holdedGadget.onDelete();
        this.removeFromParent();
        root.onEvent(Reason.removeGadget);
    }

    // --------------------------------------------------------------

    private void setup() {
        final ISetup setupGadget = holdedGadget.getSetup();

        // create main gadget setup panel: 
        final FlowPanel setup = new FlowPanel();
        setup.addStyleName(DashboardTheme.StyleName.DashboardGadgetHolderSetup.name());
        setup.add(setupGadget.asWidget());

        // create panel with Ok/Cancel buttons:
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(new Button(i18n.tr("OK"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (setupGadget.onOk()) {
                    switchViewToNormal();
                    root.onEvent(Reason.updateGadget);
                }
            }
        }));
        buttons.add(new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                switchViewToNormal();
                setupGadget.onCancel();
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
        holder.setWidget(view);
    }

    private void switchViewToNormal() {
        switchViewTo(holdedGadget.asWidget());
        inSetup = false;
    }

    // --------------------------------------------------------------

    private abstract class GadgetMenu extends MenuBar {

        /**
         * @param isReadOnly
         *            if <code>true</code> a menu will not contain commands that can change the state of the parent dashboard
         */
        GadgetMenu(boolean isReadOnly) {
            super(true);
            addStyleName(DashboardTheme.StyleName.DashboardGadgetHolderMenu.name());

            // fill menu items:
            if (holdedGadget.isMinimizable()) {
                addItem((isMinimized() ? i18n.tr("Expand") : i18n.tr("Minimize")), cmdMinimize);
            }

            if (!isReadOnly & (root instanceof Reportboard)) {
                addItem(i18n.tr("Two Columns / One Column"), cmdExpand);
            }

            if (!isReadOnly & !(isMinimized() || isMaximized())) {
                addItem(i18n.tr("Delete"), cmdDelete);
            }

            if (!isReadOnly & holdedGadget.isSetupable() && !inSetup && !isMinimized()) {
                addSeparator();
                addItem(i18n.tr("Setup"), cmdSetup);
            }
        }

        // menu items command processors:
        private final Command cmdMinimize = new Command() {
            @Override
            public void execute() {
                onItemSelected();
                minimize();
            }
        };

        private final Command cmdSetup = new Command() {
            @Override
            public void execute() {
                onItemSelected();
                setup();
            }
        };

        private final Command cmdExpand = new Command() {

            @Override
            public void execute() {
                onItemSelected();
                expand();
            }

        };

        private final Command cmdDelete = new Command() {
            @Override
            public void execute() {
                onItemSelected();
                delete();
            }
        };

        /**
         * Override to close popup panel where menu resides
         */
        abstract protected void onItemSelected();
    }
}