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
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.client.ui.gadgets.ListerGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.Tuple;
import com.propertyvista.crm.rpc.services.dashboard.gadgets.ArrearsReportService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.arrears.Arrears;
import com.propertyvista.domain.dashboard.gadgets.arrears.MockupArrearsCompilation;

public abstract class ArrearsListerGadget extends ListerGadgetBase<MockupArrearsCompilation> implements IBuildingGadget {
    private boolean isLoading = false;

    private final ArrearsReportService service;

    private final FilterData filterData;

    public ArrearsListerGadget(GadgetMetadata gmd) {
        super(gmd, MockupArrearsCompilation.class);
        service = GWT.create(ArrearsReportService.class);
        filterData = new FilterData();
        filterData.toDate = new Date();
    }

    protected abstract Arrears getArrearsMemberProto();

    private List<ColumnDescriptor<MockupArrearsCompilation>> getArrearsColumns() {
        List<IObject<?>> members = new ArrayList<IObject<?>>();
        for (String memberName : getArrearsMemberProto().getEntityMeta().getMemberNames()) {
            members.add(getArrearsMemberProto().getMember(memberName));
        }
        return columnDescriptors(members);
    }

    //@formatter:off   
    @Override
    protected List<ColumnDescriptor<MockupArrearsCompilation>> getDefaultColumnDescriptors(MockupArrearsCompilation proto) {
        List<ColumnDescriptor<MockupArrearsCompilation>> cd = columnDescriptorsEx(Arrays.asList(new Object[] {        
                proto.unitNumber(),
                proto.lastName()
        }));
        cd.addAll(getArrearsColumns());
        return cd;               
    }

    @Override
    protected List<ColumnDescriptor<MockupArrearsCompilation>> getAvailableColumnDescriptors(MockupArrearsCompilation proto) {        
        List<ColumnDescriptor<MockupArrearsCompilation>> cd = columnDescriptorsEx(Arrays.asList(new Object[] {
                // building info
                proto.propertyCode(),
                proto.buildingName(),
                proto.complexName(),                
                
                proto.unitNumber(),
                
                // tenant info                
                proto.firstName(),
                proto.lastName(),                                
        }));
        cd.addAll(getArrearsColumns());
        cd.addAll(columnDescriptorsEx(Arrays.asList(new Object[] {                
                // address
                proto.streetNumber(),
                proto.streetName(),
                proto.streetType(),
                proto.city(),
                Tuple.cons(proto.province(), i18n.tr("Province")),
                Tuple.cons(proto.country().name(), i18n.tr("Country")),
                
                // common gadget stuff
                Tuple.cons(proto.common().propertyManger().name(), i18n.tr("Property Manager")),
                Tuple.cons(proto.common().owner().company().name(), i18n.tr("Owner")),
                proto.common().region(),
                Tuple.cons(proto.common().portfolio().name(), i18n.tr("Portfolio"))                
        })));               
        return cd;
    }
    //@formatter:on

    @Override
    public Widget asWidget() {
        return getListerWidget().asWidget();
    }

    @Override
    public void setFiltering(FilterData filterData) {
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
                service.arrearsList(new AsyncCallback<EntitySearchResult<MockupArrearsCompilation>>() {
                    @Override
                    public void onSuccess(EntitySearchResult<MockupArrearsCompilation> result) {
                        setPageData(result.getData(), p, result.getTotalRows(), result.hasMoreData());
                        isLoading = false;
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        isLoading = false;
                        throw new Error(caught);
                    }
                }, new Vector<Key>(filterData.buildings), new LogicalDate(filterData.toDate), new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            }
        } else {
            setPageData(new ArrayList<MockupArrearsCompilation>(1), 0, 0, false);
        }
    }
}
