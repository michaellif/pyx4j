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
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.crm.client.ui.gadgets.ListerGadgetBase;
import com.propertyvista.crm.client.ui.gadgets.building.IBuildingGadget;
import com.propertyvista.crm.client.ui.gadgets.vacancyreport.util.Tuple;
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

    protected abstract Arrears getArrearsMemberProto();

    private List<ColumnDescriptor<MockupArrearsState>> getArrearsStatusColumns() {
        List<IObject<?>> members = new ArrayList<IObject<?>>();
        for (String memberName : getArrearsMemberProto().getEntityMeta().getMemberNames()) {
            members.add(getArrearsMemberProto().getMember(memberName));
        }
        return columnDescriptors(members);
    }

    //@formatter:off   
    @Override
    protected List<ColumnDescriptor<MockupArrearsState>> getDefaultColumnDescriptors(MockupArrearsState proto) {
        List<ColumnDescriptor<MockupArrearsState>> cd = columnDescriptorsEx(Arrays.asList(new Object[] {        
                proto.unitNumber(),
                proto.lastName()
        }));
        cd.addAll(getArrearsStatusColumns());
        cd.addAll(columnDescriptorsEx(Arrays.asList(new Object[] {
                proto.legalStatus(),
                proto.lmrUnitRentDifference()                
        })));
        return cd;               
    }

    @Override
    protected List<ColumnDescriptor<MockupArrearsState>> getAvailableColumnDescriptors(MockupArrearsState proto) {        
        List<ColumnDescriptor<MockupArrearsState>> cd = columnDescriptorsEx(Arrays.asList(new Object[] {
                // building info
                proto.propertyCode(),
                proto.buildingName(),
                proto.complexName(),                
                
                proto.unitNumber(),
                
                // tenant info                
                proto.firstName(),
                proto.lastName(),
                
                // arrears status
                proto.legalStatus(),
                proto.lmrUnitRentDifference()                
        }));        
        cd.addAll(getArrearsStatusColumns());
        cd.addAll(columnDescriptorsEx(Arrays.asList(new Object[] {                
                // address
                proto.streetNumber(),
                proto.streetName(),
                proto.streetType(),
                proto.city(),
                Tuple.cons(proto.province(), i18n.tr("Province")),
                Tuple.cons(proto.country().name(), i18n.tr("Country")),
                
                // common tabular gadget stuff
                Tuple.cons(proto.common().propertyManger().name(), i18n.tr("Property Manager")),
                Tuple.cons(proto.common().owner().company().name(), i18n.tr("Owner")),
                proto.common().region(),
                //Tuple.cons(proto.common().portfolio().name(), i18n.tr("Portfolio"))
                proto.common().portfolio()                
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
                }, new Vector<Key>(filterData.buildings), filterData.toDate == null ? null : new LogicalDate(filterData.toDate),
                        new Vector<Sort>(getSorting()), getPageNumber(), getPageSize());
            }
        } else {
            setPageData(new ArrayList<MockupArrearsState>(1), 0, 0, false);
        }
    }
}
