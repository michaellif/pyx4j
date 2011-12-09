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
package com.propertyvista.crm.client.ui.report;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.Reportboard;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.board.BoardBase;
import com.propertyvista.crm.client.ui.gadgets.addgadgetdialog.GadgetDirectoryDialog;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class ReportPanel extends BoardBase implements ReportView {

    private static I18n i18n = I18n.get(ReportPanel.class);

    public ReportPanel() {
    }

    @Override
    protected IBoard createBoard() {
        return new Reportboard();
    }

    @Override
    protected Widget createActionsWidget() {
        return new ActionsWidget();
    }

    @Override
    protected LayoutType translateLayout(BoardLayout layout) {
        return LayoutType.Report; // always!..
    }

    @Override
    protected void setLayout(LayoutType layout) {
        // nothing to do!..
    }

    private class ActionsWidget extends HorizontalPanel {

        public ActionsWidget() {
            super();

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
                    final GadgetDirectoryDialog agb = new GadgetDirectoryDialog(ReportPanel.this);
//                    agb.setPopupPositionAndShow(new PositionCallback() {
//                        @Override
//                        public void setPosition(int offsetWidth, int offsetHeight) {
//                            agb.setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, (Window.getClientHeight() - offsetHeight) / 2);
//                        }
//                    });

                    agb.show();
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
            print.getElement().getStyle().setCursor(Cursor.POINTER);

            add(addGadget);
            add(print);
            this.setSpacing(4);

        }
    }

}
