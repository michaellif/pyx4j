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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.LeaseDTO;

public class LeaseLister extends ListerBase<LeaseDTO> {

    public LeaseLister() {
        super(LeaseDTO.class, CrmSiteMap.Tenants.Lease.class);

        List<ColumnDescriptor<LeaseDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<LeaseDTO>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().unit().belongsTo().propertyCode(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().unit(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().leaseID(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().type(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().leaseFrom(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().leaseTo(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().expectedMoveIn(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().expectedMoveOut(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().actualMoveIn(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().actualMoveOut(), false));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().moveOutNotice(), false));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().status(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().signDate(), true));
        setColumnDescriptors(columnDescriptors);
    }

}
