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
                new ColumnDescriptorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.padBillingCycle().billingType()).build()),
                new ColumnDescriptorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.padBillingCycle().billingCycleStartDate()).build()),
                new ColumnDescriptorAnchorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().leaseId()).build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Tenants.Lease().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().lease().getPath())).getPrimaryKey()); }
                },
                new ColumnDescriptorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().expectedMoveOut()).build()),
                new ColumnDescriptorAnchorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().unit().building().propertyCode()).columnTitle("Building").build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Properties.Building().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().lease().unit().building().getPath())).getPrimaryKey()); }                    
                },
                new ColumnDescriptorAnchorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().lease().unit().info().number()).columnTitle("Unit").build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Properties.Unit().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().lease().unit().getPath())).getPrimaryKey()); }                    
                },
                new ColumnDescriptorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().participantId()).columnTitle("Participant Id").build()),
                new ColumnDescriptorAnchorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.preauthorizedPayment().tenant().customer()).build()) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Tenants.Tenant().formViewerPlace(((IEntity)entity.getMember(proto.preauthorizedPayment().tenant().getPath())).getPrimaryKey()); }
                },
                new ColumnDescriptorAnchorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.amount()).build(), true) {
                    @Override protected CrudAppPlace makePlace(IEntity entity) { return new CrmSiteMap.Finance.Payment().formViewerPlace(entity.getPrimaryKey()); }                    
                },
                new ColumnDescriptorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.paymentMethod().type()).build()),
                new ColumnDescriptorTableColumnFormatter(new MemberColumnDescriptor.Builder(proto.paymentStatus()).build())
        );//@formatter:on
    }

    private final HTML reportHtml;

    public EftReport() {
        reportHtml = new HTML();
        initWidget(reportHtml);
    }

    @Override
    public void setData(Object data) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div style=\"text-align: center; font-size: 22pt; line-height:22pt\">");
        builder.appendEscaped(i18n.tr("EFT Report"));
        builder.appendHtmlConstant("</div>");

        builder.appendHtmlConstant("<table style=\"white-space: nowrap; border-collapse: separate; border-spacing: 15pt;\">");
        builder.appendHtmlConstant("<tr>");

        for (ITableColumnFormatter formatter : COLUMN_DESCRIPTORS) {
            builder.appendHtmlConstant("<th style=\"text-align: left\">");
            builder.append(formatter.formatHeader());
            builder.appendHtmlConstant("</th>");
        }

        builder.appendHtmlConstant("</tr>");

        @SuppressWarnings("unchecked")
        Vector<PaymentRecord> paymentRecords = (Vector<PaymentRecord>) data;
        for (PaymentRecord paymentRecord : paymentRecords) {
            builder.appendHtmlConstant("<tr>");
            for (ITableColumnFormatter desc : COLUMN_DESCRIPTORS) {
                builder.appendHtmlConstant("<td>");
                builder.append(desc.formatContent(paymentRecord));
                builder.appendHtmlConstant("</td>");
            }
            builder.appendHtmlConstant("</tr>");
        }
        builder.appendHtmlConstant("</table>");

        reportHtml.setHTML(builder.toSafeHtml());
    }

}
