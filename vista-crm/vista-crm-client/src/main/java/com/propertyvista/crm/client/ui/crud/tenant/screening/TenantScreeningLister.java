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
package com.propertyvista.crm.client.ui.crud.tenant.screening;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.TenantScreening;

public class TenantScreeningLister extends ListerBase<TenantScreening> {

    public TenantScreeningLister() {
        super(TenantScreening.class, CrmSiteMap.Tenants.TenantScreening.class, false, true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);

        List<ColumnDescriptor<TenantScreening>> columnDescriptors = new ArrayList<ColumnDescriptor<TenantScreening>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().screeningDate(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().driversLicense(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().secureIdentifier(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().notCanadianCitizen(), true));

        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().equifaxApproval().percenrtageApproved(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().equifaxApproval().suggestedDecision(), true));

        setColumnDescriptors(columnDescriptors);

    }

}
