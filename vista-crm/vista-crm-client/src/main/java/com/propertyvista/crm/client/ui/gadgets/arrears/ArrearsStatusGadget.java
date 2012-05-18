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
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.LeaseArrearsSnapshotDTO;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsStatusGadget extends AbstractGadget<ArrearsGadgetMeta> {

    private final static I18n i18n = I18n.get(ArrearsStatusGadget.class);

    private static class ArrearsStatusGadgetInstance extends ListerGadgetInstanceBase<LeaseArrearsSnapshotDTO, ArrearsGadgetMeta> {

        private final ArrearsReportService service;

        private FormFlexPanel contentPanel;

        private CDatePicker asOf;

        public ArrearsStatusGadgetInstance(GadgetMetadata gmd) {
            super(gmd, LeaseArrearsSnapshotDTO.class, ArrearsGadgetMeta.class);
            service = GWT.<ArrearsReportService> create(ArrearsReportService.class);
        }

        @Override
        protected boolean isFilterRequired() {
            return false;
        }

        @Override
        protected ArrearsGadgetMeta createDefaultSettings(Class<ArrearsGadgetMeta> metadataClass) {
            return super.createDefaultSettings(metadataClass);
        }

        @Override
        public List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().propertyCode()).visible(true).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().info().name()).title(i18n.tr("Building")).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().info().address().streetNumber()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().info().address().streetName()).visible(false).build(),                    
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().info().address().province().name()).visible(false).title(i18n.tr("Province")).build(),                    
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().info().address().country().name()).visible(false).title(i18n.tr("Country")).build(),                    
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().belongsTo().complex().name()).visible(false).title(i18n.tr("Complex")).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().unit().info().number()).title(i18n.tr("Unit")).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().leaseId()).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().leaseFrom()).build(),
                    new MemberColumnDescriptor.Builder(proto().billingAccount().lease().leaseTo()).build(),
                    
                    // arrears
                    new MemberColumnDescriptor.Builder(proto().selectedBuckets().bucketCurrent()).build(),
                    new MemberColumnDescriptor.Builder(proto().selectedBuckets().bucket30()).build(),
                    new MemberColumnDescriptor.Builder(proto().selectedBuckets().bucket60()).build(),
                    new MemberColumnDescriptor.Builder(proto().selectedBuckets().bucket90()).build(),
                    new MemberColumnDescriptor.Builder(proto().selectedBuckets().bucketOver90()).build(),
                    
                    new MemberColumnDescriptor.Builder(proto().arrearsAmount()).build(),
                    new MemberColumnDescriptor.Builder(proto().creditAmount()).build(),                    
                    new MemberColumnDescriptor.Builder(proto().totalBalance()).build(),
                    new MemberColumnDescriptor.Builder(proto().lmrToUnitRentDifference()).build()
                    
            );//@formatter:on
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
        public void populatePage(final int pageNumber) {
            if (containerBoard.getSelectedBuildings() == null) {
                setAsOfValue(getStatusDate());
                setPageData(new Vector<LeaseArrearsSnapshotDTO>(), 0, 0, false);
                populateSucceded();
                return;
            } else {
                final int page = pageNumber;

                service.arrearsList(new DefaultAsyncCallback<EntitySearchResult<LeaseArrearsSnapshotDTO>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<LeaseArrearsSnapshotDTO> result) {
                        setPageData(result.getData(), page, result.getTotalRows(), result.hasMoreData());
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }

                }, new Vector<Criterion>(), new Vector<Building>(containerBoard.getSelectedBuildings()), getStatusDate(), new Vector<Sort>(getSorting()),
                        pageNumber, getMetadata().pageSize().getValue());
            }

        }

        @Override
        public Widget initContentPanel() {
            contentPanel = new FormFlexPanel();
            String selectedCategory = (getMetadata().category().getValue() != null ? getMetadata().category().getValue() : DebitType.total).toString();
            contentPanel.setH1(0, 0, 1, selectedCategory);
            contentPanel.setWidget(1, 0, initAsOfBannerPanel());
            contentPanel.setWidget(2, 0, initListerWidget());
            return contentPanel;
        }

        private Widget initAsOfBannerPanel() {
            HorizontalPanel asForBannerPanel = new HorizontalPanel();
            asForBannerPanel.setWidth("100%");
            asForBannerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

            asOf = new CDatePicker();
            asOf.setValue(getStatusDate());
            asOf.setViewable(true);

            asForBannerPanel.add(asOf);
            return asForBannerPanel.asWidget();
        }

        private LogicalDate getStatusDate() {
            return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate();
        }

        private void setAsOfValue(LogicalDate asOf) {
            this.asOf.setValue(asOf);
        }
    }

    public ArrearsStatusGadget() {
        super(ArrearsGadgetMeta.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Arrears.toString());
    }

    @Override
    public boolean isBuildingGadget() {
        return true;
    }

    @Override
    protected GadgetInstanceBase<ArrearsGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsStatusGadgetInstance(gadgetMetadata);
    }

}
