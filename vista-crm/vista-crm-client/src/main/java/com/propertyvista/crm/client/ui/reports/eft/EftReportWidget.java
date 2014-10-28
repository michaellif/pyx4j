/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-27
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.eft;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.utils.EntityFormatUtils;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.report.AbstractReport;
import com.pyx4j.site.client.backoffice.ui.prime.report.IReportWidget;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.reports.ColumnDescriptorAnchorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.ColumnDescriptorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.ITableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.ScrollBarPositionMemento;
import com.propertyvista.crm.rpc.dto.reports.EftReportDataDTO;
import com.propertyvista.crm.rpc.dto.reports.EftReportRecordDTO;

public class EftReportWidget extends HTML implements IReportWidget {

    private final static I18n i18n = I18n.get(EftReportWidget.class);

    private ScrollBarPositionMemento tableBodyScrollBarPositionMemento;

    private ScrollBarPositionMemento reportScrollBarPositionMemento;

    private boolean isTableReady;

    public EftReportWidget() {

        isTableReady = false;
    }

    @Override
    public void setData(Object data) {
        setHTML("");
        isTableReady = false;
        if (data == null) {
            return;
        }

        final EftReportDataDTO eftReportData = (EftReportDataDTO) data;
        final List<EftReportRecordDTO> paymentRecords = eftReportData.eftReportRecords();

        if (paymentRecords.isEmpty()) {
            setHTML(NoResultsHtml.get());
            return;
        }

        final SafeHtmlBuilder builder = new SafeHtmlBuilder();
        final List<ITableColumnFormatter> columnDescriptors = initColumnDescriptors();

        int totalWidth = 0;
        for (ITableColumnFormatter formatter : columnDescriptors) {
            totalWidth += formatter.getWidth();
        }
        builder.appendHtmlConstant("<div style=\"position: relative; left:0px; width: " + totalWidth
                + "px; top: 0px; height: 30px; text-align: center; font-size: 22pt; line-height:22pt\">");
        builder.appendEscaped(i18n.tr("EFT Report"));
        builder.appendHtmlConstant("</div>");

        builder.appendHtmlConstant("<table style=\"display: inline-block; position: relative; left: 0px; width: " + totalWidth
                + "px; top: 31px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border=\"0\">");
        builder.appendHtmlConstant("<thead>");
        builder.appendHtmlConstant("<tr>");

        for (ITableColumnFormatter formatter : columnDescriptors) {
            builder.appendHtmlConstant("<th style=\"text-align: left; width: " + formatter.getWidth() + "px;\">");
            builder.append(formatter.formatHeader());
            builder.appendHtmlConstant("</th>");
        }
        builder.appendHtmlConstant("</thead>");

        builder.appendHtmlConstant("<tbody>");
        builder.appendHtmlConstant("</tr>");

        final NumberFormat currencyFormat = NumberFormat.getFormat(paymentRecords.get(0).amount().getMeta().getFormat());

        RepeatingCommand rc = new RepeatingCommand() {

            private final Iterator<EftReportRecordDTO> paymentRecordIterator = paymentRecords.iterator();

            private String currentPropertyCode = paymentRecords.get(0).building().getValue();

            private BigDecimal propertyCodeTotal = new BigDecimal("0.00");

            private BigDecimal overallTotal = new BigDecimal("0.00");

            private int progress = 0;

            @Override
            public boolean execute() {
                if (paymentRecordIterator.hasNext()) {
                    setHTML(new SafeHtmlBuilder().appendHtmlConstant("<div style='text-align: center;'>")
                            .appendEscaped(i18n.tr("Preparing report table (record {0} of {1})", progress, paymentRecords.size())).appendHtmlConstant("</div>")
                            .toSafeHtml());
                    int i = 0;
                    while (paymentRecordIterator.hasNext() & (i < 300)) {
                        ++i;
                        EftReportRecordDTO paymentRecord = paymentRecordIterator.next();
                        if (eftReportData.agregateByBuildings().getValue(false)) {
                            if (!currentPropertyCode.equals(paymentRecord.building().getValue())) {
                                appendBreak(builder);
                                appendRenderedTotalRow(builder, currencyFormat, i18n.tr("Total for Building {0} $", currentPropertyCode), propertyCodeTotal);
                                appendBreak(builder);

                                currentPropertyCode = paymentRecord.building().getValue();
                                propertyCodeTotal = new BigDecimal("0.00");
                            }
                            propertyCodeTotal = propertyCodeTotal.add(paymentRecord.amount().getValue());
                        }
                        overallTotal = overallTotal.add(paymentRecord.amount().getValue());

                        builder.appendHtmlConstant("<tr>");
                        for (ITableColumnFormatter desc : columnDescriptors) {
                            builder.appendHtmlConstant("<td style=\"width: " + desc.getWidth() + "px;\">");
                            builder.append(desc.formatContent(paymentRecord));
                            builder.appendHtmlConstant("</td>");
                        }
                        builder.appendHtmlConstant("</tr>");
                    }
                    progress += i;
                    return true;
                } else {
                    setHTML(new SafeHtmlBuilder().appendHtmlConstant("<div style='text-align: center;'>").appendEscaped("Finished...")
                            .appendHtmlConstant("</div>").toSafeHtml());

                    if (eftReportData.agregateByBuildings().getValue(false)) {
                        appendBreak(builder);
                        appendRenderedTotalRow(builder, currencyFormat, i18n.tr("Total for Building {0} $", currentPropertyCode), propertyCodeTotal);
                    }
                    appendBreak(builder);
                    appendRenderedTotalRow(builder, NumberFormat.getFormat("#,##0"), i18n.tr("Total # of Payment Records:"),
                            new BigDecimal(paymentRecords.size()));
                    appendRenderedTotalRow(builder, currencyFormat, i18n.tr("Total $"), overallTotal);

                    builder.appendHtmlConstant("</tbody>");
                    builder.appendHtmlConstant("</table>");

                    setReportTable(builder.toSafeHtml().asString(), null);
                    return false;
                }
            }
        };
        Scheduler.get().scheduleIncremental(rc);

    }

    @Override
    public Object getMemento() {
        if (isTableReady) {
            return new Object[] { getHTML(), new ScrollBarPositionMemento[] { reportScrollBarPositionMemento, tableBodyScrollBarPositionMemento } };
        } else {
            return null;
        }
    }

    @Override
    public void setMemento(final Object memento) {
        isTableReady = false;
        if (memento != null) {
            String html = (String) (((Object[]) memento)[0]);
            setReportTable(html, new Command() {
                @Override
                public void execute() {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            final Element tableBody = getElement().getElementsByTagName("tbody").getItem(0);
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
            });
        } else {
            setData(null);
        }
    }

    private void setReportTable(String safeHtmlReportTable, final Command onSetComplete) {
        setHTML(safeHtmlReportTable);
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

                if (onSetComplete != null) {
                    onSetComplete.execute();
                }
                isTableReady = true;
            }
        });

    }

    private final static List<ITableColumnFormatter> initColumnDescriptors() {
        final EftReportRecordDTO proto = EntityFactory.getEntityPrototype(EftReportRecordDTO.class);
        final int wideColumnWidth = Window.getClientWidth() >= 1200 ? 200 : 150;
        final int shortColumnWidth = Window.getClientWidth() >= 1200 ? 100 : 80;

        ITableColumnFormatter noticeTooltipColumnFormatter = new ITableColumnFormatter() {

            @Override
            public int getWidth() {
                return 50;
            }

            @Override
            public SafeHtml formatHeader() {
                return new SafeHtmlBuilder() //
                        .appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportNonPrintable.name() + "'>") //
                        .appendEscaped(i18n.tr("Notice")) //
                        .appendHtmlConstant("</span>") //
                        .toSafeHtml();
            }

            @Override
            public SafeHtml formatContent(IEntity entity) {
                EftReportRecordDTO r = (EftReportRecordDTO) entity;
                if (!r.notice().isNull() || !r.comments().isNull()) {
                    String noticeIcon = CrmImages.INSTANCE.reportsInfo().getSafeUri().asString();
                    if (!r.notice().isNull()) {
                        noticeIcon = CrmImages.INSTANCE.noticeWarning().getSafeUri().asString();
                    }
                    String textValue = EntityFormatUtils.nvl_concat(" ", r.notice(), r.comments());
                    return new SafeHtmlBuilder()
                            .appendHtmlConstant(
                                    "<div style='text-align:center' class='" + AbstractReport.ReportPrintTheme.Styles.ReportNonPrintable.name() + "'>")
                            .appendHtmlConstant("<img title='" + SafeHtmlUtils.htmlEscape(textValue) + "'" //
                                    + " src='" + noticeIcon + "'" //
                                    + " border='0' " //
                                    + " style='width:15px; height:15px;text-align:center'" //
                                    + ">") //
                            .appendHtmlConstant("</div>").toSafeHtml();
                } else {
                    return new SafeHtmlBuilder().toSafeHtml();
                }
            }
        };

        ITableColumnFormatter noticeForPrintColumnFormatter = new ITableColumnFormatter() {

            @Override
            public int getWidth() {
                return 50;
            }

            @Override
            public SafeHtml formatHeader() {
                return new SafeHtmlBuilder() //
                        .appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name() + "'>") //
                        .appendEscaped(i18n.tr("Notice")) //
                        .appendHtmlConstant("</span>") //
                        .toSafeHtml();
            }

            @Override
            public SafeHtml formatContent(IEntity entity) {
                EftReportRecordDTO r = (EftReportRecordDTO) entity;
                SafeHtmlBuilder b = new SafeHtmlBuilder();
                if (!r.notice().isNull() || !r.comments().isNull()) {
                    b.appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name() + "'>")
                            .appendEscaped(EntityFormatUtils.nvl_concat(" ", r.notice(), r.comments())).appendHtmlConstant("</span>");
                }
                return b.toSafeHtml();
            }
        };

        List<ITableColumnFormatter> columnDescriptors = Arrays.<ITableColumnFormatter> asList(//@formatter:off
                    noticeTooltipColumnFormatter,
                    noticeForPrintColumnFormatter,
                    new ColumnDescriptorTableColumnFormatter(wideColumnWidth, new MemberColumnDescriptor.Builder(proto.billingCycleStartDate()).build()),
                    new ColumnDescriptorAnchorTableColumnFormatter(shortColumnWidth, new MemberColumnDescriptor.Builder(proto.leaseId()).build()),
                    new ColumnDescriptorTableColumnFormatter(wideColumnWidth, new MemberColumnDescriptor.Builder(proto.expectedMoveOut()).build()),
                    new ColumnDescriptorAnchorTableColumnFormatter(shortColumnWidth, new MemberColumnDescriptor.Builder(proto.building()).build()),
                    new ColumnDescriptorAnchorTableColumnFormatter(shortColumnWidth, new MemberColumnDescriptor.Builder(proto.unit()).build()),
                    new ColumnDescriptorTableColumnFormatter(shortColumnWidth, new MemberColumnDescriptor.Builder(proto.participantId()).build()),
                    new ColumnDescriptorAnchorTableColumnFormatter(wideColumnWidth, new MemberColumnDescriptor.Builder(proto.customer()).build()),
                    new ColumnDescriptorTableColumnFormatter(wideColumnWidth, new MemberColumnDescriptor.Builder(proto.paymentType()).build()),
                    new ColumnDescriptorTableColumnFormatter(wideColumnWidth, new MemberColumnDescriptor.Builder(proto.paymentStatus()).build())
        );//@formatter:on
        return columnDescriptors;
    }

    private final void appendBreak(SafeHtmlBuilder builder) {
        builder.appendHtmlConstant("<tr>");
        builder.appendHtmlConstant("<td colspan='12'>");
        builder.appendHtmlConstant("<div>&nbsp</div>");
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("</tr>");
    }

    private final void appendRenderedTotalRow(SafeHtmlBuilder builder, NumberFormat totalFormat, String totalLineDescription, BigDecimal total) {
        builder.appendHtmlConstant("<tr>");
        builder.appendHtmlConstant("<td colspan='9' style='text-align:right'>");
        builder.appendHtmlConstant("<b>");
        builder.appendEscaped(totalLineDescription);
        builder.appendHtmlConstant("&nbsp");
        builder.appendHtmlConstant("</b>");
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("<td style='text-align:left'>");
        builder.appendHtmlConstant("<b>");
        builder.appendEscaped(totalFormat.format(total));
        builder.appendHtmlConstant("</b>");
        builder.appendHtmlConstant("</td>");
        builder.appendHtmlConstant("</tr>");
    }
}
