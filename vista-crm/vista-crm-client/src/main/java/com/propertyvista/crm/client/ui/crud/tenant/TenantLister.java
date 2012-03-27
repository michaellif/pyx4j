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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor.Builder;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.TenantDTO;

public class TenantLister extends ListerBase<TenantDTO> {

    public TenantLister() {
        super(TenantDTO.class, CrmSiteMap.Tenants.Tenant.class, false, true);

        // TODO: currently we use just person tenant, so we'll display more data for them:
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().displayName()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().type()));

        setColumnDescriptors(//@formatter:off
                new Builder(proto().id(), false).build(),
                new Builder(proto().person().name()).searchable(false).build(),
                new Builder(proto().person().name().firstName(), false).build(),
                new Builder(proto().person().name().lastName(), false).build(),
                new Builder(proto().person().sex()).build(),
                new Builder(proto().person().birthDate()).build(),
                new Builder(proto().person().homePhone()).build(),
                new Builder(proto().person().mobilePhone(), false).build(),
                new Builder(proto().person().workPhone(), false).build(),
                new Builder(proto().person().email()).build(),
                new Builder(proto().lease()).sortable(false).build()
            ); // @formatter:on
    }
}
