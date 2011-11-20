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
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.client.ui.gadgets.ListerGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.Tuple;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.domain.dashboard.gadgets.arrears.ArrearsSummary;

public class ArrearsSummaryGadget extends ListerGadgetBase<ArrearsSummary> implements IBuildingGadget {
    FilterData filterData = null;

    ArrearsReportService service;

    private boolean isLoading = false;

    public ArrearsSummaryGadget(GadgetMetadata gmd) {
        super(gmd, ArrearsSummary.class);
        service = GWT.create(ArrearsReportService.class);

        setColumnDescriptors(columnDescriptorsEx(Arrays.asList(new Object[] {

        Tuple.cons(proto().thisMonth(), i18n.tr("Total this month")),

        Tuple.cons(proto().monthAgo(), i18n.tr("Total 0-30")),

        Tuple.cons(proto().twoMonthsAgo(), i18n.tr("Total 30-60")),

        Tuple.cons(proto().threeMonthsAgo(), i18n.tr("Total 60-90")),

        Tuple.cons(proto().overFourMonthsAgo(), i18n.tr("Total over 90")),

        Tuple.cons(proto().arBalance(), i18n.tr("Total AR Balance")) }), true));

    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.ArrearsSummaryGadget);
        gmd.name().setValue(GadgetType.ArrearsSummaryGadget.toString());
    }

    @Override
    public void setFiltering(FilterData filterData) {
        this.filterData = filterData;
        populatePage(0);
    }

    @Override
    public void populatePage(int pageNumber) {
        if (filterData != null) {
            if (isRunning() & !isLoading) {
                final int p = pageNumber;
                isLoading = true;
                service.summary(new AsyncCallback<EntitySearchResult<ArrearsSummary>>() {
                    @Override
                    public void onSuccess(EntitySearchResult<ArrearsSummary> result) {
                        setPageData(result.getData(), p, result.getTotalRows(), result.hasMoreData());
                        isLoading = false;
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        isLoading = false;
                        throw new Error(caught);
                    }
                }, new Vector<Key>(filterData.buildings), filterData.toDate == null ? null : new LogicalDate(filterData.toDate),
                        new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            }
        } else {
            setPageData(new ArrayList<ArrearsSummary>(1), 0, 0, false);
        }
    }

    @Override
    public Widget asWidget() {
        return getListerWidget().asWidget();
    }

    @Override
    protected boolean isFilterRequired() {
        // TODO Auto-generated method stub
        return false;
    }

}
