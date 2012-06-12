/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.availability;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.DataItem;
import com.pyx4j.entity.client.ui.datatable.DataTable;
import com.pyx4j.entity.client.ui.datatable.DataTableModel;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGMeta;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilitySummaryGadgetFactory extends AbstractGadget<UnitAvailabilitySummaryGMeta> {

    private static final I18n i18n = I18n.get(UnitAvailabilitySummaryGadgetFactory.class);

    private class UnitAvailabilitySummaryGadget extends GadgetInstanceBase<UnitAvailabilitySummaryGMeta> {

        private DataTable<UnitAvailabilityStatusSummaryLineDTO> table;

        private HTML asOf;

        private DataTableModel<UnitAvailabilityStatusSummaryLineDTO> tableModel;

        private final AvailabilityReportService service;

        public UnitAvailabilitySummaryGadget(GadgetMetadata gadgetMetadata) {
            super(gadgetMetadata, UnitAvailabilitySummaryGMeta.class, new UnitAvailabilitySummaryMetaForm());

            service = GWT.<AvailabilityReportService> create(AvailabilityReportService.class);

            setDefaultPopulator(new Populator() {
                @Override
                public void populate() {
                    doPopulate();
                }
            });

            initView();
        }

        @Override
        public void setContainerBoard(BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        protected UnitAvailabilitySummaryGMeta createDefaultSettings(Class<UnitAvailabilitySummaryGMeta> metadataClass) {
            UnitAvailabilitySummaryGMeta settings = super.createDefaultSettings(metadataClass);

            UnitAvailabilityStatusSummaryLineDTO proto = EntityFactory.getEntityPrototype(UnitAvailabilityStatusSummaryLineDTO.class);
            settings.columnDescriptors().addAll(ColumnDescriptorConverter.asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.category()).title("").sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.units()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.percentile()).sortable(false).build()
            )));//@formatter:on
            return settings;
        }

        @Override
        protected Widget initContentPanel() {
            VerticalPanel contentPanel = new VerticalPanel();
            contentPanel.setWidth("100%");

            asOf = new HTML();
            contentPanel.add(asOf);
            contentPanel.setCellHorizontalAlignment(asOf, HasHorizontalAlignment.ALIGN_CENTER);

            tableModel = new DataTableModel<UnitAvailabilityStatusSummaryLineDTO>(UnitAvailabilityStatusSummaryLineDTO.class);
            table = new DataTable<UnitAvailabilityStatusSummaryLineDTO>(tableModel);
            table.setWidth("100%");
            table.getElement().getStyle().setProperty("tableLayout", "auto");
            table.setHasColumnClickSorting(false);
            table.setColumnSelectorVisible(false);
            table.setHasDetailsNavigation(false);
            table.setMarkSelectedRow(false);

            contentPanel.add(table);
            contentPanel.setCellHorizontalAlignment(table, HasHorizontalAlignment.ALIGN_CENTER);

            return contentPanel;
        }

        private void doPopulate() {
            service.unitStatusSummary(new AsyncCallback<Vector<UnitAvailabilityStatusSummaryLineDTO>>() {

                @Override
                public void onSuccess(Vector<UnitAvailabilityStatusSummaryLineDTO> result) {
                    tableModel.setColumnDescriptors(ColumnDescriptorConverter.asColumnDescriptorist(UnitAvailabilityStatusSummaryLineDTO.class, getMetadata()
                            .columnDescriptors()));

                    List<DataItem<UnitAvailabilityStatusSummaryLineDTO>> dataItems = new Vector<DataItem<UnitAvailabilityStatusSummaryLineDTO>>();
                    for (UnitAvailabilityStatusSummaryLineDTO statusRecord : result) {
                        dataItems.add(new DataItem<UnitAvailabilityStatusSummaryLineDTO>(statusRecord));
                    }

                    asOf.setHTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("As of Date: {0}", getStatusDate())).toSafeHtml());
                    tableModel.populateData(dataItems, 0, false, 0);

                    populateSucceded();
                }

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }
            }, new Vector<Building>(containerBoard.getSelectedBuildingsStubs()), getStatusDate());
        }

        private LogicalDate getStatusDate() {
            return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate();
        }
    }

    public UnitAvailabilitySummaryGadgetFactory() {
        super(UnitAvailabilitySummaryGMeta.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Availability.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<UnitAvailabilitySummaryGMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new UnitAvailabilitySummaryGadget(gadgetMetadata);
    }

}
