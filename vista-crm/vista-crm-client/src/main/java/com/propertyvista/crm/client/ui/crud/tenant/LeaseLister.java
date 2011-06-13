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
package com.propertyvista.crm.client.ui.crud.tenant;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseLister extends ListerBase<Lease> {

    public LeaseLister() {
        super(Lease.class, CrmSiteMap.Tenants.Lease.class);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Lease>> columnDescriptors, Lease proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.leaseID()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.leaseFrom()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.leaseTo()));
    }
}
