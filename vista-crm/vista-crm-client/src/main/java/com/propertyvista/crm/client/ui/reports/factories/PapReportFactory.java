/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.factories;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.ReportFactory;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.factories.pad.PadReportForm;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.reports.PapReportMetadata;

public class PapReportFactory implements ReportFactory<PapReportMetadata> {

    public static I18n i18n = I18n.get(PapReportFactory.class);

    private static List<ColumnDescriptor> COLUMN_DESCRIPTORS;
    static {
        PaymentRecord proto = EntityFactory.getEntityPrototype(PaymentRecord.class);

        COLUMN_DESCRIPTORS = Arrays.asList(//@formatter:off
                new MemberColumnDescriptor.Builder(proto.id()).build(),
                new MemberColumnDescriptor.Builder(proto.padBillingCycle().billingType()).build(),
                new MemberColumnDescriptor.Builder(proto.padBillingCycle().billingCycleStartDate()).build(),
                new MemberColumnDescriptor.Builder(proto.amount()).build(),               
                new MemberColumnDescriptor.Builder(proto.paymentMethod().type()).build(),
                new MemberColumnDescriptor.Builder(proto.paymentStatus()).build(),
                new MemberColumnDescriptor.Builder(proto.createdDate()).build(),
                new MemberColumnDescriptor.Builder(proto.receivedDate()).build(),
                new MemberColumnDescriptor.Builder(proto.lastStatusChangeDate()).build(),
                new MemberColumnDescriptor.Builder(proto.targetDate()).build()
                
        );//@formatter:on
    }

    @Override
    public CEntityForm<PapReportMetadata> getReportSettingsForm() {
        CEntityDecoratableForm<PapReportMetadata> form = new PadReportForm();
        form.initContent();
        return form;
    }

    @Override
    public Report getReport() {
        return new Report() {

            HTML reportHtml = new HTML();

            @Override
            public Widget asWidget() {
                return reportHtml;
            }

            @Override
            public void setData(Object data) {
                Vector<PaymentRecord> reportData = (Vector<PaymentRecord>) data;

                SafeHtmlBuilder bb = new SafeHtmlBuilder();
                bb.appendHtmlConstant("<div style=\"text-align: center; font-size: 22pt\">");
                bb.appendEscaped(i18n.tr("PAP Report"));
                bb.appendHtmlConstant("</div>");
                bb.appendHtmlConstant("<div>");
                bb.appendHtmlConstant("</div>");

                bb.appendHtmlConstant("<table style=\"white-space: nowrap; border-collapse: separate; border-spacing: 15pt;\">");
                bb.appendHtmlConstant("<tr>");

                for (ColumnDescriptor desc : COLUMN_DESCRIPTORS) {
                    bb.appendHtmlConstant("<th style=\"text-align: left\">");
                    bb.appendEscaped(desc.getColumnTitle());
                    bb.appendHtmlConstant("</th>");
                }

                bb.appendHtmlConstant("</tr>");

                for (PaymentRecord paymentRecord : reportData) {
                    bb.appendHtmlConstant("<tr>");
                    for (ColumnDescriptor desc : COLUMN_DESCRIPTORS) {
                        cell(bb, desc.convert(paymentRecord));
                    }
                    bb.appendHtmlConstant("</tr>");
                }
                bb.appendHtmlConstant("</table>");

                reportHtml.setHTML(bb.toSafeHtml());
            }

            private void cell(SafeHtmlBuilder bb, String data) {
                bb.appendHtmlConstant("<td>");
                bb.appendEscaped(data);
                bb.appendHtmlConstant("</td>");
            }
        };
    }

}
