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
package com.propertyvista.crm.client.ui.crud.building.mech;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorLister extends ListerBase<ElevatorDTO> {

    public ElevatorLister() {
        super(ElevatorDTO.class, CrmSiteMap.Properties.Elevator.class);
        getDataTablePanel().setFilterEnabled(false);
    }

    public ElevatorLister(boolean readOnly) {
        super(ElevatorDTO.class, CrmSiteMap.Properties.Elevator.class, readOnly);
        getDataTablePanel().setFilterEnabled(false);
    }

    @Override
    protected List<ColumnDescriptor<ElevatorDTO>> getDefaultColumnDescriptors(ElevatorDTO proto) {
        List<ColumnDescriptor<ElevatorDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<ElevatorDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.description()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.make()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.model()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.build()));
        return columnDescriptors;
    }

    @Override
    protected List<ColumnDescriptor<ElevatorDTO>> getAvailableColumnDescriptors(ElevatorDTO proto) {
        List<ColumnDescriptor<ElevatorDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<ElevatorDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.description()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.make()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.model()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.build()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.license().number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.license().expiration()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.license().renewal()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.warranty().type()));
        return columnDescriptors;
    }
}
