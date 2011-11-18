/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-16
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanLister extends ListerBase<FloorplanDTO> {

    public FloorplanLister() {
        super(FloorplanDTO.class, CrmSiteMap.Properties.Floorplan.class);
        getDataTablePanel().setFilterEnabled(false);
    }

    public FloorplanLister(boolean readOnly) {
        super(FloorplanDTO.class, CrmSiteMap.Properties.Floorplan.class, readOnly);
        getDataTablePanel().setFilterEnabled(false);
    }

    @Override
    protected List<ColumnDescriptor<FloorplanDTO>> getDefaultColumnDescriptors(FloorplanDTO proto) {
        List<ColumnDescriptor<FloorplanDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<FloorplanDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorCount()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.dens()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bathrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.counters()._unitCount()));
        return columnDescriptors;
    }
}
