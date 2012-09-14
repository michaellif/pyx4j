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
import java.util.Iterator;
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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTable;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentFeesDTO;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentsSummaryGadgetFactory extends AbstractGadget<PaymentsSummaryGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentsSummaryGadgetFactory.class);

    private class PaymentsSummaryGadget extends ListerGadgetInstanceBase<PaymentsSummary, PaymentsSummaryGadgetMetadata> {

        private final PaymentReportService service;

        private DataTableModel<PaymentFeesDTO> feesTableModel;

        private HTML summaryTitlePanel;

        public PaymentsSummaryGadget(GadgetMetadata gadgetMetadata) {
            super(gadgetMetadata, PaymentsSummaryGadgetMetadata.class, new PaymentsSummaryGadgetMetadataForm(), PaymentsSummary.class, false);
            service = GWT.<PaymentReportService> create(PaymentReportService.class);
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
        protected PaymentsSummaryGadgetMetadata createDefaultSettings(Class<PaymentsSummaryGadgetMetadata> metadataClass) {

            PaymentsSummaryGadgetMetadata settings = super.createDefaultSettings(metadataClass);

            settings.paymentStatus().addAll(PaymentStatus.processed());

            PaymentsSummary proto = EntityFactory.create(PaymentsSummary.class);
            settings.columnDescriptors().addAll(asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    (PaymentsSummary.summaryByBuilding)?
                    new MemberColumnDescriptor.Builder(proto.building()).build():
                    new MemberColumnDescriptor.Builder(proto.merchantAccount().accountNumber()).title(i18n.tr("Merchant Account")).build(),
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

            contentPanel.add(initSummaryPanel());
            contentPanel.add(initFeesPanel());

            return contentPanel;
        }

        private Widget initSummaryPanel() {
            VerticalPanel summaryPanel = new VerticalPanel();
            summaryPanel.setWidth("100%");

            summaryTitlePanel = new HTML();
            summaryTitlePanel.setWidth("100%");
            summaryTitlePanel.getElement().getStyle().setProperty("textAlign", "center");
            summaryPanel.add(summaryTitlePanel);

            summaryPanel.add(initListerWidget());

            return summaryPanel;
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
                    new MemberColumnDescriptor.Builder(proto.cash()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.cheque()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.eCheque()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.eft()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.cc()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.interacCaledon()).sortable(false).build(),
                    new MemberColumnDescriptor.Builder(proto.interacVisa()).sortable(false).build()
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

        private void redrawSummaryCaption() {
            String statusFilterView = !getMetadata().paymentStatus().isEmpty() ? makeListView(getMetadata().paymentStatus()) : i18n.tr("none");

            summaryTitlePanel.setHTML(new SafeHtmlBuilder()//@formatter:off                     
                    .appendHtmlConstant("<div>")
                        .appendEscaped(i18n.tr("Target Date: {0}", getStatusDate()))
                    .appendHtmlConstant("</div>")
                                        
                    .appendHtmlConstant("<div>")                    
                        .appendEscaped(i18n.tr("Payment Statuses: {0}", statusFilterView))
                    .appendHtmlConstant("</div>")
                    .toSafeHtml());//@formatter:on

        }

        private String makeListView(Iterable<?> col) {
            StringBuilder viewBuilder = new StringBuilder();
            Iterator<?> i = col.iterator();
            if (i.hasNext()) {
                viewBuilder.append(i.next().toString());
            }
            while (i.hasNext()) {
                viewBuilder.append(", ").append(i.next().toString());
            }
            return viewBuilder.toString();

        }

        private void populateFeesPanel() {
            service.paymentsFees(new AsyncCallback<Vector<PaymentFeesDTO>>() {

                @Override
                public void onFailure(Throwable caught) {
                    populateFailed(caught);
                }

                @Override
                public void onSuccess(Vector<PaymentFeesDTO> result) {
                    if (!result.isEmpty()) {
                        List<DataItem<PaymentFeesDTO>> dataItems = new ArrayList<DataItem<PaymentFeesDTO>>(2);
                        dataItems.add(new DataItem<PaymentFeesDTO>(result.get(0)));
                        dataItems.add(new DataItem<PaymentFeesDTO>(result.get(1)));
                        feesTableModel.populateData(dataItems, 0, false, 2);
                    } else {
                        feesTableModel.clearData();
                    }
                    redrawSummaryCaption();
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
    protected GadgetInstanceBase<PaymentsSummaryGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new PaymentsSummaryGadget(gadgetMetadata);
    }

}
