/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;
import com.propertyvista.dto.BuildingDTO;

public class BuildingListerGadget extends ListerGadgetBase<BuildingDTO> {
    private final AbstractListService<BuildingDTO> service;

    @SuppressWarnings("unchecked")
    public BuildingListerGadget(GadgetMetadata gmd) {
        super(gmd, BuildingDTO.class);
        service = (AbstractListService<BuildingDTO>) GWT.create(BuildingCrudService.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue(GadgetType.BuildingLister.toString());
    }

    @Override
    protected List<ColumnDescriptor<BuildingDTO>> getDefaultColumnDescriptors(BuildingDTO proto) {
        List<ColumnDescriptor<BuildingDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<BuildingDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketing().name()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().city()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
        return columnDescriptors;
    }

    @Override
    protected List<ColumnDescriptor<BuildingDTO>> getAvailableColumnDescriptors(BuildingDTO proto) {
        List<ColumnDescriptor<BuildingDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<BuildingDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.complex()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.propertyManager()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketing().name()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().shape()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().streetName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().city()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
        return columnDescriptors;
    }

    @Override
    public Widget asWidget() {
        return getListerWidget().asWidget();
    }

    @Override
    public void populatePage(final int pageNumber) {
        EntityListCriteria<BuildingDTO> criteria = new EntityListCriteria<BuildingDTO>(BuildingDTO.class);
        criteria.setPageSize(getPageSize());
        criteria.setPageNumber(pageNumber);
        service.list(new AsyncCallback<EntitySearchResult<BuildingDTO>>() {
            @Override
            public void onSuccess(EntitySearchResult<BuildingDTO> result) {
                setPageData(result.getData(), pageNumber, result.getTotalRows(), result.hasMoreData());
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO report error                        
            }
        }, criteria);
    }

    @Override
    protected boolean isFilterRequired() {
        // TODO Auto-generated method stub
        return false;
    }
}
