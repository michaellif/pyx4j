/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.availability;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.ReportWidget;
import com.pyx4j.site.client.ui.reports.widgets.ReportTable;
import com.pyx4j.site.client.ui.reports.widgets.ReportTable.CellFormatter;

import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;

public class AvailabilityReportWidget implements ReportWidget {

    private static final I18n i18n = I18n.get(AvailabilityReportWidget.class);

    FlowPanel reportPanel;

    ScrollBarPositionMemento scrollBarPositionMemento;

    {
        reportPanel = new FlowPanel();
        reportPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportPanel.getElement().getStyle().setLeft(0, Unit.PX);
        reportPanel.getElement().getStyle().setTop(0, Unit.PX);
        reportPanel.getElement().getStyle().setRight(0, Unit.PX);
        reportPanel.getElement().getStyle().setBottom(0, Unit.PX);
        reportPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
    }

    @Override
    public Widget asWidget() {
        return reportPanel;
    }

    @Override
    public void setData(Object data, Command onWidgetReady) {
        reportPanel.clear();
        if (data == null) {
            onWidgetReady.execute();
            return;
        }

        AvailabilityReportDataDTO reportData = (AvailabilityReportDataDTO) data;
        if (reportData.unitStatuses.isEmpty()) {
            reportPanel.add(new HTML(NoResultsHtml.get()));
            onWidgetReady.execute();
            return;
        }
        SafeHtmlBuilder bb = new SafeHtmlBuilder();
        bb.appendHtmlConstant("<div style=\"text-align: center; font-size: 22pt; line-height: 22pt;\">");
        bb.appendEscaped(i18n.tr("Unit Availability Report"));
        bb.appendHtmlConstant("</div>");
        bb.appendHtmlConstant("<div style=\"text-align: center;\">");
        bb.appendEscaped(i18n.tr("As of Date: {0}", reportData.asOf));
        bb.appendHtmlConstant("</div>");
        HTML header = new HTML(bb.toSafeHtml());
        reportPanel.add(header);

        ReportTable reportTable = new ReportTable(Arrays.asList(AvailabilityReportTableColumnsHolder.AVAILABILITY_TABLE_COLUMNS),
                new ArrayList<CellFormatter>());
        reportTable.populate(reportData.unitStatuses);
        reportPanel.add(reportTable);
        reportPanel.addDomHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                scrollBarPositionMemento = new ScrollBarPositionMemento(reportPanel.getElement().getScrollLeft(), reportPanel.getElement().getScrollTop());
            }
        }, ScrollEvent.getType());

        onWidgetReady.execute();
    }

    @Override
    public Object getMemento() {
        return scrollBarPositionMemento;
    }

    @Override
    public void setMemento(Object memento, Command onWidgetReady) {
        if (memento != null) {
            ScrollBarPositionMemento scrollBarPosition = (ScrollBarPositionMemento) memento;
            reportPanel.getElement().setScrollLeft(scrollBarPosition.posX);
            reportPanel.getElement().setScrollTop(scrollBarPosition.posY);
        }

        onWidgetReady.execute();
    }

}
