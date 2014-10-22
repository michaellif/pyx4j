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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.report.AbstractReport;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReportWidget;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.reports.Column;
import com.propertyvista.crm.client.ui.reports.ColumnGroup;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReportWidget extends HTML implements IReportWidget {

    private static final I18n i18n = I18n.get(AutoPayChangesReportWidget.class);

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    public AutoPayChangesReportWidget() {

    }

    @Override
    public void setData(Object data) {
        setHTML("");
        if (data == null) {
            return;
        }

        Vector<AutoPayReviewLeaseDTO> autoPayReviews = (Vector<AutoPayReviewLeaseDTO>) data;
        if (autoPayReviews.isEmpty()) {
            setHTML(NoResultsHtml.get());
            return;
        }

        ColumnGroup autoPaySuspended = new ColumnGroup(i18n.tr("Auto Pay - Previous"), null);
        ColumnGroup autoPaySuggested = new ColumnGroup(i18n.tr("Auto Pay - Current"), null);

        int[] VERY_SHORT_COLUMN_WIDTHS = new int[] { 1200, 80, 60 };
        int[] SHORT_COLUMN_WIDTHS = new int[] { 1200, 100, 80 };
        int[] LONG_COLUMN_WIDTHS = new int[] { 1200, 150, 100 };
        List<Column> columns = Arrays.asList(//@formatter:off
                        new Column(i18n.tr("Notice"), null, VERY_SHORT_COLUMN_WIDTHS),
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
        builder.appendHtmlConstant("<table style=\"display: block; position: relative; left:0px; width: " + tableWidth
                + "px; top: 0px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border='1'>");

        builder.appendHtmlConstant("<thead>");

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
        builder.appendHtmlConstant("<tbody>");
        for (AutoPayReviewLeaseDTO reviewCase : autoPayReviews) {
            int numOfCaseRows = caseRows(reviewCase);
            boolean isFirstLine = true;
            builder.appendHtmlConstant("<tr>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(0).getEffectiveWidth() + "px;'>");
            builder.append(getFormattedNotice(reviewCase));
            builder.append(getFormattedNoticePrintable(reviewCase));
            builder.appendHtmlConstant("</td>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(1).getEffectiveWidth() + "px;'>"
                    + SafeHtmlUtils.htmlEscape(reviewCase.building().getValue()) + "</td>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(2).getEffectiveWidth() + "px;'>"
                    + SafeHtmlUtils.htmlEscape(reviewCase.unit().getValue()) + "</td>");

            String leaseUrl = AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false,
                    new CrmSiteMap.Tenants.Lease().formViewerPlace(reviewCase.lease().getPrimaryKey()));
            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(3).getEffectiveWidth() + "px;'>" + "<a href='"
                    + leaseUrl + "'>" + SafeHtmlUtils.htmlEscape(reviewCase.leaseId().getValue()) + "</a>" + "</td>");

            builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "' style='width: " + columns.get(4).getEffectiveWidth() + "px;'>"
                    + reviewCase.lease().expectedMoveOut().getStringView() + "</td>");

            for (AutoPayReviewPreauthorizedPaymentDTO reviewPap : reviewCase.pap()) {
                int numOfTenantRows = reviewPap.items().size();
                if (!isFirstLine) {
                    builder.appendHtmlConstant("<tr>");
                }
                builder.appendHtmlConstant("<td rowspan='" + numOfTenantRows + "' style='width: " + columns.get(5).getEffectiveWidth() + "px;'>"
                        + SafeHtmlUtils.htmlEscape(reviewPap.tenantName().getValue()) + "</td>");
                boolean isFirstCharge = true;
                for (AutoPayReviewChargeDTO charge : reviewPap.items()) {
                    if (!isFirstCharge) {
                        builder.appendHtmlConstant("<tr>");
                    } else {
                        isFirstCharge = false;
                    }
                    builder.appendHtmlConstant("<td style='width: " + columns.get(6).getEffectiveWidth() + "px;'>"
                            + SafeHtmlUtils.htmlEscape(charge.leaseCharge().getStringView()) + "</td>");
                    builder.appendHtmlConstant("<td style='width: " + columns.get(7).getEffectiveWidth() + "px;'>"
                            + charge.previous().totalPrice().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td style='width: " + columns.get(8).getEffectiveWidth() + "px;'>"
                            + charge.previous().payment().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td style='width: " + columns.get(9).getEffectiveWidth() + "px;'>"
                            + charge.previous().percent().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td style='width: " + columns.get(10).getEffectiveWidth() + "px;'>"
                            + charge.current().totalPrice().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td style='width: " + columns.get(12).getEffectiveWidth() + "px;'>"
                            + charge.current().payment().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td style='width: " + columns.get(13).getEffectiveWidth() + "px;'>"
                            + charge.current().percent().getStringView() + "</td>");
                    String percentChange = SafeHtmlUtils.htmlEscape(charge.current().billableItem().isNull() ? i18n.tr("Removed") : charge.current()
                            .percentChange().isNull() ? i18n.tr("New") : charge.current().percentChange().getStringView());
                    builder.appendHtmlConstant("<td style='width: " + columns.get(11).getEffectiveWidth() + "px;'>" + percentChange + "</td>");
                    if (isFirstLine) {
                        builder.appendHtmlConstant("<td rowspan='" + (numOfCaseRows + 1) + "' style='width: " + columns.get(14).getEffectiveWidth() + "px;'>"
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
            builder.appendHtmlConstant("<th colspan='6' style='text-align:right;'>" + i18n.tr("Total for lease:") + "</th>");
            builder.appendHtmlConstant("<td>" + reviewCase.totalPrevious().totalPrice().getStringView() + "</td>");
            builder.appendHtmlConstant("<td>" + reviewCase.totalPrevious().payment().getStringView() + "</td>"); // payment
            builder.appendHtmlConstant("<td>" + reviewCase.totalPrevious().percent().getStringView() + "</td>");
            builder.appendHtmlConstant("<td>" + reviewCase.totalCurrent().totalPrice().getStringView() + "</td>"); // totalPrice
            builder.appendHtmlConstant("<td>" + reviewCase.totalCurrent().payment().getStringView() + "</td>"); // payment
            builder.appendHtmlConstant("<td>" + reviewCase.totalCurrent().percent().getStringView() + "</td>"); // %
            builder.appendHtmlConstant("<td>" + "</td>"); // percent change
            builder.appendHtmlConstant("</tr>");
        }
        builder.appendHtmlConstant("</tbody>");
        builder.appendHtmlConstant("</table>");
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

    private int caseRows(AutoPayReviewLeaseDTO reviewCase) {
        int rows = 0;
        for (AutoPayReviewPreauthorizedPaymentDTO pap : reviewCase.pap()) {
            rows += pap.items().size();
        }
        return rows;
    }

    private SafeHtml getFormattedNotice(IEntity entity) {
        AutoPayReviewLeaseDTO r = (AutoPayReviewLeaseDTO) entity;
        if (!r.notice().isNull() || !r.comments().isNull()) {
            String noticeIcon = CrmImages.INSTANCE.reportsInfo().getSafeUri().asString();
            if (!r.notice().isNull()) {
                noticeIcon = CrmImages.INSTANCE.noticeWarning().getSafeUri().asString();
            }
            String textValue = EntityFromatUtils.nvl_concat(" ", r.notice(), r.comments());
            return new SafeHtmlBuilder()
                    .appendHtmlConstant("<div style='text-align:center' class='" + AbstractReport.ReportPrintTheme.Styles.ReportNonPrintable.name() + "'>")
                    .appendHtmlConstant(
                            "<img title='" + SafeHtmlUtils.htmlEscape(textValue) + "'" + " src='" + noticeIcon + "'" + " border='0' "
                                    + " style='width:15px; height:15px;text-align:center'" + ">").appendHtmlConstant("</div>").toSafeHtml();
        } else {
            return new SafeHtmlBuilder().toSafeHtml();
        }
    }

    private SafeHtml getFormattedNoticePrintable(IEntity entity) {
        AutoPayReviewLeaseDTO r = (AutoPayReviewLeaseDTO) entity;
        SafeHtmlBuilder b = new SafeHtmlBuilder();
        if (CommonsStringUtils.isStringSet(r.notice().getValue())) {
            b.appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name() + "'>")
                    .appendEscaped(r.notice().getValue()).appendHtmlConstant("</span>");
        }
        return b.toSafeHtml();
    }

}
