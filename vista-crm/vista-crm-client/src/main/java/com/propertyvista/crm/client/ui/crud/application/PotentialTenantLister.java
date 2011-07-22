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
package com.propertyvista.crm.client.ui.crud.application;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.site.client.ui.crud.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.TenantInLease;

public class PotentialTenantLister extends ListerBase<TenantInLease> {

    public PotentialTenantLister() {
        super(TenantInLease.class, CrmSiteMap.Tenants.PotentialTenant.class);
        setFiltersVisible(false);
    }

    public PotentialTenantLister(boolean readOnly) {
        super(TenantInLease.class, CrmSiteMap.Tenants.PotentialTenant.class, readOnly);
        setFiltersVisible(false);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<TenantInLease>> columnDescriptors, TenantInLease proto) {
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().name()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.person().birthDate()));
    }
}
