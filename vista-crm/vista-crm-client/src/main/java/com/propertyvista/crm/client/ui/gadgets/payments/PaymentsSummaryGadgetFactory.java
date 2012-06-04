/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.payments;

import static com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter.asColumnDesciptorEntityList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentsSummaryGadgetFactory extends AbstractGadget<PaymentsSummaryGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentsSummaryGadgetFactory.class);

    private class PaymentsSummaryGadget extends ListerGadgetInstanceBase<PaymentsSummary, PaymentsSummaryGadgetMetadata> {

        private final PaymentReportService service;

        private DataTableModel<PaymentFeesDTO> feesTableModel;

        public PaymentsSummaryGadget(GadgetMetadata gadgetMetadata) {
            super(gadgetMetadata, PaymentsSummaryGadgetMetadata.class, null, PaymentsSummary.class, false);
            service = GWT.<PaymentReportService> create(PaymentReportService.class);
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
        protected PaymentsSummaryGadgetMetadata createDefaultSettings(Class<PaymentsSummaryGadgetMetadata> metadataClass) {

            PaymentsSummaryGadgetMetadata settings = super.createDefaultSettings(metadataClass);

            settings.paymentStatus().addAll(EnumSet.complementOf(EnumSet.of(PaymentStatus.Processing)));

            PaymentsSummary proto = EntityFactory.create(PaymentsSummary.class);
            settings.columnDescriptors().addAll(asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.merchantAccount()).build(),
                    new MemberColumnDescriptor.Builder(proto.status()).build(),
                    new MemberColumnDescriptor.Builder(proto.cash()).build(),
                    new MemberColumnDescriptor.Builder(proto.cheque()).build(),
                    new MemberColumnDescriptor.Builder(proto.eCheque()).build(),
                    new MemberColumnDescriptor.Builder(proto.eft()).build(),
                    new MemberColumnDescriptor.Builder(proto.cc()).build(),
                    new MemberColumnDescriptor.Builder(proto.interac()).build()                    
            )));//@formatter:on

            return settings;
        }

        @Override
        protected void populatePage(final int pageNumber) {
            service.paymentsSummary(//@formatter:off
                    new AsyncCallback<EntitySearchResult<PaymentsSummary>>() {
                        
                        @Override
                        public void onSuccess(EntitySearchResult<PaymentsSummary> result) {
                            setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                            populateFeesPanel();
                        }
                        
                        @Override
                        public void onFailure(Throwable caught) {
                            populateFailed(caught);
                        }
                        
                    }, 
                    new Vector<Building>(containerBoard.getSelectedBuildingsStubs()),
                    getStatusDate(),
                    new Vector<PaymentRecord.PaymentStatus>(getMetadata().paymentStatus()),
                    pageNumber,
                    getPageSize(),
                    new Vector<Sort>(getListerSortingCriteria())
            );//@formatter:on
        }

        @Override
        protected Widget initContentPanel() {

            VerticalPanel contentPanel = new VerticalPanel();
            contentPanel.setWidth("100%");

            contentPanel.add(initListerWidget());
            contentPanel.add(initFeesPanel());

            return contentPanel;
        }

        private Widget initFeesPanel() {
            VerticalPanel feesPanel = new VerticalPanel();
            feesPanel.setWidth("100%");

            HTML feesCaption = new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Note: The following fees are applied (as of today)")).toSafeHtml());
            feesCaption.setWidth("100%");
            feesCaption.getElement().getStyle().setPaddingTop(2, Unit.EM);
            feesCaption.getElement().getStyle().setProperty("textAlign", "center");
            feesPanel.add(feesCaption);

            PaymentFeesDTO proto = EntityFactory.getEntityPrototype(PaymentFeesDTO.class);
            feesTableModel = new DataTableModel<PaymentFeesDTO>(PaymentFeesDTO.class);
            feesTableModel.setColumnDescriptors(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.paymentFeeMeasure()).sortable(false).width("20%").build(),
                    new MemberColumnDescriptor.Builder(proto.cash()).sortable(false).width("13.33%").build(),
                    new MemberColumnDescriptor.Builder(proto.cheque()).sortable(false).width("13.33%").build(),
                    new MemberColumnDescriptor.Builder(proto.eCheque()).sortable(false).width("13.33%").build(),
                    new MemberColumnDescriptor.Builder(proto.eft()).sortable(false).width("13.33%").build(),
                    new MemberColumnDescriptor.Builder(proto.cc()).sortable(false).width("13.33%").build(),
                    new MemberColumnDescriptor.Builder(proto.interac()).sortable(false).width("13.33%").build()
            ));//@formatter:on

            DataTable<PaymentFeesDTO> feesTable = new DataTable<PaymentFeesDTO>(feesTableModel);
            feesTable.setWidth("100%");
            feesTable.getElement().getStyle().setProperty("tableLayout", "auto");
            feesTable.setHasColumnClickSorting(false);
            feesTable.setColumnSelectorVisible(false);
            feesTable.setHasDetailsNavigation(false);
            feesTable.setMarkSelectedRow(false);
            feesPanel.add(feesTable);

            feesPanel.setCellHorizontalAlignment(feesTable, HasHorizontalAlignment.ALIGN_CENTER);

            return feesPanel;
        }

        private void populateFeesPanel() {
            service.paymentsFees(new AsyncCallback<Vector<PaymentFeesDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }

                @Override
                public void onSuccess(Vector<PaymentFeesDTO> result) {
                    List<DataItem<PaymentFeesDTO>> dataItems = new ArrayList<DataItem<PaymentFeesDTO>>(2);
                    dataItems.add(new DataItem<PaymentFeesDTO>(result.get(0)));
                    dataItems.add(new DataItem<PaymentFeesDTO>(result.get(1)));
                    feesTableModel.populateData(dataItems, 0, false, 2);
                    populateSucceded();
                }
            });

        }

        private LogicalDate getStatusDate() {
            return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate();
        }
    }

    public PaymentsSummaryGadgetFactory() {
        super(PaymentsSummaryGadgetMetadata.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Payments.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<PaymentsSummaryGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new PaymentsSummaryGadget(gadgetMetadata);
    }

}
