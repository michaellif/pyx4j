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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.misc.VistaTODO;

public class TenantInLeaseLister extends ListerBase<TenantInLeaseDTO> {

    public TenantInLeaseLister() {
        super(TenantInLeaseDTO.class, CrmSiteMap.Tenants.Tenant.class, false, false);

        setColumnDescriptors(new MemberColumnDescriptor.Builder(proto().tenant().id(), true).build(),

        new MemberColumnDescriptor.Builder(proto().tenant().person().name(), true).sortable(!VistaTODO.entityAsStringQueryCriteria).build(),

        new MemberColumnDescriptor.Builder(proto().tenant().person().sex(), true).build(),

        new MemberColumnDescriptor.Builder(proto().tenant().person().birthDate(), true).build(),

        new MemberColumnDescriptor.Builder(proto().role(), true).build(),

        new MemberColumnDescriptor.Builder(proto().incomeSource(), true).sortable(!VistaTODO.complextQueryCriteria).build(),

        new MemberColumnDescriptor.Builder(proto().equifaxApproval().percenrtageApproved(), true).sortable(false).build());
    }

    @Override
    protected void onItemSelect(TenantInLeaseDTO item) {
        getPresenter().view(CrmSiteMap.Tenants.Tenant.class, item.tenant().getPrimaryKey());
    }
}
