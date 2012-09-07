/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 14, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.factories;

import java.util.Arrays;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.HasAdvancedModeReportFactory;
import com.pyx4j.site.client.ui.reports.PropertyCriteriaFolder;
import com.pyx4j.site.client.ui.reports.Report;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.SubsetSelector.Layout;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportFactory implements HasAdvancedModeReportFactory<AvailabilityReportMetadata> {

    private static final I18n i18n = I18n.get(AvailabilityReportFactory.class);

    private static ColumnDescriptor[] AVAILABILITY_TABLE_COLUMNS;

    static {
        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);

        AVAILABILITY_TABLE_COLUMNS = new ColumnDescriptor[] {//@formatter:off
                
                // references
                new MemberColumnDescriptor.Builder(proto.building().propertyCode()).build(),
                new MemberColumnDescriptor.Builder(proto.building().externalId()).build(),
                new MemberColumnDescriptor.Builder(proto.building().info().name()).title(i18n.tr("Building Name")).build(),
                new MemberColumnDescriptor.Builder(proto.building().info().address()).build(),
                new MemberColumnDescriptor.Builder(proto.building().propertyManager().name()).title(i18n.tr("Property Manager")).build(),
                new MemberColumnDescriptor.Builder(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                new MemberColumnDescriptor.Builder(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                new MemberColumnDescriptor.Builder(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                new MemberColumnDescriptor.Builder(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                
                // status
                new MemberColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                new MemberColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.unitRent()).build(),
                new MemberColumnDescriptor.Builder(proto.marketRent()).build(),
                new MemberColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                new MemberColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build(),
                new MemberColumnDescriptor.Builder(proto.daysVacant()).build(),
                new MemberColumnDescriptor.Builder(proto.revenueLost()).build()
        };//@formatter:on
    }

    @Override
    public CEntityForm<AvailabilityReportMetadata> getReportSettingsForm() {
        CEntityDecoratableForm<AvailabilityReportMetadata> form = new CEntityDecoratableForm<AvailabilityReportMetadata>(AvailabilityReportMetadata.class) {

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
        CEntityDecoratableForm<AvailabilityReportMetadata> form = new CEntityDecoratableForm<AvailabilityReportMetadata>(AvailabilityReportMetadata.class) {

            @Override
            public IsWidget createContent() {
                int row = -1;
                FormFlexPanel panel = new FormFlexPanel();
                panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).labelWidth(10).componentWidth(10).build());
                panel.setWidget(
                        ++row,
                        0,
                        inject(proto().availbilityTableCriteria(),
                                new PropertyCriteriaFolder(VistaImages.INSTANCE, UnitAvailabilityStatus.class, Arrays.asList(AVAILABILITY_TABLE_COLUMNS))));
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

                for (ColumnDescriptor desc : AVAILABILITY_TABLE_COLUMNS) {
                    bb.appendHtmlConstant("<th style=\"text-align: left\">");
                    bb.appendEscaped(desc.getColumnTitle());
                    bb.appendHtmlConstant("</th>");
                }

                bb.appendHtmlConstant("</tr>");

                for (UnitAvailabilityStatus status : reportData.unitStatuses) {
                    bb.appendHtmlConstant("<tr>");
                    for (ColumnDescriptor desc : AVAILABILITY_TABLE_COLUMNS) {
                        cell(bb, desc.convert(status));
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
