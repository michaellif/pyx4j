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

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.TenantScreening;

public class TenantScreeningLister extends ListerBase<TenantScreening> {

    public TenantScreeningLister() {
        super(TenantScreening.class, CrmSiteMap.Tenants.TenantScreening.class);
        getListPanel().getDataTable().setHasCheckboxColumn(false);
    }

    public TenantScreeningLister(boolean readOnly) {
        super(TenantScreening.class, CrmSiteMap.Tenants.TenantScreening.class, readOnly);
        getListPanel().getDataTable().setHasCheckboxColumn(false);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<TenantScreening>> columnDescriptors, TenantScreening proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.screeningDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.driversLicense()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.secureIdentifier()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.notCanadianCitizen()));
    }
}
