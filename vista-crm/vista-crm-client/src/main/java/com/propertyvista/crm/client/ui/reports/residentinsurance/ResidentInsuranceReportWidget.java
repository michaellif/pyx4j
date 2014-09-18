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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReportWidget;

import com.propertyvista.crm.client.ui.reports.ColumnDescriptorAnchorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.ColumnDescriptorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.ITableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.ResidentInsuranceStatusDTO;

public class ResidentInsuranceReportWidget extends HTML implements IReportWidget {

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    public ResidentInsuranceReportWidget() {
    }

    @Override
    public void setData(Object data) {
        setHTML("");
        if (data == null) {
            return;
        }

        Vector<ResidentInsuranceStatusDTO> reportData = (Vector<ResidentInsuranceStatusDTO>) data;
        if (reportData.isEmpty()) {
            setHTML(NoResultsHtml.get());
            return;
        }

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        List<ITableColumnFormatter> columnDescriptors = initColumnDescriptors();

        builder.appendHtmlConstant("<table border=\"0\">");
        builder.appendHtmlConstant("<thead>");
        builder.appendHtmlConstant("<tr>");
        for (ITableColumnFormatter formatter : columnDescriptors) {
            builder.appendHtmlConstant("<th style=\"text-align: left; width: " + formatter.getWidth() + "px;\">");
            builder.append(formatter.formatHeader());
            builder.appendHtmlConstant("</th>");
        }
        builder.appendHtmlConstant("</tr>");
        builder.appendHtmlConstant("</thead>");

        builder.appendHtmlConstant("<tbody>");
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

        setHTML(builder.toSafeHtml());

        addDomHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                reportScrollBarPositionMemento = new ScrollBarPositionMemento(getElement().getScrollLeft(), getElement().getScrollLeft());
            }
        }, ScrollEvent.getType());

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Element tableHead = getElement().getElementsByTagName("thead").getItem(0);
                int tableHeadHeight = tableHead.getClientHeight();

                final Element tableBody = getElement().getElementsByTagName("tbody").getItem(0);
                tableBody.getStyle().setTop(tableHeadHeight + 1, Unit.PX);

                DOM.sinkEvents(tableBody, Event.ONSCROLL);
                DOM.setEventListener(tableBody, new EventListener() {
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
    }

    @Override
    public Object getMemento() {
        return new Object[] { getHTML(), new ScrollBarPositionMemento[] { reportScrollBarPositionMemento, tableBodyScrollBarPositionMemento } };
    }

    @Override
    public void setMemento(final Object memento) {
        if (memento != null) {
            String html = (String) (((Object[]) memento)[0]);
            setHTML(html);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    final Element tableBody = getElement().getElementsByTagName("tbody").getItem(0);
                    if (tableBody == null) {
                        return;
                    }
                    ScrollBarPositionMemento[] scrollBarPositionMementi = (ScrollBarPositionMemento[]) (((Object[]) memento)[1]);
                    if (scrollBarPositionMementi[0] != null) {
                        getElement().setScrollLeft(scrollBarPositionMementi[0].posX);
                        getElement().setScrollTop(scrollBarPositionMementi[0].posY);
                    }
                    if (scrollBarPositionMementi[1] != null) {
                        tableBody.setScrollLeft(scrollBarPositionMementi[1].posX);
                        tableBody.setScrollTop(scrollBarPositionMementi[1].posY);
                    }
                }
            });

        }
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
