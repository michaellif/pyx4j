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
import java.util.Set;

import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.site.client.ui.reports.AbstractReportsView;
import com.pyx4j.site.client.ui.reports.HasAdvancedModeReportFactory;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.ReportFactory;

import com.propertyvista.common.client.ui.components.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class CrmReportsViewImpl extends AbstractReportsView implements CrmReportsView {

    private static final I18n i18n = I18n.get(CrmReportsViewImpl.class);

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

        factoryMap.put(AvailabilityReportMetadata.class, new HasAdvancedModeReportFactory<AvailabilityReportMetadata>() {

            @Override
            public CEntityForm<AvailabilityReportMetadata> getReportSettingsForm() {
                CEntityDecoratableForm<AvailabilityReportMetadata> form = new CEntityDecoratableForm<AvailabilityReportMetadata>(
                        AvailabilityReportMetadata.class) {

                    @SuppressWarnings("unchecked")
                    @Override
                    public IsWidget createContent() {
                        int row = -1;
                        FormFlexPanel panel = new FormFlexPanel();
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).labelWidth(10).componentWidth(10).build());
                        panel.setWidget(
                                ++row,
                                0,
                                new DecoratorBuilder(inject(proto().vacancyStatus(), new CEnumSubsetSelector<UnitAvailabilityStatus.Vacancy>(
                                        UnitAvailabilityStatus.Vacancy.class, Layout.Horizontal))).labelWidth(10).componentWidth(10).build());
                        get(proto().vacancyStatus()).addValueValidator(new NotEmptySetValidator());
                        panel.setWidget(
                                ++row,
                                0,
                                new DecoratorBuilder(inject(proto().rentedStatus(), new CEnumSubsetSelector<UnitAvailabilityStatus.RentedStatus>(
                                        UnitAvailabilityStatus.RentedStatus.class, Layout.Vertical))).labelWidth(10).componentWidth(10).build());
                        get(proto().rentedStatus()).addValueValidator(new NotEmptySetValidator());
                        panel.setWidget(
                                row,
                                1,
                                new DecoratorBuilder(inject(proto().rentReadinessStatus(), new CEnumSubsetSelector<UnitAvailabilityStatus.RentReadiness>(
                                        UnitAvailabilityStatus.RentReadiness.class, Layout.Vertical))).labelWidth(10).componentWidth(15).build());
                        get(proto().rentReadinessStatus()).addValueValidator(new NotEmptySetValidator());
                        return panel;
                    }
                };
                form.initContent();
                return form;
            }

            @Override
            public CEntityForm<AvailabilityReportMetadata> getAdvancedReportSettingsForm() {
                CEntityDecoratableForm<AvailabilityReportMetadata> form = new CEntityDecoratableForm<AvailabilityReportMetadata>(
                        AvailabilityReportMetadata.class) {

                    @Override
                    public IsWidget createContent() {
                        int row = -1;
                        FormFlexPanel panel = new FormFlexPanel();
                        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).labelWidth(10).componentWidth(10).build());
                        panel.setWidget(++row, 0, new HTML("imagine there is a column criteria widget"));
                        return panel;
                    }
                };

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
                        AvailabilityReportDataDTO reportData = (AvailabilityReportDataDTO) data;
                        SafeHtmlBuilder bb = new SafeHtmlBuilder();
                        bb.appendHtmlConstant("<div style=\"text-align: center; font-size: 22pt\">");
                        bb.appendEscaped(i18n.tr("Unit Availability Report"));
                        bb.appendHtmlConstant("</div>");
                        bb.appendHtmlConstant("<div>");
                        bb.appendEscaped(i18n.tr("As of Date: {0}", reportData.asOf));
                        bb.appendHtmlConstant("</div>");

                        bb.appendHtmlConstant("<table style=\"white-space: nowrap; border-collapse: separate; border-spacing: 15pt;\">");
                        bb.appendHtmlConstant("<tr>");
                        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);
                        ColumnDescriptor[] columns = {//@formatter:off
                                defColumn(proto.building().propertyCode()).build(),
                                defColumn(proto.building().externalId()).build(),
                                defColumn(proto.building().info().name()).title(i18n.tr("Building Name")).build(),
                                defColumn(proto.building().info().address()).build(),
                                defColumn(proto.building().propertyManager().name()).title(i18n.tr("Property Manager")).build(),                    
                                defColumn(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                                defColumn(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                                defColumn(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                                defColumn(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                                
                                // status
                                defColumn(proto.vacancyStatus()).build(),
                                defColumn(proto.rentedStatus()).visible(true).build(),
                                defColumn(proto.scoping()).visible(true).build(),
                                defColumn(proto.rentReadinessStatus()).visible(true).build(),
                                defColumn(proto.unitRent()).build(),
                                defColumn(proto.marketRent()).build(),
                                defColumn(proto.rentDeltaAbsolute()).visible(true).build(),
                                defColumn(proto.rentDeltaRelative()).visible(false).build(),
                                defColumn(proto.rentEndDay()).visible(true).build(),
                                defColumn(proto.moveInDay()).visible(true).build(),
                                defColumn(proto.rentedFromDay()).visible(true).build(),
                                defColumn(proto.daysVacant()).build(),
                                defColumn(proto.revenueLost()).build()
                        };//@formatter:on

                        for (ColumnDescriptor desc : columns) {
                            bb.appendHtmlConstant("<th style=\"text-align: left\">");
                            bb.appendEscaped(desc.getColumnTitle());
                            bb.appendHtmlConstant("</th>");
                        }

                        bb.appendHtmlConstant("</tr>");

                        for (UnitAvailabilityStatus status : reportData.unitStatuses) {
                            bb.appendHtmlConstant("<tr>");
                            for (ColumnDescriptor desc : columns) {
                                cell(bb, desc.convert(status));
                            }
                            bb.appendHtmlConstant("</tr>");
                        }
                        bb.appendHtmlConstant("</table>");

                        reportHtml.setHTML(bb.toSafeHtml());
                    }

                    private MemberColumnDescriptor.Builder defColumn(IObject<?> object) {
                        return new MemberColumnDescriptor.Builder(object);
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

    public static class NotEmptySetValidator implements EditableValueValidator {

        @Override
        public ValidationError isValid(CComponent component, Object value) {
            boolean isEmpty = value == null;
            if (value != null) {
                isEmpty = ((Set<?>) value).isEmpty();
            }
            if (isEmpty) {
                return new ValidationError(component, i18n.tr("at least one status is required"));
            } else {
                return null;
            }
        }

    }

    @Override
    public IMemento getMemento() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeState(Place place) {
        // TODO Auto-generated method stub

    }

    @Override
    public void restoreState() {
        // TODO Auto-generated method stub

    }
}
