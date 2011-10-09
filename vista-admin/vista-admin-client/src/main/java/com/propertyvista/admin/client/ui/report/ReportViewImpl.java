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
package com.propertyvista.admin.client.ui.report;


import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.Reportboard;
import com.pyx4j.widgets.client.dashboard.Reportboard.Location;

import com.propertyvista.admin.client.resources.AdminImages;
import com.propertyvista.admin.client.ui.decorations.AdminHeaderDecorator;
import com.propertyvista.admin.client.ui.gadgets.AddGadgetBox;
import com.propertyvista.admin.client.ui.gadgets.GadgetsFactory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata;

public class ReportViewImpl extends SimplePanel implements ReportView {

    private static I18n i18n = I18n.get(ReportViewImpl.class);

    private final ScrollPanel scroll;

    private Reportboard report;

    public ReportViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new AdminHeaderDecorator(i18n.tr("Report"), new AddWidgetButton()));

        scroll = new ScrollPanel();
        scroll.getElement().getStyle().setPosition(Position.ABSOLUTE);
        scroll.getElement().getStyle().setTop(2.5, Unit.EM);
        scroll.getElement().getStyle().setLeft(0, Unit.PX);
        scroll.getElement().getStyle().setRight(0, Unit.PX);
        scroll.getElement().getStyle().setBottom(0, Unit.PX);
        main.add(scroll);

        main.setSize("100%", "100%");
        setWidget(main);
    }

    @Override
    public void fillDashboard(DashboardMetadata dashboardMetadata) {
        if (dashboardMetadata.isEmpty()) {
            return;
        }

        report = new Reportboard();

        // fill the dashboard with gadgets:
        for (GadgetMetadata gmd : dashboardMetadata.gadgets()) {
            IGadget gadget = GadgetsFactory.createGadget(gmd.type().getValue(), gmd);
            if (gadget != null) {
                Reportboard.Location location;
                // decode columns:
                switch (gmd.column().getValue()) {
                case 0:
                    location = Location.Left;
                    break;
                case 1:
                    location = Location.Right;
                    break;
                default:
                    location = Location.Full;
                }
                report.addGadget(gadget, location);
                gadget.start(); // allow gadget execution... 
            }
        }

        scroll.setWidget(report);
    }

    private class AddWidgetButton extends SimplePanel {

        public AddWidgetButton() {
            super();

            final Image addGadget = new Image(AdminImages.INSTANCE.dashboardAddGadget());
            addGadget.getElement().getStyle().setCursor(Cursor.POINTER);
            addGadget.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    addGadget.setResource(AdminImages.INSTANCE.dashboardAddGadgetHover());
                }
            });
            addGadget.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent event) {
                    addGadget.setResource(AdminImages.INSTANCE.dashboardAddGadget());
                }
            });
            addGadget.setTitle(i18n.tr("Add Gadget..."));
            addGadget.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    final AddGadgetBox agb = new AddGadgetBox();
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
                                report.insertGadget(gadget, (gadget.isFullWidth() ? Reportboard.Location.Full : Reportboard.Location.Left), 0);
                                gadget.start();
                            }
                        }
                    });

                    agb.show();
                }
            });

            setWidget(addGadget);
        }

    }
}
