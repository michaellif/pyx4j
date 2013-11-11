/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopay;

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
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.ReportWidget;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.client.ui.reports.Column;
import com.propertyvista.crm.client.ui.reports.ColumnGroup;
import com.propertyvista.crm.client.ui.reports.CommonReportStyles;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReportWidget implements ReportWidget {

    private static final I18n i18n = I18n.get(AutoPayChangesReportWidget.class);

    private final HTML reportHtml;

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    public AutoPayChangesReportWidget() {
        reportHtml = new HTML();
        reportHtml.getElement().getStyle().setPosition(Position.ABSOLUTE);
        reportHtml.getElement().getStyle().setLeft(0, Unit.PX);
        reportHtml.getElement().getStyle().setRight(0, Unit.PX);
        reportHtml.getElement().getStyle().setTop(0, Unit.PX);
        reportHtml.getElement().getStyle().setBottom(0, Unit.PX);

        reportHtml.getElement().getStyle().setOverflowX(Overflow.SCROLL);
        reportHtml.getElement().getStyle().setOverflowY(Overflow.AUTO);
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

        Vector<AutoPayReviewLeaseDTO> autoPayReviews = (Vector<AutoPayReviewLeaseDTO>) data;
        if (autoPayReviews.isEmpty()) {
            reportHtml.setHTML(NoResultsHtml.get());
            onWidgetReady.execute();
            return;
        }

        ColumnGroup autoPaySuspended = new ColumnGroup(i18n.tr("Auto Pay - Previous"), null);
        ColumnGroup autoPaySuggested = new ColumnGroup(i18n.tr("Auto Pay - Current"), null);

        int[] VERY_SHORT_COLUMN_WIDTHS = new int[] { 1200, 80, 60 };
        int[] SHORT_COLUMN_WIDTHS = new int[] { 1200, 100, 80 };
        int[] LONG_COLUMN_WIDTHS = new int[] { 1200, 150, 100 };
        List<Column> columns = Arrays.asList(//@formatter:off
                        new Column(i18n.tr("Building"), null, VERY_SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Unit"), null, VERY_SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Lease ID"), null, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Expected Move Out"), null, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Tenant Name"), null, LONG_COLUMN_WIDTHS),
                        new Column(i18n.tr("Charge Code"), null, LONG_COLUMN_WIDTHS),

                        new Column(i18n.tr("Total Price"), autoPaySuspended, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Payment"), autoPaySuspended, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("% of Total"),autoPaySuspended, VERY_SHORT_COLUMN_WIDTHS),

                        new Column(i18n.tr("Total Price"), autoPaySuggested, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Payment"), autoPaySuggested, SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("% of Total"), autoPaySuggested, VERY_SHORT_COLUMN_WIDTHS),
                        new Column(i18n.tr("Change"), autoPaySuggested, VERY_SHORT_COLUMN_WIDTHS),

                        new Column(i18n.tr("PaymentDue"), null, SHORT_COLUMN_WIDTHS)
                );//@formatter:on

        int tableWidth = 0;
        for (Column c : columns) {
            tableWidth += c.getEffectiveWidth();
        }
        // header
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<table style=\"display: block; position: absolute; left:0px; width: " + tableWidth
                + "px; top: 0px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border='1'>");

        builder.appendHtmlConstant("<thead class=\"" + CommonReportStyles.RReportTableFixedHeader.name() + "\">");

        int maxRowSpan = 1;
        for (Column c : columns) {
            if ((c.level + 1) > maxRowSpan) {
                maxRowSpan = c.level + 1;
            }
        }
        for (int level = 0; level < maxRowSpan; ++level) {
            builder.appendHtmlConstant("<tr>");

            ColumnGroup prevGroup = null;

            for (Column c : columns) {
                if (c.level == level) {
                    prevGroup = null;

                    builder.appendHtmlConstant("<th rowspan='" + (maxRowSpan - level) + "' style='width: " + c.getEffectiveWidth() + "px;'>");
                    builder.appendEscaped(c.name);
                    builder.appendHtmlConstant("</th>");
                } else if (c.level < level) {
                    continue;
                } else {
                    ColumnGroup curGroup = c.parentGroup;
                    while (curGroup.level != level) {
                        curGroup = curGroup.parentGroup;
                    }
                    if (curGroup != prevGroup) {
                        // TODO implement rowspan calculation
                        builder.appendHtmlConstant("<th rowspan='" + 1 + "' colspan='" + curGroup.childCount + "'>");
                        builder.appendHtmlConstant(SafeHtmlUtils.htmlEscape(curGroup.groupTitle));
                        builder.appendHtmlConstant("</th>");
                        prevGroup = curGroup;
                    }

                }
            }
            builder.appendHtmlConstant("</tr>");
        }
        builder.appendHtmlConstant("</thead>");

        // rows
        builder.appendHtmlConstant("<tbody class=\"" + CommonReportStyles.RReportTableScrollableBody.name() + "\">");
        for (AutoPayReviewLeaseDTO reviewCase : autoPayReviews) {
            int numOfCaseRows = caseRows(reviewCase);
            boolean isFirstLine = true;
            builder.appendHtmlConstant("<tr>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(0).getEffectiveWidth() + "px;'>"
                    + SafeHtmlUtils.htmlEscape(reviewCase.building().getValue()) + "</td>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(1).getEffectiveWidth() + "px;'>"
                    + SafeHtmlUtils.htmlEscape(reviewCase.unit().getValue()) + "</td>");

            String leaseUrl = AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false,
                    new CrmSiteMap.Tenants.Lease().formViewerPlace(reviewCase.lease().getPrimaryKey()));
            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(2).getEffectiveWidth() + "px;'>" + "<a href='"
                    + leaseUrl + "'>" + SafeHtmlUtils.htmlEscape(reviewCase.leaseId().getValue()) + "</a>" + "</td>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(3).getEffectiveWidth() + "px;'>"
                    + reviewCase.lease().expectedMoveOut().getStringView() + "</td>");

            for (AutoPayReviewPreauthorizedPaymentDTO reviewPap : reviewCase.pap()) {
                int numOfTenantRows = reviewPap.items().size();
                if (!isFirstLine) {
                    builder.appendHtmlConstant("<tr>");
                }
                builder.appendHtmlConstant("<td rowspan='" + numOfTenantRows + "' style='width: " + columns.get(4).getEffectiveWidth() + "px;'>"
                        + SafeHtmlUtils.htmlEscape(reviewPap.tenantName().getValue()) + "</td>");
                boolean isFirstCharge = true;
                for (AutoPayReviewChargeDTO charge : reviewPap.items()) {
                    if (!isFirstCharge) {
                        builder.appendHtmlConstant("<tr>");
                    } else {
                        isFirstCharge = false;
                    }
                    builder.appendHtmlConstant("<td style='width: " + columns.get(5).getEffectiveWidth() + "px;'>"
                            + SafeHtmlUtils.htmlEscape(charge.leaseCharge().getStringView()) + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(6).getEffectiveWidth()
                            + "px;'>" + charge.previous().totalPrice().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(7).getEffectiveWidth()
                            + "px;'>" + charge.previous().payment().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(8).getEffectiveWidth()
                            + "px;'>" + charge.previous().percent().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(9).getEffectiveWidth()
                            + "px;'>" + charge.current().totalPrice().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(11).getEffectiveWidth()
                            + "px;'>" + charge.current().payment().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(12).getEffectiveWidth()
                            + "px;'>" + charge.current().percent().getStringView() + "</td>");
                    String percentChange = SafeHtmlUtils.htmlEscape(charge.current().billableItem().isNull() ? i18n.tr("Removed") : charge.current()
                            .percentChange().isNull() ? i18n.tr("New") : charge.current().percentChange().getStringView());
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "' style='width: " + columns.get(10).getEffectiveWidth()
                            + "px;'>" + percentChange + "</td>");
                    if (isFirstLine) {
                        builder.appendHtmlConstant("<td rowspan='" + (numOfCaseRows + 1) + "' style='width: " + columns.get(13).getEffectiveWidth() + "px;'>"
                                + reviewCase.paymentDue().getStringView() + "</td>");
                    }
                    builder.appendHtmlConstant("</tr>");

                    if (isFirstLine) {
                        isFirstLine = false;
                    }
                }

            }

            // add summary for lease
            builder.appendHtmlConstant("<tr>");
            builder.appendHtmlConstant("<th colspan='6' style='text-align:right;' class='" + CommonReportStyles.RRowTotal.name() + "'>"
                    + i18n.tr("Total for lease:") + "</th>");
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                    + reviewCase.totalPrevious().totalPrice().getStringView() + "</td>");
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                    + reviewCase.totalPrevious().payment().getStringView() + "</td>"); // payment 
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                    + reviewCase.totalPrevious().percent().getStringView() + "</td>");
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                    + reviewCase.totalCurrent().totalPrice().getStringView() + "</td>"); // totalPrice
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>" + "</td>"); // percent change
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                    + reviewCase.totalCurrent().payment().getStringView() + "</td>"); // payment
            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                    + reviewCase.totalCurrent().percent().getStringView() + "</td>"); // %                    
            builder.appendHtmlConstant("</tr>");
        }
        builder.appendHtmlConstant("</tbody>");
        builder.appendHtmlConstant("</table>");
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

    private int caseRows(AutoPayReviewLeaseDTO reviewCase) {
        int rows = 0;
        for (AutoPayReviewPreauthorizedPaymentDTO pap : reviewCase.pap()) {
            rows += pap.items().size();
        }
        return rows;
    }

}
