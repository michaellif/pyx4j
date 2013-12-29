/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.eftvariance;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.reports.ReportWidget;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.client.ui.reports.Column;
import com.propertyvista.crm.client.ui.reports.CommonReportStyles;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDTO;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDetailsDTO;
import com.propertyvista.domain.tenant.lease.Lease;

public class EftVarianceReportWidget extends Composite implements ReportWidget {

    private static final I18n i18n = I18n.get(EftVarianceReportWidget.class);

    private final HTML reportHtml;

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    public EftVarianceReportWidget() {
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

        Vector<EftVarianceReportRecordDTO> eftReportRecords = (Vector<EftVarianceReportRecordDTO>) data;
        if (eftReportRecords.isEmpty()) {
            reportHtml.setHTML(NoResultsHtml.get());
            onWidgetReady.execute();
            return;
        }

        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        int[] VERY_SHORT_COLUMN_WIDTHS = new int[] { 1200, 80, 60 };
        int[] SHORT_COLUMN_WIDTHS = new int[] { 1200, 100, 80 };
        int[] LONG_COLUMN_WIDTHS = new int[] { 1200, 200, 150 };
        int[] VERY_LONG_COLUMN_WIDTHS = new int[] { 1200, 400, 350 };
        List<Column> columns = Arrays.asList(//@formatter:off
                        new Column(i18n.tr("Building"), null, VERY_SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Unit"), null, VERY_SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Lease ID"), null, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Tenant Name"), null, LONG_COLUMN_WIDTHS),
                        new Column(i18n.tr("Bank Account No"), null, VERY_LONG_COLUMN_WIDTHS),

                        new Column(i18n.tr("Total EFT"), null, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Charges"), null, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Difference"),null, VERY_SHORT_COLUMN_WIDTHS)
        );//@formatter:on

        int tableWidth = 0;
        for (Column c : columns) {
            tableWidth += c.getEffectiveWidth();
        }
        builder.appendHtmlConstant("<table style=\"display: inline-block; position: absolute; left: 0px; width: " + tableWidth
                + "px; top: 31px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border=\"0\">");

        builder.appendHtmlConstant("<thead class=\"" + CommonReportStyles.RReportTableFixedHeader.name() + "\">");
        builder.appendHtmlConstant("<tr>");
        for (Column c : columns) {
            builder.appendHtmlConstant("<th style=\"text-align: left; width: " + c.getEffectiveWidth() + "px;\">");
            builder.appendEscaped(c.name);
            builder.appendHtmlConstant("</th>");
        }
        builder.appendHtmlConstant("</tr>");
        builder.appendHtmlConstant("</thead>");

        builder.appendHtmlConstant("<tbody class=\"" + CommonReportStyles.RReportTableScrollableBody.name() + "\">");

        NumberFormat totalFormat = NumberFormat.getFormat(EntityFactory.getEntityPrototype(EftVarianceReportRecordDTO.class).leaseTotals().totalEft().getMeta()
                .getFormat());
        String buildingId = eftReportRecords.get(0).building().getValue();
        BigDecimal buildingEftTotal = new BigDecimal("0.00");
        BigDecimal buildingChargesTotal = new BigDecimal("0.00");
        BigDecimal buildingDifferenceTotal = new BigDecimal("0.00");

        for (EftVarianceReportRecordDTO record : eftReportRecords) {
            // building totals:
            if (!buildingId.equals(record.building().getValue())) {
                addBuildingTotals(builder, totalFormat, buildingId, buildingEftTotal, buildingChargesTotal, buildingDifferenceTotal);

                buildingId = record.building().getValue();
                buildingEftTotal = new BigDecimal("0.00");
                buildingChargesTotal = new BigDecimal("0.00");
                buildingDifferenceTotal = new BigDecimal("0.00");

            }

            builder.appendHtmlConstant("<tr>");
            builder.appendHtmlConstant("<td style='width: " + columns.get(0).getEffectiveWidth() + "px;'>");
            builder.appendEscaped(record.building().getValue());
            builder.appendHtmlConstant("</td>");

            builder.appendHtmlConstant("<td style='width: " + columns.get(1).getEffectiveWidth() + "px;'>");
            builder.appendEscaped(record.unit().getValue());

            builder.appendHtmlConstant("</td>");

            builder.appendHtmlConstant("<td style='width: " + columns.get(2).getEffectiveWidth() + "px;'>");

            builder.appendHtmlConstant("<a href='"
                    + AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false, AppPlaceEntityMapper.resolvePlace(Lease.class, record.leaseId_().getPrimaryKey()))
                    + "'>");
            builder.appendEscaped(record.leaseId().getValue());
            builder.appendHtmlConstant("</a>");
            builder.appendHtmlConstant("</td>");

            boolean isFirstLine = true;

            for (EftVarianceReportRecordDetailsDTO details : record.details()) {

                if (isFirstLine) {
                    isFirstLine = false;
                } else {
                    builder.appendHtmlConstant("<tr>");
                    builder.appendHtmlConstant("<td></td><td></td><td></td>");
                }
                builder.appendHtmlConstant("<td style='width: " + columns.get(3).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.tenantName().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(4).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.paymentMethod().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(5).getEffectiveWidth() + "px;' class='" + CommonReportStyles.RCellNumber.name()
                        + "'>");
                builder.appendEscaped(details.totalEft().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(6).getEffectiveWidth() + "px;' class='" + CommonReportStyles.RCellNumber.name()
                        + "'>");
                builder.appendEscaped(details.charges().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(7).getEffectiveWidth() + "px;' class='" + CommonReportStyles.RCellNumber.name()
                        + "'>");
                builder.appendEscaped(details.difference().getStringView());
                builder.appendHtmlConstant("</td>");

                builder.appendHtmlConstant("</tr>");

            }
            // lease totals:
            builder.appendHtmlConstant("<tr>");
            builder.appendHtmlConstant("<td colspan='2'></td>");
            builder.appendHtmlConstant("<td colspan='3' style='text-align:left;' class='" + CommonReportStyles.RRowTotal.name() + "'>");
            builder.appendEscaped(i18n.tr("Total for lease:"));
            builder.appendHtmlConstant("</td>");
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>");
            builder.appendEscaped(record.leaseTotals().totalEft().getStringView());
            builder.appendHtmlConstant("</td>");
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>");
            builder.appendEscaped(record.leaseTotals().charges().getStringView());
            builder.appendHtmlConstant("</td>");
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>");
            builder.appendEscaped(record.leaseTotals().difference().getStringView());
            builder.appendHtmlConstant("</td>");
            builder.appendHtmlConstant("</tr>");

            buildingEftTotal = buildingEftTotal.add(record.leaseTotals().totalEft().getValue());
            buildingChargesTotal = record.leaseTotals().charges().isNull() ? buildingChargesTotal : buildingChargesTotal.add(record.leaseTotals().charges()
                    .getValue());
            buildingDifferenceTotal = record.leaseTotals().difference().isNull() ? buildingDifferenceTotal : buildingDifferenceTotal.add(record.leaseTotals()
                    .difference().getValue());
        }
        addBuildingTotals(builder, totalFormat, buildingId, buildingEftTotal, buildingChargesTotal, buildingDifferenceTotal);

        builder.appendHtmlConstant("</table>");
        reportHtml.setHTML(builder.toSafeHtml());

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

    private void addBuildingTotals(SafeHtmlBuilder builder, NumberFormat totalFormat, String buildingId, BigDecimal totalEft, BigDecimal totalCharges,
            BigDecimal totalDifference) {

        builder.appendHtmlConstant("<tr>");
        builder.appendHtmlConstant("<td colspan='5' style='text-align:left;' class='" + CommonReportStyles.RRowTotal.name() + "'>");
        builder.appendEscaped(i18n.tr("Total for building {0}:", buildingId));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>");
        builder.appendEscaped(totalFormat.format(totalEft));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>");
        builder.appendEscaped(totalFormat.format(totalCharges));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>");
        builder.appendEscaped(totalFormat.format(totalDifference));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("</tr>");
    }

}
