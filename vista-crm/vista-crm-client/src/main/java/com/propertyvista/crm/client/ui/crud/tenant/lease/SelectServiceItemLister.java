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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.financial.offering.ServiceItem;

public class SelectServiceItemLister extends ListerBase<ServiceItem> {

    public SelectServiceItemLister() {
        super(ServiceItem.class);
        getDataTablePanel().getDataTable().setMarkSelectedRow(true);
        getDataTablePanel().setPageSize(5);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().type()).build(),
            new MemberColumnDescriptor.Builder(proto().price()).build(),
            new MemberColumnDescriptor.Builder(proto().description()).build()
        );//@formatter:on
    }
}
