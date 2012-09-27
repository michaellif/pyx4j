/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.impl;

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
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTable;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.forms.UnitAvailabilitySummaryGadgetMetadataForm;
import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatusSummaryLineDTO;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class UnitAvailabilitySummaryGadget extends GadgetInstanceBase<UnitAvailabilitySummaryGadgetMetadata> {

    private static final I18n i18n = I18n.get(UnitAvailabilitySummaryGadget.class);

    private DataTable<UnitAvailabilityStatusSummaryLineDTO> table;

    private HTML asOf;

    private DataTableModel<UnitAvailabilityStatusSummaryLineDTO> tableModel;

    private final AvailabilityReportService service;

    public UnitAvailabilitySummaryGadget(UnitAvailabilitySummaryGadgetMetadata gadgetMetadata) {
        super(gadgetMetadata, UnitAvailabilitySummaryGadgetMetadata.class, new UnitAvailabilitySummaryGadgetMetadataForm());

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
    public void setContainerBoard(IBuildingFilterContainer board) {
        super.setContainerBoard(board);
        board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
            @Override
            public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                populate();
            }
        });
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
        return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
    }
}