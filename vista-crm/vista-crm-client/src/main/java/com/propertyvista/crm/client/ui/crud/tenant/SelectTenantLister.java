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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.BasicLister;

import com.propertyvista.domain.tenant.Tenant;

public class SelectTenantLister extends BasicLister<Tenant> {

    public SelectTenantLister() {
        super(Tenant.class);
        setHasCheckboxColumn(true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().person().name()).build(),
            new MemberColumnDescriptor.Builder(proto().person().birthDate()).build(),
            new MemberColumnDescriptor.Builder(proto().person().email()).build(),
            new MemberColumnDescriptor.Builder(proto().person().homePhone()).build()
        );//@formatter:on
    }

}
