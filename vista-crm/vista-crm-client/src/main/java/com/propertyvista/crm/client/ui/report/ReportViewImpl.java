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

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Singleton;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.widgets.client.dashboard.Report;
import com.pyx4j.widgets.client.dashboard.Report.Location;

import com.propertyvista.crm.client.ui.decorations.CrmHeaderDecorator;
import com.propertyvista.crm.client.ui.gadgets.DemoGadget;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;

@Singleton
public class ReportViewImpl extends SimplePanel implements ReportView {

    private final ScrollPanel scroll;

    private Report report;

    public ReportViewImpl() {
        VerticalPanel main = new VerticalPanel();
        main.add(new CrmHeaderDecorator("Report Menu/Tools"));

        scroll = new ScrollPanel();
        scroll.getElement().getStyle().setPosition(Position.ABSOLUTE);
        scroll.getElement().getStyle().setTop(45, Unit.PX);
        scroll.getElement().getStyle().setLeft(0, Unit.PX);
        scroll.getElement().getStyle().setRight(0, Unit.PX);
        scroll.getElement().getStyle().setBottom(0, Unit.PX);
        main.add(scroll);

        main.setSize("100%", "100%");
        setWidget(main);

        fillReport();
    }

    private void fillReport() {

        report = new Report();

        // fill the dashboard with demo widgets:
        int count = 0;
        for (int row = 0; row < 5; ++row) {
            // initialize a widget
            GadgetMetadata gmd = EntityFactory.create(GadgetMetadata.class);
            gmd.name().setValue("Gadget #" + ++count);
            DemoGadget widget = new DemoGadget(gmd);
            widget.setFullWidth(row % 2 > 0);
            report.addGadget(widget, Location.Any);
        }

        scroll.setWidget(report);
    }
}
