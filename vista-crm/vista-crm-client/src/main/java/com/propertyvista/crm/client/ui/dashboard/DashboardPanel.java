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
package com.propertyvista.crm.client.ui.dashboard;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.board.BoardBase;
import com.propertyvista.crm.client.ui.gadgets.AddGadgetBox;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class DashboardPanel extends BoardBase implements DashboardView {

    private static I18n i18n = I18n.get(DashboardPanel.class);

    private LayoutsSet layouts;

    public DashboardPanel() {
        this(false);
    }

    public DashboardPanel(boolean readOnly) {
        super(readOnly);
    }

//
// Internals:
//
    @Override
    protected IBoard createBoard() {
        return new Dashboard();
    }

    @Override
    protected Widget createActionsWidget() {
        return (layouts = new LayoutsSet());
    }

    @Override
    protected void setLayout(LayoutType layoutType) {
        layouts.setLayout(layoutType);
    }

    @Override
    protected LayoutType translateLayout(BoardLayout layout) {
        LayoutType layoutType = null;
        switch (layout) {
        case One:
            layoutType = LayoutType.One;
            break;
        case Two11:
            layoutType = LayoutType.Two11;
            break;
        case Two12:
            layoutType = LayoutType.Two12;
            break;
        case Two21:
            layoutType = LayoutType.Two21;
            break;
        case Three:
            layoutType = LayoutType.Three;
            break;
        }
        return layoutType;
    }

    private class LayoutsSet extends HorizontalPanel {

        final Image layout1 = new Image();

        final Image layout12 = new Image();

        final Image layout21 = new Image();

        final Image layout22 = new Image();

        final Image layout3 = new Image();

        public LayoutsSet() {
            setDefaultImages();

            layout1.setTitle(i18n.tr("Switch layout"));
            layout1.getElement().getStyle().setCursor(Cursor.POINTER);
            layout1.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.One);
                }
            });

            layout12.setTitle(i18n.tr("Switch layout"));
            layout12.getElement().getStyle().setCursor(Cursor.POINTER);
            layout12.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Two12);
                }
            });

            layout21.setTitle(i18n.tr("Switch layout"));
            layout21.getElement().getStyle().setCursor(Cursor.POINTER);
            layout21.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Two21);
                }
            });

            layout22.setTitle(i18n.tr("Switch layout"));
            layout22.getElement().getStyle().setCursor(Cursor.POINTER);
            layout22.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Two11);
                }
            });

            layout3.setTitle(i18n.tr("Switch layout"));
            layout3.getElement().getStyle().setCursor(Cursor.POINTER);
            layout3.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout(LayoutType.Three);
                }
            });

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
                    final AddGadgetBox agb = new AddGadgetBox(getDashboardMetadata().type().getValue());
                    agb.setPopupPositionAndShow(new PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            agb.setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, (Window.getClientHeight() - offsetHeight) / 2);
                        }
                    });

                    agb.addCloseHandler(new CloseHandler<PopupPanel>() {
                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {
                            IGadget gadget = agb.getSelectedGadget();
                            if (gadget != null) {
                                getBoard().addGadget(gadget);
                                gadget.start();
                            }
                        }
                    });

                    agb.show();
                }
            });

            this.add(layout1);
            this.add(layout12);
            this.add(layout21);
            this.add(layout22);
            this.add(layout3);
            if (!isReadOnly()) {
                this.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
                this.add(addGadget);
            }
            this.setSpacing(4);
        }

        public void setLayout(LayoutType layoutType) {
            switch (layoutType) {
            case One:
                getBoard().setLayout(BoardLayout.One);
                setDefaultImages();
                layout1.setResource(CrmImages.INSTANCE.dashboardLayout1_1());
                break;
            case Two11:
                getBoard().setLayout(BoardLayout.Two11);
                setDefaultImages();
                layout22.setResource(CrmImages.INSTANCE.dashboardLayout22_1());
                break;
            case Two12:
                getBoard().setLayout(BoardLayout.Two12);
                setDefaultImages();
                layout12.setResource(CrmImages.INSTANCE.dashboardLayout12_1());
                break;
            case Two21:
                getBoard().setLayout(BoardLayout.Two21);
                setDefaultImages();
                layout21.setResource(CrmImages.INSTANCE.dashboardLayout21_1());
                break;
            case Three:
                getBoard().setLayout(BoardLayout.Three);
                setDefaultImages();
                layout3.setResource(CrmImages.INSTANCE.dashboardLayout3_1());
                break;
            }
        }

        private void setDefaultImages() {
            layout1.setResource(CrmImages.INSTANCE.dashboardLayout1_0());
            layout12.setResource(CrmImages.INSTANCE.dashboardLayout12_0());
            layout21.setResource(CrmImages.INSTANCE.dashboardLayout21_0());
            layout22.setResource(CrmImages.INSTANCE.dashboardLayout22_0());
            layout3.setResource(CrmImages.INSTANCE.dashboardLayout3_0());
        }
    }

}
