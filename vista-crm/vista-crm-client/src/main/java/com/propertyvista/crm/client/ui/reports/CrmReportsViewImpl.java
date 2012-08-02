/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.site.client.ui.reports.AbstractReportsView;
import com.pyx4j.site.client.ui.reports.HasAdvancedModeReportFactory;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.ReportFactory;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class CrmReportsViewImpl extends AbstractReportsView implements CrmReportsView {

    private static Map<Class<? extends ReportMetadata>, ReportFactory> factoryMap;

    static {
        factoryMap = new HashMap<Class<? extends ReportMetadata>, ReportFactory>();

        factoryMap.put(MockupReportSettings.class, new HasAdvancedModeReportFactory() {
            @Override
            public CEntityForm<MockupReportSettings> getReportSettingsForm() {
                CEntityForm<MockupReportSettings> form = new CEntityDecoratableForm<MockupReportSettings>(MockupReportSettings.class) {

                    @Override
                    public IsWidget createContent() {
                        FormFlexPanel simple = new FormFlexPanel();
                        int row = -1;
                        simple.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().valueX())).build());
                        simple.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().valueY())).build());
                        return simple;
                    }
                };
                form.initContent();
                return form;
            }

            @Override
            public CEntityForm<MockupReportSettings> getAdvancedReportSettingsForm() {
                CEntityForm<MockupReportSettings> form = new CEntityForm<MockupReportSettings>(MockupReportSettings.class) {
                    @Override
                    public IsWidget createContent() {
                        FormFlexPanel advanced = new FormFlexPanel();
                        int row = -1;
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValueX())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValueY())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValueZ())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValue1())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValue2())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValue3())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValue4())).build());
                        advanced.setWidget(++row, 0, new WidgetDecorator.Builder(inject(proto().advancedValue5())).build());
                        return advanced;
                    }
                };
                form.initContent();
                return form;
            }

            @Override
            public Report getReport() {

                return new Report() {

                    private HTML reportHTML;

                    {
                        reportHTML = new HTML();
                        reportHTML.setSize("100%", "300em");
                        SafeHtmlBuilder mockupReport = new SafeHtmlBuilder();
                        mockupReport.appendHtmlConstant("<div>");
                        for (int i = 0; i < 1000; ++i) {
                            mockupReport.appendHtmlConstant("<div>this is SPARTA!!!</div>");
                        }
                        mockupReport.appendHtmlConstant("</div>");
                        reportHTML.setHTML(mockupReport.toSafeHtml());
                    }

                    @Override
                    public Widget asWidget() {
                        return reportHTML;
                    }

                    @Override
                    public void setData(Object data) {
                        // TODO ignore
                    }
                };
            }

        });

        factoryMap.put(AvailabilityReportMetadata.class, new ReportFactory<AvailabilityReportMetadata>() {

            @Override
            public CEntityForm<AvailabilityReportMetadata> getReportSettingsForm() {
                CEntityDecoratableForm<AvailabilityReportMetadata> form = new CEntityDecoratableForm<AvailabilityReportMetadata>(
                        AvailabilityReportMetadata.class) {

                    @Override
                    public IsWidget createContent() {
                        int row = -1;
                        FormFlexPanel panel = new FormFlexPanel();
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).build());
                        return panel;
                    }
                };
                form.initContent();
                return form;
            }

            @Override
            public Report getReport() {
                // TODO Auto-generated method stub
                return new Report() {
                    HTML reportHtml = new HTML();

                    @Override
                    public Widget asWidget() {
                        return reportHtml;
                    }

                    @Override
                    public void setData(Object data) {
                        AvailabilityReportDataDTO reportData = (AvailabilityReportDataDTO) data;
                        SafeHtmlBuilder bb = new SafeHtmlBuilder();
                        bb.appendHtmlConstant("<table>");
                        for (UnitAvailabilityStatus status : reportData.unitStatuses) {
                            bb.appendHtmlConstant("<tr>");

                            cell(bb, status.building().propertyCode().getValue());
                            cell(bb, status.unit().info().number().getValue());
                            cell(bb, status.vacancyStatus().getValue().toString());

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
        });

    }

    public CrmReportsViewImpl() {
        super(factoryMap);
    }

}
