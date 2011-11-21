/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2011
 * @author ArtyomB
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
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.client.ui.gadgets.ListerGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.arrears.Arrears;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsState;

public abstract class ArrearsListerGadget extends ListerGadgetBase<MockupArrearsState> implements IBuildingGadget {
    private boolean isLoading = false;

    private final ArrearsReportService service;

    private FilterData filterData;

    public ArrearsListerGadget(GadgetMetadata gmd) {
        super(gmd, MockupArrearsState.class);
        service = GWT.create(ArrearsReportService.class);
    }

    @Override
    protected boolean isFilterRequired() {
        return true;
    }

    protected abstract Arrears arrearsProto();

    //@formatter:off    
    @SuppressWarnings("unchecked")
    @Override
    public List<ColumnDescriptor<MockupArrearsState>> defineColumnDescriptors() {
        return Arrays.asList(
                colh(proto().propertyCode()),
                colh(proto().buildingName()),
                colh(proto().complexName()),
                colv(proto().unitNumber()),
                colh(proto().firstName()),
                colv(proto().lastName()),
                
                // arrears subclass specific stuff goes here here
                colv(arrearsProto().thisMonth()),
                colv(arrearsProto().monthAgo()),
                colv(arrearsProto().twoMonthsAgo()),
                colv(arrearsProto().threeMonthsAgo()),
                colv(arrearsProto().overFourMonthsAgo()),
                colv(arrearsProto().totalBalance()),
                colv(arrearsProto().prepayments()),
                                
                colv(proto().legalStatus()),
                colv(proto().lmrUnitRentDifference()),
                
                // address
                colh(proto().streetNumber()),
                colh(proto().streetName()),
                colh(proto().streetType()),
                colh(proto().city()),
                colh(proto().province(), i18n.tr("Province")),
                colh(proto().country().name(), i18n.tr("Country")),
                
                // common tabular gadget stuff
                colh(proto().common().propertyManger().name(), i18n.tr("Property Manager")),
                colh(proto().common().owner().company().name(), i18n.tr("Owner")),
                colh(proto().common().region()),
                colh(proto().common().portfolio().name(), i18n.tr("Portfolio"))
        );
    }                    
    //@formatter:on

    @Override
    public Widget asWidget() {
        return getListerWidget().asWidget();
    }

    @Override
    public void setFiltering(FilterData filterData) {
        this.filterData = filterData;
        populatePage(0);
    }

    @Override
    public void populatePage(int pageNumber) {
        // TODO move isRunning() check to the base class
        // TODO isLoading is for avoiding redundant reloads, investigate why they are happening and then remove it if it's possible
        if (filterData != null) {
            if (isRunning() & !isLoading) {
                final int p = pageNumber;
                isLoading = true;
                service.arrearsList(new AsyncCallback<EntitySearchResult<MockupArrearsState>>() {
                    @Override
                    public void onSuccess(EntitySearchResult<MockupArrearsState> result) {
                        setPageData(result.getData(), p, result.getTotalRows(), result.hasMoreData());
                        isLoading = false;
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        isLoading = false;
                        throw new Error(caught);
                    }
                }, getCustomCriteria(), new Vector<Key>(filterData.buildings), filterData.toDate == null ? null : new LogicalDate(filterData.toDate),
                        new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            }
        } else {
            setPageData(new ArrayList<MockupArrearsState>(1), 0, 0, false);
        }
    }

    private Vector<Criterion> getCustomCriteria() {
        Vector<Criterion> customCriteria = new Vector<Criterion>();
        for (DataTableFilterData filterData : getListerFilterData()) {
            if (filterData.isFilterOK()) {
                switch (filterData.getOperand()) {
                case is:
                    customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.EQUAL, filterData.getValue()));
                    break;
                case isNot:
                    customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.NOT_EQUAL, filterData.getValue()));
                    break;
                case like:
                    customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.RDB_LIKE, filterData.getValue()));
                    break;
                case greaterThan:
                    customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.GREATER_THAN, filterData.getValue()));
                    break;
                case lessThan:
                    customCriteria.add(new PropertyCriterion(filterData.getMemberPath(), Restriction.LESS_THAN, filterData.getValue()));
                    break;
                }
            }
        }
        return customCriteria;
    }

}
