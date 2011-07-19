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
package com.propertyvista.crm.client.ui.crud.application;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.ListerBase;

import com.propertyvista.dto.AptUnitDTO;

public class SelectedUnitLister extends ListerBase<AptUnitDTO> {

    public SelectedUnitLister() {
        super(AptUnitDTO.class, null, true);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<AptUnitDTO>> columnDescriptors, AptUnitDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().floor()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().number()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().bedrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().bathrooms()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().area()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().areaUnits()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.info().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.marketing().name()));
    }
}
