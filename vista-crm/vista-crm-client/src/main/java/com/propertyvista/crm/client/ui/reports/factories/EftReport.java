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
package com.propertyvista.crm.client.ui.reports.factories;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.AbstractReport;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.ui.reports.components.ColumnDescriptorAnchorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.components.ColumnDescriptorTableColumnFormatter;
import com.propertyvista.crm.client.ui.reports.components.CommonReportStyles;
import com.propertyvista.crm.client.ui.reports.components.ITableColumnFormatter;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.PaymentRecord;

public class EftReport extends Composite implements Report {

    private final static I18n i18n = I18n.get(EftReportFactory.class);

    private final static List<ITableColumnFormatter> COLUMN_DESCRIPTORS;
    static {
        // TODO use AppPlaceEntityMapper to resolve places (now its' not optimized and works really slow)        
        final PaymentRecord proto = EntityFactory.getEntityPrototype(PaymentRecord.class);
        COLUMN_DESCRIPTORS = Arrays.<ITableColumnFormatter> asList(//@formatter:off
                new ITableColumnFormatter() {
                    
                    @Override
                    public int getWidth() {
                        return 50;
                    }
                    
                    @Override
                    public SafeHtml formatHeader() {
                        return new SafeHtmlBuilder()
                            .appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportNonPrintable.name() + "'>")
                            .appendEscaped(i18n.tr("Notice"))
                            .appendHtmlConstant("</span>")
                            .toSafeHtml();
                    }
                    
                    @Override
                    public SafeHtml formatContent(IEntity entity) {                        
                        PaymentRecord r = (PaymentRecord) entity;                        
                        if (CommonsStringUtils.isStringSet(r.notice().getValue())) {                                                       
                            return new SafeHtmlBuilder()
                                    .appendHtmlConstant("<div style='text-align:center' class='" + AbstractReport.ReportPrintTheme.Styles.ReportNonPrintable.name() + "'>")
                                    .appendHtmlConstant("<img title='" + SafeHtmlUtils.htmlEscape(r.notice().getValue()) + "'" + 
                                             " src='" + CrmImages.INSTANCE.noticeWarning().getSafeUri().asString() + "'" + 
                                             " border='0' " +
                                             " style='width:15px; height:15px;text-al'" + 
                                             ">")
                                    .appendHtmlConstant("</div>")
                                    .toSafeHtml();
                        } else {
                            return new SafeHtmlBuilder().toSafeHtml();
                        }
                    }
                },
                new ITableColumnFormatter() {
                    
                    @Override
                    public int getWidth() {                    
                        return 50;
                    }
                    
                    @Override
                    public SafeHtml formatHeader() {                        
                        return new SafeHtmlBuilder()
                                .appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name() + "'>")
                                .appendEscaped(i18n.tr("Notice"))
                                .appendHtmlConstant("</span>")
                                .toSafeHtml();
                    }
                    
                    @Override
                    public SafeHtml formatContent(IEntity entity) {
                        PaymentRecord r = (PaymentRecord) entity;
                        SafeHtmlBuilder b = new SafeHtmlBuilder();
                        if (CommonsStringUtils.isStringSet(r.notice().getValue())) {                            
                            b.appendHtmlConstant("<span class='" + AbstractReport.ReportPrintTheme.Styles.ReportPrintableOnly.name() + "'>")
                             .appendEscaped(r.notice().getValue())
                             .appendHtmlConstant("</span>");
                        }
                        return b.toSafeHtml();                            
                    }
                },
                
                new ColumnDescriptorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.padBillingCycle().billingType()).build()),
                new ColumnDescriptorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.padBillingCycle().billingCycleStartDate()).build()),
                new ColumnDescriptorAnchorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().leaseId()).build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Tenants.Lease().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().lease().getPath())).getPrimaryKey()); }
                },
                new ColumnDescriptorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().expectedMoveOut()).build()),
                new ColumnDescriptorAnchorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().unit().building().propertyCode()).columnTitle("Building").build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Properties.Building().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().lease().unit().building().getPath())).getPrimaryKey()); }                    
                },
                new ColumnDescriptorAnchorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().unit().info().number()).columnTitle("Unit").build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Properties.Unit().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().lease().unit().getPath())).getPrimaryKey()); }                    
                },
                new ColumnDescriptorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().participantId()).columnTitle("Participant Id").build()),
                new ColumnDescriptorAnchorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().customer()).build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Tenants.Tenant().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().getPath())).getPrimaryKey()); }
                },
                new ColumnDescriptorAnchorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.amount()).build(), true) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Finance.Payment().formViewerPlace(entity.getPrimaryKey()); }                    
                },
                new ColumnDescriptorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.paymentMethod().type()).build()),
                new ColumnDescriptorTableColumnFormatter(150, new MemberColumnDescriptor.Builder(proto.paymentStatus()).build())
        );//@formatter:on
    }

    private final HTML reportHtml;

    public EftReport() {
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
    public void setData(Object data) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        int totalWidth = 0;
        for (ITableColumnFormatter formatter : COLUMN_DESCRIPTORS) {
            totalWidth += formatter.getWidth();
        }
        builder.appendHtmlConstant("<div style=\"position: absolute; left:0px; width: " + totalWidth
                + "px; top: 0px; height: 30px; text-align: center; font-size: 22pt; line-height:22pt\">");
        builder.appendEscaped(i18n.tr("EFT Report"));
        builder.appendHtmlConstant("</div>");

        builder.appendHtmlConstant("<table style=\"display: inline-block; position: absolute; left: 0px; width: " + totalWidth
                + "px; top: 31px; bottom: 0px; border-collapse: separate; border-spacing: 0px;\" border=\"0\">");
        builder.appendHtmlConstant("<thead class=\"" + CommonReportStyles.RReportTableFixedHeader.name() + "\">");
        builder.appendHtmlConstant("<tr>");

        for (ITableColumnFormatter formatter : COLUMN_DESCRIPTORS) {
            builder.appendHtmlConstant("<th style=\"text-align: left; width: " + formatter.getWidth() + "px;\">");
            builder.append(formatter.formatHeader());
            builder.appendHtmlConstant("</th>");
        }
        builder.appendHtmlConstant("</thead>");

        builder.appendHtmlConstant("<tbody class=\"" + CommonReportStyles.RReportTableScrollableBody.name() + "\">");
        builder.appendHtmlConstant("</tr>");

        @SuppressWarnings("unchecked")
        Vector<PaymentRecord> paymentRecords = (Vector<PaymentRecord>) data;
        for (PaymentRecord paymentRecord : paymentRecords) {
            builder.appendHtmlConstant("<tr>");
            for (ITableColumnFormatter desc : COLUMN_DESCRIPTORS) {
                builder.appendHtmlConstant("<td style=\"width: " + desc.getWidth() + "px;\">");
                builder.append(desc.formatContent(paymentRecord));
                builder.appendHtmlConstant("</td>");
            }
            builder.appendHtmlConstant("</tr>");
        }
        builder.appendHtmlConstant("</tbody>");
        builder.appendHtmlConstant("</table>");

        reportHtml.setHTML(builder.toSafeHtml());

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Element tableHead = reportHtml.getElement().getElementsByTagName("thead").getItem(0);
                int tableHeadHeight = tableHead.getClientHeight();

                Element tableBody = reportHtml.getElement().getElementsByTagName("tbody").getItem(0);
                tableBody.getStyle().setTop(tableHeadHeight + 1, Unit.PX);
            }
        });
    }

}
