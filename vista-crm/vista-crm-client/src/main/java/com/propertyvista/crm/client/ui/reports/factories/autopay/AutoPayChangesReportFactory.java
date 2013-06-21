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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.ReportFactory;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.components.CommonReportStyles;
import com.propertyvista.crm.client.ui.reports.factories.eft.SelectedBuildingsFolder;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.reports.AutoPayChangesReportMetadata;
import com.propertyvista.dto.payment.AutoPayReviewChargeDTO;
import com.propertyvista.dto.payment.AutoPayReviewDTO;
import com.propertyvista.dto.payment.AutoPayReviewPreauthorizedPaymentDTO;

public class AutoPayChangesReportFactory implements ReportFactory<AutoPayChangesReportMetadata> {

    private static final I18n i18n = I18n.get(AutoPayChangesReportFactory.class);

    @Override
    public CEntityForm<AutoPayChangesReportMetadata> getReportSettingsForm() {
        CEntityDecoratableForm<AutoPayChangesReportMetadata> form = new CEntityDecoratableForm<AutoPayChangesReportMetadata>(AutoPayChangesReportMetadata.class) {
            @Override
            public IsWidget createContent() {
                FormFlexPanel panel = new FormFlexPanel();
                int row = -1;
                panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leasesOnNoticeOnly())).build());
                panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().filterByBuildings())).build());
                get(proto().filterByBuildings()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        get(proto().buildings()).setVisible(event.getValue());
                    }
                });
                panel.setWidget(++row, 0, inject(proto().buildings(), new SelectedBuildingsFolder()));

                panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().filterByExpectedMoveOut())).build());
                get(proto().filterByExpectedMoveOut()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        get(proto().minimum()).setVisible(event.getValue() == true);
                        get(proto().maximum()).setVisible(event.getValue() == true);
                    }
                });
                panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().minimum())).build());
                panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().maximum())).build());
                return panel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                super.onValueSet(populate);
                get(proto().buildings()).setVisible(getValue().filterByBuildings().isBooleanTrue());
                get(proto().minimum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
                get(proto().maximum()).setVisible(getValue().filterByExpectedMoveOut().isBooleanTrue());
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

                // header
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.appendHtmlConstant("<table style=\"width: 100%; white-space: nowrap; border-collapse: separate; border-spacing: 0px;\" border='1'>");
                builder.appendHtmlConstant("<tr>");
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
                builder.appendEscaped(i18n.tr("Expected Move Out"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Tenant Name"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='2'>");
                builder.appendEscaped(i18n.tr("Charge Code"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1' colspan='3'>");
                builder.appendEscaped(i18n.tr("Auto Pay - Suspended"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1' colspan='4'>");
                builder.appendEscaped(i18n.tr("Auto Pay - Suggested"));
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
                builder.appendEscaped(i18n.tr("% Change"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("Payment"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("<th rowspan='1'>");
                builder.appendEscaped(i18n.tr("% of Total"));
                builder.appendHtmlConstant("</th>");
                builder.appendHtmlConstant("</tr>");

                // rows
                Vector<AutoPayReviewDTO> autoPayReviews = (Vector<AutoPayReviewDTO>) data;
                for (AutoPayReviewDTO reviewCase : autoPayReviews) {
                    int numOfCaseRows = caseRows(reviewCase);
                    boolean isFirstLine = true;
                    builder.appendHtmlConstant("<tr>");
                    builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "'>" + SafeHtmlUtils.htmlEscape(reviewCase.building().getValue()) + "</td>");
                    builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "'>" + SafeHtmlUtils.htmlEscape(reviewCase.unit().getValue()) + "</td>");

                    String leaseUrl = AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), false,
                            new CrmSiteMap.Tenants.Lease().formViewerPlace(reviewCase.lease().getPrimaryKey()));
                    builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "'><a href='" + leaseUrl + "'>"
                            + SafeHtmlUtils.htmlEscape(reviewCase.leaseId().getValue()) + "</a></td>");

                    builder.appendHtmlConstant("<td rowspan='" + numOfCaseRows + "'>" + reviewCase.lease().expectedMoveOut().getStringView() + "</td>");

                    for (AutoPayReviewPreauthorizedPaymentDTO reviewPap : reviewCase.pap()) {
                        int numOfTenantRows = reviewPap.items().size();
                        if (!isFirstLine) {
                            builder.appendHtmlConstant("<tr>");
                        }
                        builder.appendHtmlConstant("<td rowspan='" + numOfTenantRows + "'>" + SafeHtmlUtils.htmlEscape(reviewPap.tenantName().getValue())
                                + "</td>");
                        boolean isFirstCharge = true;
                        for (AutoPayReviewChargeDTO charge : reviewPap.items()) {
                            if (!isFirstCharge) {
                                builder.appendHtmlConstant("<tr>");
                            } else {
                                isFirstCharge = false;
                            }
                            builder.appendHtmlConstant("<td>" + SafeHtmlUtils.htmlEscape(charge.leaseCharge().getStringView()) + "</td>");
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>"
                                    + charge.suspended().totalPrice().getStringView() + "</td>");
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>"
                                    + charge.suspended().payment().getStringView() + "</td>");
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>"
                                    + charge.suspended().percent().getStringView() + "</td>");
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>"
                                    + charge.suggested().totalPrice().getStringView() + "</td>");
                            String percentChange = SafeHtmlUtils.htmlEscape(charge.suggested().billableItem().isNull() ? i18n.tr("Removed") : charge
                                    .suggested().percentChange().isNull() ? i18n.tr("New") : charge.suggested().percentChange().getStringView());
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>" + percentChange + "</td>");
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>"
                                    + charge.suggested().payment().getStringView() + "</td>");
                            builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + "'>"
                                    + charge.suggested().percent().getStringView() + "</td>");
                            if (isFirstLine) {
                                builder.appendHtmlConstant("<td rowspan='" + (numOfCaseRows + 1) + "'>" + reviewCase.paymentDue().getStringView() + "</td>");
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
                            + reviewCase.totalSuspended().totalPrice().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                            + reviewCase.totalSuspended().payment().getStringView() + "</td>"); // payment 
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                            + reviewCase.totalSuspended().percent().getStringView() + "</td>");
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                            + reviewCase.totalSuggested().totalPrice().getStringView() + "</td>"); // totalPrice
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                            + "</td>"); // percent change
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                            + reviewCase.totalSuggested().payment().getStringView() + "</td>"); // payment
                    builder.appendHtmlConstant("<td class='" + CommonReportStyles.RCellNumber.name() + " " + CommonReportStyles.RRowTotal.name() + "'>"
                            + reviewCase.totalSuggested().percent().getStringView() + "</td>"); // %                    
                    builder.appendHtmlConstant("</tr>");
                }

                builder.appendHtmlConstant("</table>");
                reportHtml.setHTML(builder.toSafeHtml());

            }

            private int caseRows(AutoPayReviewDTO reviewCase) {
                int rows = 0;
                for (AutoPayReviewPreauthorizedPaymentDTO pap : reviewCase.pap()) {
                    rows += pap.items().size();
                }
                return rows;
            }

        };
    }
}
