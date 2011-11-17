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
package com.propertyvista.crm.client.ui.crud.tenant;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.tenant.Tenant;

public class SelectTenantLister extends ListerBase<Tenant> {

    public SelectTenantLister() {
        super(Tenant.class, null, true);
        getDataTablePanel().getDataTable().setMarkSelectedRow(true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);
    }

    @Override
    protected List<ColumnDescriptor<Tenant>> getDefaultColumnDescriptors(Tenant proto) {
        List<ColumnDescriptor<Tenant>> columnDescriptors = new ArrayList<ColumnDescriptor<Tenant>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().birthDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().email()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().homePhone()));
        return columnDescriptors;
    }
}
