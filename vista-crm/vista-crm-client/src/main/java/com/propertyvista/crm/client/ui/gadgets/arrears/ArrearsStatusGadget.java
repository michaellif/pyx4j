/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.Arrays;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsStatusGadget extends AbstractGadgetFactory<ArrearsStatusGadgetMetadata> {

    private final static I18n i18n = I18n.get(ArrearsStatusGadget.class);

    private static class ArrearsStatusGadgetInstance extends ListerGadgetInstanceBase<LeaseArrearsSnapshotDTO, ArrearsStatusGadgetMetadata> {

        private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(CDatePicker.defaultDateFormat);

        private final ArrearsReportService service;

        private FormFlexPanel contentPanel;

        private HTML titleBannerLabel;

        public ArrearsStatusGadgetInstance(GadgetMetadata gmd) {
            super(gmd, ArrearsStatusGadgetMetadata.class, new ArrearsStatusGadgetMetadataForm(), LeaseArrearsSnapshotDTO.class, false);
            service = GWT.<ArrearsReportService> create(ArrearsReportService.class);
        }

        @Override
        protected ArrearsStatusGadgetMetadata createDefaultSettings(Class<ArrearsStatusGadgetMetadata> metadataClass) {
            ArrearsStatusGadgetMetadata settings = super.createDefaultSettings(metadataClass);
            settings.category().setValue(DebitType.total);
            LeaseArrearsSnapshotDTO proto = EntityFactory.getEntityPrototype(LeaseArrearsSnapshotDTO.class);
            settings.columnDescriptors().addAll(ColumnDescriptorConverter.asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().propertyCode()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().name()).title(i18n.tr("Building")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().streetNumber()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().streetName()).visible(false).build(),                    
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().province().name()).visible(false).title(i18n.tr("Province")).build(),                    
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().info().address().country().name()).visible(false).title(i18n.tr("Country")).build(),                    
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().building().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().unit().info().number()).title(i18n.tr("Unit")).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseId()).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseFrom()).build(),
                    new MemberColumnDescriptor.Builder(proto.billingAccount().lease().leaseTo()).build(),
                    
                    // arrears
                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucketCurrent()).build(),
                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucket30()).build(),
                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucket60()).build(),
                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucket90()).build(),
                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().bucketOver90()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().arrearsAmount()).build()
// TODO calculate CREDIT AMOUNT                    
//                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().creditAmount()).build(),                    
//                    new MemberColumnDescriptor.Builder(proto.selectedBuckets().totalBalance()).build(),
// TODO calculate LMR                    
//                    new MemberColumnDescriptor.Builder(proto.lmrToUnitRentDifference()).build()                   
            )));//@formatter:on
            return settings;
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
            contentPanel = new FormFlexPanel();
            contentPanel.setWidget(1, 0, initTitleBannerPanel());
            contentPanel.setWidget(2, 0, initListerWidget());
            return contentPanel;
        }

        private Widget initTitleBannerPanel() {
            HorizontalPanel titleBannerPanel = new HorizontalPanel();
            titleBannerPanel.setWidth("100%");
            titleBannerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

            titleBannerLabel = new HTML();
            titleBannerPanel.add(titleBannerLabel);
            return titleBannerPanel.asWidget();
        }

        @Override
        protected void populatePage(final int pageNumber) {
            if (containerBoard.getSelectedBuildingsStubs() == null) {
                refreshTitleBanner();
                setPageData(new Vector<LeaseArrearsSnapshotDTO>(), 0, 0, false);
                populateSucceded();
                return;
            } else {
                Vector<Building> buildings = new Vector<Building>(containerBoard.getSelectedBuildingsStubs());
                Vector<Sort> sortingCriteria = new Vector<Sort>(getListerSortingCriteria());

                service.leaseArrearsRoster(new DefaultAsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<LeaseArrearsSnapshotDTO> result) {
                        refreshTitleBanner();
                        setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }

                }, buildings, getStatusDate(), getMetadata().category().getValue(), sortingCriteria, pageNumber, getMetadata().pageSize().getValue());
            }

        }

        private LogicalDate getStatusDate() {
            return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate(ClientContext.getServerDate());
        }

        private void refreshTitleBanner() {
            String unescaptedBanner = i18n.tr("{0} arrears as of {1}", getMetadata().category().getValue(), DATE_FORMAT.format(getStatusDate()));
            titleBannerLabel.setHTML(new SafeHtmlBuilder().appendEscaped(unescaptedBanner).toSafeHtml());
        }
    }

    public ArrearsStatusGadget() {
        super(ArrearsStatusGadgetMetadata.class);
    }

    @Override
    protected GadgetInstanceBase<ArrearsStatusGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsStatusGadgetInstance(gadgetMetadata);
    }

}
