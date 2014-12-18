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
 */
package com.propertyvista.crm.client.ui.reports.availability;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReportWidget;
import com.pyx4j.site.client.backoffice.ui.prime.report.ReportTable;
import com.pyx4j.site.client.backoffice.ui.prime.report.ReportTable.CellFormatter;
import com.pyx4j.widgets.client.memento.IMementoAware;
import com.pyx4j.widgets.client.memento.IMementoInput;
import com.pyx4j.widgets.client.memento.IMementoOutput;

import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;

public class AvailabilityReportWidget extends FlowPanel implements IReportWidget, IMementoAware {

    private static final I18n i18n = I18n.get(AvailabilityReportWidget.class);

    ScrollBarPositionMemento scrollBarPositionMemento;

    @Override
    public void setData(Object data) {
        clear();
        if (data == null) {
            return;
        }

        AvailabilityReportDataDTO reportData = (AvailabilityReportDataDTO) data;
        if (reportData.unitStatuses.isEmpty()) {
            add(new HTML(NoResultsHtml.get()));
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
        add(header);

        ReportTable reportTable = new ReportTable(Arrays.asList(AvailabilityReportTableColumnsHolder.AVAILABILITY_TABLE_COLUMNS));
        reportTable.populate(reportData.unitStatuses);
        add(reportTable);
        addDomHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                scrollBarPositionMemento = new ScrollBarPositionMemento(getElement().getScrollLeft(), getElement().getScrollTop());
            }
        }, ScrollEvent.getType());
    }

    @Override
    public void saveState(IMementoOutput memento) {
        memento.write(scrollBarPositionMemento);
    }

    @Override
    public void restoreState(IMementoInput memento) {
        ScrollBarPositionMemento scrollBarPosition = (ScrollBarPositionMemento) memento.read();
        getElement().setScrollLeft(scrollBarPosition.posX);
        getElement().setScrollTop(scrollBarPosition.posY);
    }

}
