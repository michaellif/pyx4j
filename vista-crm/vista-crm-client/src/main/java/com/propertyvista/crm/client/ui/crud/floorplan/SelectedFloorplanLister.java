/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.property.asset.Floorplan;

public class SelectedFloorplanLister extends ListerBase<Floorplan> {

    public SelectedFloorplanLister() {
        super(Floorplan.class, null, true);
        getDataTablePanel().getDataTable().setMarkSelectedRow(true);
        getDataTablePanel().setPageSize(8);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Floorplan>> columnDescriptors, Floorplan proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorCount()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bedrooms()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bathrooms()));
    }

    @Override
    protected void fillAvailableColumnDescriptors(List<ColumnDescriptor<Floorplan>> columnDescriptors, Floorplan proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorCount()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.dens()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bathrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.halfBath()));
    }
}
