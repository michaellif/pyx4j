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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;
import com.propertyvista.crm.rpc.services.BuildingCrudServiceOld;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingListerGadget extends ListerGadgetBase<Building> {

    public BuildingListerGadget(GadgetMetadata gmd) {
        super(gmd, Building.class);
    }

    @Override
    protected void selfInit(GadgetMetadata gmd) {
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue(i18n.tr("Building Lister"));
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Building>> columnDescriptors, Building proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketing().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.contacts().website()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.contacts().email().emailAddress()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
    }

    @Override
    public void populateData(final int pageNumber) {
        BuildingCrudServiceOld bcs = GWT.create(BuildingCrudServiceOld.class);
        if (bcs != null) {
            EntitySearchCriteria<Building> criteria = new EntitySearchCriteria<Building>(Building.class);
            criteria.setPageSize(getListPanel().getPageSize());
            criteria.setPageNumber(pageNumber);

            bcs.search(new AsyncCallback<EntitySearchResult<Building>>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(EntitySearchResult<Building> result) {
                    BuildingListerGadget.this.getListPanel().populateData(result.getData(), pageNumber, result.hasMoreData());
                }
            }, criteria);
        }
    }
}
