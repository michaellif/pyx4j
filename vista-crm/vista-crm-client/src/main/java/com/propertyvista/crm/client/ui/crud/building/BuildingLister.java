/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;

import com.propertyvista.crm.client.ui.crud.ListerBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.BuildingDTO;

public class BuildingLister extends ListerBase<BuildingDTO> {

    public BuildingLister() {
        super(BuildingDTO.class, CrmSiteMap.Properties.Building.class);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<BuildingDTO>> columnDescriptors, BuildingDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketing().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().propertyCode()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.contacts().website()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.contacts().email().address()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().province()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().address().country()));
    }
}
