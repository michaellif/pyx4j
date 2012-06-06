/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.payments;

import static com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter.asColumnDesciptorEntityList;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentReportService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.PaymentRecordsGadgetMetadata;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentRecordsGadgetFactory extends AbstractGadget<PaymentRecordsGadgetMetadata> {

    private final static I18n i18n = I18n.get(PaymentRecordsGadgetFactory.class);

    private static class PaymentRecordsGadget extends ListerGadgetInstanceBase<PaymentRecordForReportDTO, PaymentRecordsGadgetMetadata> {

        private final PaymentReportService service;

        private HTML titlePanel;

        public PaymentRecordsGadget(GadgetMetadata gadgetMetadata) {
            super(gadgetMetadata, PaymentRecordsGadgetMetadata.class, new PaymentRecordsGadgetMetadataForm(), PaymentRecordForReportDTO.class, false);
            this.service = GWT.<PaymentReportService> create(PaymentReportService.class);
        }

        @Override
        public void setContainerBoard(final BoardView board) {
            super.setContainerBoard(board);
            board.addBuildingSelectionChangedEventHandler(new BuildingSelectionChangedEventHandler() {
                @Override
                public void onBuildingSelectionChanged(BuildingSelectionChangedEvent event) {
                    populate();
                }
            });
        }

        @Override
        protected PaymentRecordsGadgetMetadata createDefaultSettings(Class<PaymentRecordsGadgetMetadata> metadataClass) {
            PaymentRecordsGadgetMetadata settings = super.createDefaultSettings(metadataClass);

            settings.paymentMethodFilter().addAll(EnumSet.allOf(PaymentType.class));
            settings.paymentStatusFilter().addAll(EnumSet.complementOf(EnumSet.of(PaymentStatus.Processing)));

            PaymentRecordForReportDTO proto = EntityFactory.create(PaymentRecordForReportDTO.class);
            settings.columnDescriptors().addAll(asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.merchantAccount().accountNumber()).title(i18n.tr("Merchant Account")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().belongsTo().propertyCode()).title(i18n.tr("Building")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseId()).title(i18n.tr("Lease")).build(),
                    new MemberColumnDescriptor.Builder(proto.paymentMethod().leaseParticipant().customer()).title(i18n.tr("Tenant")).build(),                    
                    new MemberColumnDescriptor.Builder(proto.paymentMethod().type()).title(i18n.tr("Method")).build(),
                    new MemberColumnDescriptor.Builder(proto.paymentStatus()).title(i18n.tr("Status")).build(),
                    new MemberColumnDescriptor.Builder(proto.createdDate()).title(i18n.tr("Created")).build(),
                    new MemberColumnDescriptor.Builder(proto.receivedDate()).title(i18n.tr("Received")).build(),
                    new MemberColumnDescriptor.Builder(proto.finalizeDate()).title(i18n.tr("Finalized")).build(),
                    new MemberColumnDescriptor.Builder(proto.targetDate()).title(i18n.tr("Target")).build(),
                    new MemberColumnDescriptor.Builder(proto.amount()).title(i18n.tr("Amount")).build()                    
            )));//@formatter:on
            return settings;
        }

        @Override
        protected Widget initContentPanel() {
            VerticalPanel contentPanel = new VerticalPanel();
            contentPanel.setWidth("100%");

            contentPanel.add(initTitleWidget());
            contentPanel.add(initListerWidget());

            return contentPanel;
        }

        @Override
        protected void populatePage(final int pageNumber) {
            service.paymentRecords(//@formatter:off
                    new AsyncCallback<EntitySearchResult<PaymentRecordForReportDTO>>() {

                        @Override
                        public void onSuccess(EntitySearchResult<PaymentRecordForReportDTO> result) {
                            redrawTitle();
                            setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                            populateSucceded();
                        }
        
                        @Override
                        public void onFailure(Throwable caught) {
                            populateFailed(caught);
                        }
        
                    },
                    new Vector<Building>(containerBoard.getSelectedBuildingsStubs()),
                    getTargetDate(),
                    new Vector<PaymentType>(getMetadata().paymentMethodFilter()),
                    new Vector<PaymentRecord.PaymentStatus>(getMetadata().paymentStatusFilter()),
                    pageNumber,
                    getPageSize(),
                    new Vector<Sort>(getListerSortingCriteria()));//@formatter:on
        }

        @Override
        protected void onItemSelect(PaymentRecordForReportDTO item) {
            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(PaymentRecord.class, item.getPrimaryKey()));
        }

        private LogicalDate getTargetDate() {
            return getMetadata().customizeTargetDate().isBooleanTrue() ? getMetadata().targetDate().getValue() : new LogicalDate();
        }

        private Widget initTitleWidget() {
            titlePanel = new HTML("");
            return titlePanel;
        }

        private void redrawTitle() {
            // actually "none" is never supposed to happen thanks to automatic setup form validation
            String paymentTypeFilterView = !getMetadata().paymentMethodFilter().isEmpty() ? makeListView(getMetadata().paymentMethodFilter()) : i18n.tr("none");
            String statusFilterView = !getMetadata().paymentStatusFilter().isEmpty() ? makeListView(getMetadata().paymentStatusFilter()) : i18n.tr("none");

            titlePanel.setHTML(new SafeHtmlBuilder()//@formatter:off                     
                    .appendHtmlConstant("<div>")
                        .appendEscaped(i18n.tr("Target Date: {0}", getTargetDate()))
                    .appendHtmlConstant("</div>")
                    
                    .appendHtmlConstant("<div>")                    
                        .appendEscaped(i18n.tr("Payment Method: {0}", paymentTypeFilterView))
                    .appendHtmlConstant("</div>")
                    
                    .appendHtmlConstant("<div>")                    
                        .appendEscaped(i18n.tr("Payment Statuses: {0}", statusFilterView))
                    .appendHtmlConstant("</div>")
                    
                    .toSafeHtml());//@formatter:on

            titlePanel.getElement().getStyle().setProperty("textAlign", "center");
        }

        private static String makeListView(Iterable<?> col) {
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
    }

    public PaymentRecordsGadgetFactory() {
        super(PaymentRecordsGadgetMetadata.class);
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
    protected GadgetInstanceBase<PaymentRecordsGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new PaymentRecordsGadget(gadgetMetadata);
    }

}
