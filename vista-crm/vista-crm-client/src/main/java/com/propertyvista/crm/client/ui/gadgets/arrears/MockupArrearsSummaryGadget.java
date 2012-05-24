/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-11-16
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.arrears;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.common.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IBuildingBoardGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.common.ListerGadgetInstanceBase;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.MockupArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.MockArrearsSummaryGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class MockupArrearsSummaryGadget extends AbstractGadget<MockArrearsSummaryGadgetMeta> {

    private static final I18n i18n = I18n.get(MockupArrearsSummaryGadget.class);

    private static class MockupArrearsSummaryGadgetImpl extends ListerGadgetInstanceBase<MockupArrearsSummary, MockArrearsSummaryGadgetMeta> implements
            IBuildingBoardGadgetInstance {

        private static final I18n i18n = I18n.get(MockupArrearsSummaryGadgetImpl.class);

        MockupArrearsReportService service;

        private List<Key> buildings;

        public MockupArrearsSummaryGadgetImpl(GadgetMetadata gmd) {
            super(gmd, MockupArrearsSummary.class, MockArrearsSummaryGadgetMeta.class);
            service = GWT.create(MockupArrearsReportService.class);
        }

        @Override
        public List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().thisMonth()).title(i18n.tr("Total this month")).build(),
                    new MemberColumnDescriptor.Builder(proto().monthAgo()).title(i18n.tr("Total 0-30")).build(),
                    new MemberColumnDescriptor.Builder(proto().twoMonthsAgo()).title(i18n.tr("Total 30-60")).build(),
                    new MemberColumnDescriptor.Builder(proto().threeMonthsAgo()).title(i18n.tr("Total 60-90")).build(),
                    new MemberColumnDescriptor.Builder(proto().overFourMonthsAgo()).title(i18n.tr("Total over 90")).build(),
                    new MemberColumnDescriptor.Builder(proto().arBalance()).title(i18n.tr("Total AR Balance")).build()
           );//@formatter:on                   
        }

        @Override
        public void populatePage(final int pageNumber) {
            if (buildings != null) {
                service.summary(new AsyncCallback<EntitySearchResult<MockupArrearsSummary>>() {
                    @Override
                    public void onSuccess(EntitySearchResult<MockupArrearsSummary> result) {
                        setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                }, new Vector<Key>(buildings), getStatusDate(), new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            } else {
                setPageData(new ArrayList<MockupArrearsSummary>(1), 0, 0, false);
                populateSucceded();
            }
        }

        private LogicalDate getStatusDate() {
            return new LogicalDate();
        }

        @Override
        public Widget initContentPanel() {
            return initListerWidget();
        }

        @Override
        protected boolean isFilterRequired() {
            return false;
        }

    }

    public MockupArrearsSummaryGadget() {
        super(MockArrearsSummaryGadgetMeta.class);
    }

    @Override
    public String getDescription() {
        return i18n.tr("Shows a short summary of the total arrears");
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
    protected GadgetInstanceBase<MockArrearsSummaryGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new MockupArrearsSummaryGadgetImpl(gadgetMetadata);
    }
}