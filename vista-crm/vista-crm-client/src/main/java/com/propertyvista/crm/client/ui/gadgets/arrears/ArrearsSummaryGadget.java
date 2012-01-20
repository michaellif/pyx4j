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

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.ListerGadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsSummary;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMeta;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;

public class ArrearsSummaryGadget extends AbstractGadget<ArrearsSummaryGadgetMeta> {
    private static final I18n i18n = I18n.get(ArrearsSummaryGadget.class);

    private static class ArrearsSummaryGadgetImpl extends ListerGadgetInstanceBase<ArrearsSummary, ArrearsSummaryGadgetMeta> implements IBuildingGadget {
        private static final I18n i18n = I18n.get(ArrearsSummaryGadgetImpl.class);

        FilterData filterData = null;

        ArrearsReportService service;

        public ArrearsSummaryGadgetImpl(GadgetMetadata gmd) {
            super(gmd, ArrearsSummary.class, ArrearsSummaryGadgetMeta.class);
            service = GWT.create(ArrearsReportService.class);
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
        public void setFiltering(FilterData filterData) {
            this.filterData = filterData;
            populatePage(0);
        }

        @Override
        public void populatePage(final int pageNumber) {
            if (filterData != null) {
                service.summary(new AsyncCallback<EntitySearchResult<ArrearsSummary>>() {
                    @Override
                    public void onSuccess(EntitySearchResult<ArrearsSummary> result) {
                        setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
                        populateSucceded();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        populateFailed(caught);
                    }
                }, new Vector<Key>(filterData.buildings), filterData.toDate == null ? null : new LogicalDate(filterData.toDate),
                        new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            } else {
                setPageData(new ArrayList<ArrearsSummary>(1), 0, 0, false);
                populateSucceded();
            }
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

    public ArrearsSummaryGadget() {
        super(ArrearsSummaryGadgetMeta.class);
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
    protected GadgetInstanceBase<ArrearsSummaryGadgetMeta> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new ArrearsSummaryGadgetImpl(gadgetMetadata);
    }
}