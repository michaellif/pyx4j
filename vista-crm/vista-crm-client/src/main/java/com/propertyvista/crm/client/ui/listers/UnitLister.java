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
package com.propertyvista.crm.client.ui.listers;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.AptUnit;

public class UnitLister extends ListerBase<AptUnit> {

    public UnitLister() {
        super(AptUnit.class, new CrmSiteMap.Viewers.Unit());
//        super(AptUnit.class, new CrmSiteMap.Editors.Unit());
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<AptUnit>> columnDescriptors, AptUnit proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketingName()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.economicStatus()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.economicStatusDescription()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.building().info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.bathrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.currentOccupancies()));
    }
}
