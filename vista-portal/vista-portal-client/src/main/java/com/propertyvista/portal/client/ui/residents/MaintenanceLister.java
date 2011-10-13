/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;

public class MaintenanceLister extends ListerBase<MaintenanceRequestDTO> {

    public MaintenanceLister() {
        super(MaintenanceRequestDTO.class);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<MaintenanceRequestDTO>> columnDescriptors, MaintenanceRequestDTO proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.maintenanceType()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.problemDescription()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.updated()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.status()));

    }

}
