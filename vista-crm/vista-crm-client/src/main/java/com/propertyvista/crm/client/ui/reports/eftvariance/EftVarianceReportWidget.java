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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.report.AbstractReport;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReportWidget;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.reports.Column;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDTO;
import com.propertyvista.crm.rpc.dto.reports.EftVarianceReportRecordDetailsDTO;
import com.propertyvista.domain.tenant.lease.Lease;

public class EftVarianceReportWidget extends HTML implements IReportWidget {

    private static final I18n i18n = I18n.get(EftVarianceReportWidget.class);

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    public EftVarianceReportWidget() {

    }

    @Override
    public void setData(Object data) {
        setHTML("");
        if (data == null) {
            return;
        }

        Vector<EftVarianceReportRecordDTO> eftReportRecords = (Vector<EftVarianceReportRecordDTO>) data;
        if (eftReportRecords.isEmpty()) {
            setHTML(NoResultsHtml.get());
            return;
        }

        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        int[] VERY_SHORT_COLUMN_WIDTHS = new int[] { 1200, 80, 60 };
        int[] SHORT_COLUMN_WIDTHS = new int[] { 1200, 100, 80 };
        int[] LONG_COLUMN_WIDTHS = new int[] { 1200, 200, 150 };
        int[] VERY_LONG_COLUMN_WIDTHS = new int[] { 1200, 400, 350 };
        List<Column> columns = Arrays.asList(//@formatter:off
                        new Column(i18n.tr("Notice"), null, VERY_SHORT_COLUMN_WIDTHS),
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
        builder.appendHtmlConstant("<table style=\"display: inline-block; width: " + tableWidth
                + "px; top: 31px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border=\"0\">");

        builder.appendHtmlConstant("<thead>");
        builder.appendHtmlConstant("<tr>");
        for (Column c : columns) {
            builder.appendHtmlConstant("<th style=\"text-align: left; width: " + c.getEffectiveWidth() + "px;\">");
            builder.appendEscaped(c.name);
            builder.appendHtmlConstant("</th>");
        }
        builder.appendHtmlConstant("</tr>");
        builder.appendHtmlConstant("</thead>");

        builder.appendHtmlConstant("<tbody>");

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
                appendBreak(builder);

                buildingId = record.building().getValue();
                buildingEftTotal = new BigDecimal("0.00");
                buildingChargesTotal = new BigDecimal("0.00");
                buildingDifferenceTotal = new BigDecimal("0.00");

            }

            builder.appendHtmlConstant("<tr>");
            builder.appendHtmlConstant("<td style='width: " + columns.get(0).getEffectiveWidth() + "px;'>");
            builder.append(getFormattedNotice(record));
            builder.append(getFormattedNoticePrintable(record));
            builder.appendHtmlConstant("</td>");

            builder.appendHtmlConstant("<td style='width: " + columns.get(1).getEffectiveWidth() + "px;'>");
            builder.appendEscaped(record.building().getValue());
            builder.appendHtmlConstant("</td>");

            builder.appendHtmlConstant("<td style='width: " + columns.get(2).getEffectiveWidth() + "px;'>");
            builder.appendEscaped(record.unit().getValue());

            builder.appendHtmlConstant("</td>");

            builder.appendHtmlConstant("<td style='width: " + columns.get(3).getEffectiveWidth() + "px;'>");

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
                builder.appendHtmlConstant("<td style='width: " + columns.get(4).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.tenantName().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(5).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.paymentMethod().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(6).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.totalEft().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(7).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.charges().getStringView());
                builder.appendHtmlConstant("</td>");
                builder.appendHtmlConstant("<td style='width: " + columns.get(8).getEffectiveWidth() + "px;'>");
                builder.appendEscaped(details.difference().getStringView());
                builder.appendHtmlConstant("</td>");

                builder.appendHtmlConstant("</tr>");

            }
            // lease totals:
            appendBreak(builder);
            addLeaseTotals(builder, totalFormat, record.leaseTotals().totalEft().getValue(), record.leaseTotals().charges().getValue(), record.leaseTotals()
                    .difference().getValue());
            appendBreak(builder);

            buildingEftTotal = buildingEftTotal.add(record.leaseTotals().totalEft().getValue());
            buildingChargesTotal = record.leaseTotals().charges().isNull() ? buildingChargesTotal : buildingChargesTotal.add(record.leaseTotals().charges()
                    .getValue());
            buildingDifferenceTotal = record.leaseTotals().difference().isNull() ? buildingDifferenceTotal : buildingDifferenceTotal.add(record.leaseTotals()
                    .difference().getValue());
        }
        addBuildingTotals(builder, totalFormat, buildingId, buildingEftTotal, buildingChargesTotal, buildingDifferenceTotal);
        appendBreak(builder);

        builder.appendHtmlConstant("</table>");
        setHTML(builder.toSafeHtml());

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

    private SafeHtml getFormattedNotice(IEntity entity) {
        EftVarianceReportRecordDTO r = (EftVarianceReportRecordDTO) entity;
        if (!r.comments().isNull()) {
            String noticeIcon = CrmImages.INSTANCE.reportsInfo().getSafeUri().asString();
            return new SafeHtmlBuilder()
                    .appendHtmlConstant("<div style='text-align:center' class='" + AbstractReport.ReportPrintTheme.Styles.ReportNonPrintable.name() + "'>")
                    .appendHtmlConstant(
                            "<img title='" + SafeHtmlUtils.htmlEscape(r.comments().getValue()) + "'" + " src='" + noticeIcon + "'" + " border='0' "
                                    + " style='width:15px; height:15px;text-align:center'" + ">").appendHtmlConstant("</div>").toSafeHtml();
        } else {
            return new SafeHtmlBuilder().toSafeHtml();
        }
    }

    private SafeHtml getFormattedNoticePrintable(IEntity entity) {
        EftVarianceReportRecordDTO r = (EftVarianceReportRecordDTO) entity;
        SafeHtmlBuilder b = new SafeHtmlBuilder();
        if (CommonsStringUtils.isStringSet(r.comments().getValue())) {
            b.appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name() + "'>")
                    .appendEscaped(r.comments().getValue()).appendHtmlConstant("</span>");
        }
        return b.toSafeHtml();
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

    private final void appendBreak(SafeHtmlBuilder builder) {
        builder.appendHtmlConstant("<tr>");
        builder.appendHtmlConstant("<td colspan='8'>");
        builder.appendHtmlConstant("<div>&nbsp</div>");
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("</tr>");
    }

    private void addLeaseTotals(SafeHtmlBuilder builder, NumberFormat totalFormat, BigDecimal totalEft, BigDecimal totalCharges, BigDecimal totalDifference) {

        builder.appendHtmlConstant("<tr>");
        builder.appendHtmlConstant("<td colspan='4'></td>");
        builder.appendHtmlConstant("<td style='text-align:center;'>");
        builder.appendEscaped(i18n.tr("Total $ for lease:"));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(totalFormat.format(totalEft));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(totalFormat.format(totalCharges));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(totalFormat.format(totalDifference));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("</tr>");
    }

    private void addBuildingTotals(SafeHtmlBuilder builder, NumberFormat totalFormat, String buildingId, BigDecimal totalEft, BigDecimal totalCharges,
            BigDecimal totalDifference) {

        builder.appendHtmlConstant("<tr>");
        builder.appendHtmlConstant("<td colspan='4'></td>");
        builder.appendHtmlConstant("<td style='text-align:center;'>");
        builder.appendEscaped(i18n.tr("Total $ for Building {0}:", buildingId));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(totalFormat.format(totalEft));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(totalFormat.format(totalCharges));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(totalFormat.format(totalDifference));
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("</tr>");
    }

}
