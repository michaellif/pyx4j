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
package com.propertyvista.crm.client.ui.crud.tenant.application;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.TenantInLeaseDTO;

public class TenantInLeaseLister extends ListerBase<TenantInLeaseDTO> {

    public TenantInLeaseLister() {
        super(TenantInLeaseDTO.class, CrmSiteMap.Tenants.Tenant.class, true);

        List<ColumnDescriptor<TenantInLeaseDTO>> columnDescriptors = new ArrayList<ColumnDescriptor<TenantInLeaseDTO>>();

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().id(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().tenant().person().name(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().tenant().person().sex(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().tenant().person().birthDate(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().role(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().incomeSource(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().application().status(), true));
        setColumnDescriptors(columnDescriptors);

    }

}
