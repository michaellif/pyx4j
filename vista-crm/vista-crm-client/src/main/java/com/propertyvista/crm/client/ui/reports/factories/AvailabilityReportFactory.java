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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.reports.HasAdvancedModeReportFactory;
import com.pyx4j.site.client.ui.reports.PropertyCriteriaFolder;
import com.pyx4j.site.client.ui.reports.Report;
import com.pyx4j.site.client.ui.reports.widgets.ReportTable;
import com.pyx4j.site.client.ui.reports.widgets.ReportTable.CellFormatter;
import com.pyx4j.site.client.ui.reports.widgets.ReportTable.MemberStyleCellFormatter;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.c.CEnumSubsetSelector;
import com.propertyvista.common.client.ui.components.c.SubsetSelector.Layout;
import com.propertyvista.crm.client.ui.reports.components.NoResultsHtml;
import com.propertyvista.crm.client.ui.reports.components.NotEmptySetValidator;
import com.propertyvista.crm.rpc.dto.reports.AvailabilityReportDataDTO;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.reports.AvailabilityReportMetadata;

public class AvailabilityReportFactory implements HasAdvancedModeReportFactory<AvailabilityReportMetadata> {

    private static final I18n i18n = I18n.get(AvailabilityReportFactory.class);

    private static MemberColumnDescriptor[] AVAILABILITY_TABLE_COLUMNS;

    static {
        UnitAvailabilityStatus proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class);

        AVAILABILITY_TABLE_COLUMNS = new MemberColumnDescriptor[] {//@formatter:off
                
                // references                
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().propertyCode()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().externalId()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().info().name()).title(i18n.tr("Building Name")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().info().address()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().propertyManager().name()).title(i18n.tr("Property Manager")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.unit().info().number()).title(i18n.tr("Unit Name")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.floorplan().name()).visible(false).title(i18n.tr("Floorplan Name")).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.floorplan().marketingName()).visible(false).title(i18n.tr("Floorplan Marketing Name")).build(),
                
                // status
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.vacancyStatus()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentedStatus()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.scoping()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentReadinessStatus()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.unitRent()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.marketRent()).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentDeltaAbsolute()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentDeltaRelative()).visible(false).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentEndDay()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.moveInDay()).visible(true).build(),
                (MemberColumnDescriptor) new MemberColumnDescriptor.Builder(proto.rentedFromDay()).visible(true).build()
        };//@formatter:on
    }

    private static class UnrentedFormatter extends MemberStyleCellFormatter {

        public UnrentedFormatter() {
            super(EntityFactory.getEntityPrototype(UnitAvailabilityStatus.class).vacancyStatus(), new HashMap<String, String>());
        }

    };

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

            FlowPanel reportPanel;

            ScrollBarPositionMemento scrollBarPositionMemento;

            {
                reportPanel = new FlowPanel();
                reportPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
                reportPanel.getElement().getStyle().setLeft(0, Unit.PX);
                reportPanel.getElement().getStyle().setTop(0, Unit.PX);
                reportPanel.getElement().getStyle().setRight(0, Unit.PX);
                reportPanel.getElement().getStyle().setBottom(0, Unit.PX);
                reportPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
            }

            @Override
            public Widget asWidget() {
                return reportPanel;
            }

            @Override
            public void setData(Object data) {
                reportPanel.clear();
                AvailabilityReportDataDTO reportData = (AvailabilityReportDataDTO) data;
                if (reportData.unitStatuses.isEmpty()) {
                    reportPanel.add(new HTML(NoResultsHtml.get()));
                    return;
                }
                SafeHtmlBuilder bb = new SafeHtmlBuilder();
                bb.appendHtmlConstant("<div style=\"text-align: center; font-size: 22pt; line-height: 22pt;\">");
                bb.appendEscaped(i18n.tr("Unit Availability Report"));
                bb.appendHtmlConstant("</div>");
                bb.appendHtmlConstant("<div style=\"text-align: center;\">");
                bb.appendEscaped(i18n.tr("As of Date: {0}", reportData.asOf));
                bb.appendHtmlConstant("</div>");
                HTML header = new HTML(bb.toSafeHtml());
                reportPanel.add(header);

                ReportTable reportTable = new ReportTable(Arrays.asList(AVAILABILITY_TABLE_COLUMNS), new ArrayList<CellFormatter>());
                reportTable.populate(reportData.unitStatuses);
                reportPanel.add(reportTable);
                reportPanel.addDomHandler(new ScrollHandler() {

                    @Override
                    public void onScroll(ScrollEvent event) {
                        scrollBarPositionMemento = new ScrollBarPositionMemento(reportPanel.getElement().getScrollLeft(), reportPanel.getElement()
                                .getScrollTop());
                    }
                }, ScrollEvent.getType());

            }

            @Override
            public Object getMemento() {
                return scrollBarPositionMemento;
            }

            @Override
            public void setMemento(Object memento) {
                if (memento != null) {
                    ScrollBarPositionMemento scrollBarPosition = (ScrollBarPositionMemento) memento;
                    reportPanel.getElement().setScrollLeft(scrollBarPosition.posX);
                    reportPanel.getElement().setScrollTop(scrollBarPosition.posY);
                }
            }

        };
    }
}
