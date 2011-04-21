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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Singleton;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.gadgets.DemoGadget;

import com.pyx4j.dashboard.client.DashboardPanel;
import com.pyx4j.dashboard.client.Layout;

@Singleton
public class DashboardViewImpl extends SimplePanel implements DashboardView {

    private static I18n i18n = I18nFactory.getI18n(DashboardViewImpl.class);

    final DashboardPanel dashboard = new DashboardPanel();

    final LayoutsSet layouts = new LayoutsSet();

    public DashboardViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new CrmHeaderDecorator("Dahboard Menu/Tools", layouts));
        main.add(dashboard);
        main.setWidth("100%");
        setWidget(main);

        layouts.setLayout2();

        fillDashboard();
    }

    private class LayoutsSet extends HorizontalPanel {

        final Image layout1 = new Image();

        final Image layout2 = new Image();

        final Image layout3 = new Image();

        public LayoutsSet() {
            super();
            setDefaultImages();

            layout1.setTitle(i18n.tr("Switch layout"));
            layout1.getElement().getStyle().setCursor(Cursor.POINTER);
            layout1.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout1();
                }
            });

            layout3.setTitle(i18n.tr("Switch layout"));
            layout2.getElement().getStyle().setCursor(Cursor.POINTER);
            layout2.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout2();
                }
            });

            layout3.setTitle(i18n.tr("Switch layout"));
            layout3.getElement().getStyle().setCursor(Cursor.POINTER);
            layout3.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setLayout3();
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
                    // TODO Auto-generated method stub

                }
            });

            this.add(layout1);
            this.add(layout2);
            this.add(layout3);
            this.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
            this.add(addGadget);
            this.setSpacing(4);
        }

        public void setLayout1() {
            setDefaultImages();
            layout1.setResource(CrmImages.INSTANCE.dashboardLayout11());
            dashboard.setLayout(new Layout(1, 1, 12));
        }

        public void setLayout2() {
            setDefaultImages();
            layout2.setResource(CrmImages.INSTANCE.dashboardLayout21());
            dashboard.setLayout(new Layout(2, 1, 12));
        }

        public void setLayout3() {
            setDefaultImages();
            layout3.setResource(CrmImages.INSTANCE.dashboardLayout31());
            dashboard.setLayout(new Layout(3, 1, 12));
        }

        private void setDefaultImages() {
            layout1.setResource(CrmImages.INSTANCE.dashboardLayout10());
            layout2.setResource(CrmImages.INSTANCE.dashboardLayout20());
            layout3.setResource(CrmImages.INSTANCE.dashboardLayout30());
        }
    }

    private void fillDashboard() {

        // fill the dashboard with demo widgets:
        dashboard.removeAllGadgets();

        int count = 0;
        for (int col = 0; col < dashboard.getLayout().getColumns(); ++col)
            for (int row = 0; row < 3; ++row) {
                // initialize a widget
                DemoGadget widget = new DemoGadget("&nbsp;Gadget&nbsp;#" + ++count);
                widget.setHeight(Random.nextInt(8) + 10 + "em");
                dashboard.addGadget(widget, col);
            }
    }
}
