/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.residentinsurance;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.reports.ReportWidget;

import com.propertyvista.crm.client.ui.reports.ColumnDescriptorAnchorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.ColumnDescriptorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.CommonReportStyles;
import com.propertyvista.crm.client.ui.reports.ITableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.ResidentInsuranceStatusDTO;

public class ResidentInsuranceReportWidget extends Composite implements ReportWidget {

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    private final HTML reportHtml;

    public ResidentInsuranceReportWidget() {
        reportHtml = new HTML();
        reportHtml.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportHtml.getElement().getStyle().setLeft(0, Unit.PX);
        reportHtml.getElement().getStyle().setRight(0, Unit.PX);
        reportHtml.getElement().getStyle().setTop(0, Unit.PX);
        reportHtml.getElement().getStyle().setBottom(0, Unit.PX);

        reportHtml.getElement().getStyle().setOverflowX(Overflow.SCROLL);
        reportHtml.getElement().getStyle().setOverflowY(Overflow.AUTO);

        initWidget(reportHtml);
    }

    @Override
    public void setData(Object data, Command onWidgetReady) {
        reportHtml.setHTML("");

        if (data == null) {
            onWidgetReady.execute();
            return;
        }

        Vector<ResidentInsuranceStatusDTO> reportData = (Vector<ResidentInsuranceStatusDTO>) data;
        if (reportData.isEmpty()) {
            reportHtml.setHTML(NoResultsHtml.get());
            onWidgetReady.execute();
            return;
        }

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        List<ITableColumnFormatter> columnDescriptors = initColumnDescriptors();

        int totalWidth = 0;
        for (ITableColumnFormatter formatter : columnDescriptors) {
            totalWidth += formatter.getWidth();
        }
        builder.appendHtmlConstant("<table style=\"display: inline-block; position: absolute; left: 0px; width: " + totalWidth
                + "px; top: 31px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border=\"0\">");
        builder.appendHtmlConstant("<thead class=\"" + CommonReportStyles.RReportTableFixedHeader.name() + "\">");
        builder.appendHtmlConstant("<tr>");
        for (ITableColumnFormatter formatter : columnDescriptors) {
            builder.appendHtmlConstant("<th style=\"text-align: left; width: " + formatter.getWidth() + "px;\">");
            builder.append(formatter.formatHeader());
            builder.appendHtmlConstant("</th>");
        }
        builder.appendHtmlConstant("</tr>");
        builder.appendHtmlConstant("</thead>");

        builder.appendHtmlConstant("<tbody class=\"" + CommonReportStyles.RReportTableScrollableBody.name() + "\">");
        for (ResidentInsuranceStatusDTO insuranceStatus : reportData) {
            builder.appendHtmlConstant("<tr>");
            for (ITableColumnFormatter desc : columnDescriptors) {
                builder.appendHtmlConstant("<td style=\"width: " + desc.getWidth() + "px;\">");
                builder.append(desc.formatContent(insuranceStatus));
                builder.appendHtmlConstant("</td>");
            }
            builder.appendHtmlConstant("</tr>");
        }
        builder.appendHtmlConstant("</tbody>");

        reportHtml.setHTML(builder.toSafeHtml());

        reportHtml.addDomHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                reportScrollBarPositionMemento = new ScrollBarPositionMemento(reportHtml.getElement().getScrollLeft(), reportHtml.getElement().getScrollLeft());
            }
        }, ScrollEvent.getType());

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Element tableHead = reportHtml.getElement().getElementsByTagName("thead").getItem(0);
                int tableHeadHeight = tableHead.getClientHeight();

                final Element tableBody = reportHtml.getElement().getElementsByTagName("tbody").getItem(0);
                tableBody.getStyle().setTop(tableHeadHeight + 1, Unit.PX);

                DOM.sinkEvents((com.google.gwt.user.client.Element) tableBody, Event.ONSCROLL);
                DOM.setEventListener((com.google.gwt.user.client.Element) tableBody, new EventListener() {
                    @Override
                    public void onBrowserEvent(Event event) {
                        if (event.getTypeInt() == Event.ONSCROLL
                                && Element.as(Event.getCurrentEvent().getEventTarget()).getTagName().toUpperCase().equals("TBODY")) {
                            tableBodyScrollBarPositionMemento = new ScrollBarPositionMemento(tableBody.getScrollLeft(), tableBody.getScrollTop());
                        }
                    }
                });

            }
        });

        onWidgetReady.execute();
    }

    @Override
    public Object getMemento() {
        return new Object[] { reportHtml.getHTML(), new ScrollBarPositionMemento[] { reportScrollBarPositionMemento, tableBodyScrollBarPositionMemento } };
    }

    @Override
    public void setMemento(final Object memento, Command onWidgetReady) {
        if (memento != null) {
            String html = (String) (((Object[]) memento)[0]);
            reportHtml.setHTML(html);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    final Element tableBody = reportHtml.getElement().getElementsByTagName("tbody").getItem(0);
                    if (tableBody == null) {
                        return;
                    }
                    ScrollBarPositionMemento[] scrollBarPositionMementi = (ScrollBarPositionMemento[]) (((Object[]) memento)[1]);
                    if (scrollBarPositionMementi[0] != null) {
                        reportHtml.getElement().setScrollLeft(scrollBarPositionMementi[0].posX);
                        reportHtml.getElement().setScrollTop(scrollBarPositionMementi[0].posY);
                    }
                    if (scrollBarPositionMementi[1] != null) {
                        tableBody.setScrollLeft(scrollBarPositionMementi[1].posX);
                        tableBody.setScrollTop(scrollBarPositionMementi[1].posY);
                    }
                }
            });

        }
        onWidgetReady.execute();
    }

    private List<ITableColumnFormatter> initColumnDescriptors() {
        ResidentInsuranceStatusDTO proto = EntityFactory.getEntityPrototype(ResidentInsuranceStatusDTO.class);

        int SHORT_COLUMN_WIDTH = 50;
        int NORMAL_COLUMN_WIDTH = 100;
        int INCREASED_COLUMN_WIDTH = 150;
        int WIDE_COLUMN_WIDTH = 200;
        return Arrays.<ITableColumnFormatter> asList(//@formatter:off
                new ColumnDescriptorTableColumnFormatter(NORMAL_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.hasResidentInsurance()).build()),
                new ColumnDescriptorAnchorTableColumnFormatter(WIDE_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.namesOnLease()).build()),
                new ColumnDescriptorTableColumnFormatter(NORMAL_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.building()).build()),
                new ColumnDescriptorTableColumnFormatter(SHORT_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.unit()).build()),
                new ColumnDescriptorTableColumnFormatter(WIDE_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.address()).build()),
                new ColumnDescriptorTableColumnFormatter(NORMAL_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.postalCode()).build()),                
                new ColumnDescriptorTableColumnFormatter(INCREASED_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.provider()).build()),
                new ColumnDescriptorTableColumnFormatter(NORMAL_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.liabilityCoverage()).build()),
                new ColumnDescriptorTableColumnFormatter(NORMAL_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.expiryDate()).build()),
                new ColumnDescriptorAnchorTableColumnFormatter(INCREASED_COLUMN_WIDTH, new MemberColumnDescriptor.Builder(proto.certificate()).build())
        );//@formatter:on
    }

}
