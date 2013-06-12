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
package com.propertyvista.crm.client.ui.reports.factories.autopay;

import java.util.Vector;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.ReportFactory;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.dto.payment.AutoPayReviewDTO;

public class AutoPayChangesReportFactory implements ReportFactory<AutoPayChangesReportMetadata> {

    private static final I18n i18n = I18n.get(AutoPayChangesReportFactory.class);

    @Override
    public CEntityForm<AutoPayChangesReportMetadata> getReportSettingsForm() {
        CEntityDecoratableForm<AutoPayChangesReportMetadata> form = new CEntityDecoratableForm<AutoPayChangesReportMetadata>(AutoPayChangesReportMetadata.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel panel = new FormFlexPanel();
                return panel;
            }
        };
        form.initContent();
        return form;
    }

    @Override
    public Report getReport() {
        return new Report() {

            private final HTML reportHtml = new HTML();

            @Override
            public Widget asWidget() {
                return reportHtml;
            }

            @Override
            public void setData(Object data) {
                Vector<AutoPayReviewDTO> autoPayReviews = (Vector<AutoPayReviewDTO>) data;

                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant("<table style=\"width: 100%; white-space: nowrap; border-collapse: separate; border-spacing: 1px;\" border='1'>");
                builder.appendHtmlConstant("<tr>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Case"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Building"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Unit"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Lease ID"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Tenant Name"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Charge Code"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1' colspan='3'>");
                builder.appendEscaped(i18n.tr("Auto Pay Suspended"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1' colspan='3'>");
                builder.appendEscaped(i18n.tr("Auto Pay Suggested"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Payment Due"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("</tr>");
                builder.appendHtmlConstant("<tr>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("Total Price"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("Payment"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("% of Total"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("Total Price"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("Payment"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("% of Total"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("</tr>");

                builder.appendHtmlConstant("</table>");
                reportHtml.setHTML(builder.toSafeHtml());
            }

        };
    }
}
