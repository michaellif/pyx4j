/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.util.ColumnDescriptorConverter;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.property.asset.building.Building;

public class ArrearsSummaryGadget extends AbstractGadget<ArrearsSummaryGadgetMetadata> {

    public class ArrearsSummaryGadgetInstance extends ListerGadgetInstanceBase<AgingBuckets, ArrearsSummaryGadgetMetadata> {

        private final ArrearsReportService service;

        public ArrearsSummaryGadgetInstance(GadgetMetadata gmd) {
            super(gmd, ArrearsSummaryGadgetMetadata.class, null, AgingBuckets.class, false);
            service = GWT.<ArrearsReportService> create(ArrearsReportService.class);
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
        protected ArrearsSummaryGadgetMetadata createDefaultSettings(Class<ArrearsSummaryGadgetMetadata> metadataClass) {
            ArrearsSummaryGadgetMetadata settings = super.createDefaultSettings(metadataClass);
            AgingBuckets proto = EntityFactory.getEntityPrototype(AgingBuckets.class);
            settings.columnDescriptors().addAll(ColumnDescriptorConverter.asColumnDesciptorEntityList(Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto.bucketCurrent()).build(),
                    new MemberColumnDescriptor.Builder(proto.bucket30()).build(),
                    new MemberColumnDescriptor.Builder(proto.bucket60()).build(),
                    new MemberColumnDescriptor.Builder(proto.bucket90()).build(),
                    new MemberColumnDescriptor.Builder(proto.bucketOver90()).build(),
                    new MemberColumnDescriptor.Builder(proto.arrearsAmount()).build()
//                    new MemberColumnDescriptor.Builder(proto.creditAmount()).build()
//                    new MemberColumnDescriptor.Builder(proto.totalBalance()).build()
            )));//@formatter:on
            return settings;
        }

        @Override
        protected Widget initContentPanel() {
            return initListerWidget();
        }

        @Override
        protected void populatePage(int pageNumber) {
            if (containerBoard.getSelectedBuildingsStubs() == null) {
                setPageData(new Vector<AgingBuckets>(), 0, 0, false);
                populateSucceded();
                return;
            } else {
                service.summary(new AsyncCallback<EntitySearchResult<AgingBuckets>>() {

                    @Override
                    public void onSuccess(EntitySearchResult<AgingBuckets> result) {
                        setPageData(result.getData(), 1, 1, false);
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                }, new Vector<Building>(containerBoard.getSelectedBuildingsStubs()), getStatusDate(), new Vector<Sort>(getListerSortingCriteria()));
            }
        }

        private LogicalDate getStatusDate() {
            return getMetadata().customizeDate().isBooleanTrue() ? getMetadata().asOf().getValue() : new LogicalDate();
        }
    }

    public ArrearsSummaryGadget() {
        super(ArrearsSummaryGadgetMetadata.class);
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
    protected GadgetInstanceBase<ArrearsSummaryGadgetMetadata> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsSummaryGadgetInstance(gadgetMetadata);
    }

}
