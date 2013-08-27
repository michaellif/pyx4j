/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-19
 * @author Amer Sohail
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.customercreditcheck;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.ReportWidget;

import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.CustomerCreditCheckReportDataDTO;
import com.propertyvista.domain.tenant.CustomerCreditCheck;

public class CustomerCreditCheckReportWidget implements ReportWidget {

    private static final I18n i18n = I18n.get(CustomerCreditCheckReportWidget.class);

    private static ColumnDescriptor[] CREDITCHECK_TABLE_COLUMNS;

    static {
        CustomerCreditCheck proto = EntityFactory.getEntityPrototype(CustomerCreditCheck.class);

        CREDITCHECK_TABLE_COLUMNS = new ColumnDescriptor[] {

        new MemberColumnDescriptor.Builder(proto.screening().screene().person().name()).title(i18n.tr("Tenant")).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto.screening().screene().person().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.screening().screene().person().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.creditCheckDate()).build(),
                new MemberColumnDescriptor.Builder(proto.createdBy().name()).title(i18n.tr("Created By")).build(),
                new MemberColumnDescriptor.Builder(proto.createdBy().name().firstName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.createdBy().name().lastName()).searchableOnly().build(),
                new MemberColumnDescriptor.Builder(proto.amountChecked()).build(), new MemberColumnDescriptor.Builder(proto.riskCode()).build(),
                new MemberColumnDescriptor.Builder(proto.creditCheckResult()).build(), new MemberColumnDescriptor.Builder(proto.amountApproved()).build(),
                new MemberColumnDescriptor.Builder(proto.reason()).build()

        //statuses
        };
    }

    HTML reportHtml;

    ScrollBarPositionMemento scrollBarPositionMemento;

    {
        reportHtml = new HTML();
        reportHtml.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportHtml.getElement().getStyle().setLeft(0, Unit.PX);
        reportHtml.getElement().getStyle().setTop(0, Unit.PX);
        reportHtml.getElement().getStyle().setRight(0, Unit.PX);
        reportHtml.getElement().getStyle().setBottom(0, Unit.PX);
        reportHtml.getElement().getStyle().setOverflow(Overflow.AUTO);
    }

    @Override
    public Widget asWidget() {
        return reportHtml;
    }

    @Override
    public void setData(Object data, Command onWidgetReady) {
        reportHtml.setHTML("");
        if (data == null) {
            onWidgetReady.execute();
            return;
        }
        CustomerCreditCheckReportDataDTO reportData = (CustomerCreditCheckReportDataDTO) data;
        if (reportData.unitStatuses.isEmpty()) {
            onWidgetReady.execute();
            reportHtml.setHTML(NoResultsHtml.get());
            return;
        }
        SafeHtmlBuilder bb = new SafeHtmlBuilder();
        bb.appendHtmlConstant("<div style=\"text-align: center; font-size: 22px; line-height: 22px;\">");
        bb.appendEscaped(i18n.tr("Customer Credit Check Report"));
        bb.appendHtmlConstant("</div>");
        bb.appendHtmlConstant("<div>");
        bb.appendHtmlConstant("</div>");

        bb.appendHtmlConstant("<table style=\"white-space: nowrap; border-collapse: separate; border-spacing: 15pt;\">");
        bb.appendHtmlConstant("<tr>");

        for (ColumnDescriptor desc : CREDITCHECK_TABLE_COLUMNS) {
            bb.appendHtmlConstant("<th style=\"text-align: left\">");
            bb.appendEscaped(desc.getColumnTitle());
            bb.appendHtmlConstant("</th>");
        }

        bb.appendHtmlConstant("</tr>");

        for (CustomerCreditCheck status : reportData.unitStatuses) {
            bb.appendHtmlConstant("<tr>");
            for (ColumnDescriptor desc : CREDITCHECK_TABLE_COLUMNS) {
                cell(bb, desc.convert(status));
            }
            bb.appendHtmlConstant("</tr>");
        }
        bb.appendHtmlConstant("</table>");

        reportHtml.setHTML(bb.toSafeHtml());
        reportHtml.addDomHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                scrollBarPositionMemento = new ScrollBarPositionMemento(reportHtml.getElement().getScrollLeft(), reportHtml.getElement().getScrollTop());
            }
        }, ScrollEvent.getType());

        onWidgetReady.execute();
    }

    @Override
    public Object getMemento() {
        return new Object[] { reportHtml.getHTML(), scrollBarPositionMemento };
    }

    @Override
    public void setMemento(final Object memento, Command onWidgetReady) {
        if (memento != null) {
            String html = (String) (((Object[]) memento)[0]);
            reportHtml.setHTML(html);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    ScrollBarPositionMemento scrollBarPosition = (ScrollBarPositionMemento) (((Object[]) memento)[1]);
                    reportHtml.getElement().setScrollLeft(scrollBarPosition.posX);
                    reportHtml.getElement().setScrollTop(scrollBarPosition.posY);
                }
            });

        }
        onWidgetReady.execute();
    }

    private void cell(SafeHtmlBuilder bb, String data) {
        bb.appendHtmlConstant("<td>");
        bb.appendEscaped(data);
        bb.appendHtmlConstant("</td>");
    }
}
