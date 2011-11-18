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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.TenantDTO;

public class TenantLister extends ListerBase<TenantDTO> {

    public TenantLister() {
        super(TenantDTO.class, CrmSiteMap.Tenants.Tenant.class);
    }

    @Override
    protected List<ColumnDescriptor<TenantDTO>> getDefaultColumnDescriptors(TenantDTO proto) {
        List<ColumnDescriptor<TenantDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<TenantDTO>>();

        // TODO: currently we use just person tenant, so we'll display more data for them: 
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.displayName()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.id()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().birthDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().homePhone()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().mobilePhone()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().workPhone()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().email().address()));
        return columnDescriptors;
    }
}
