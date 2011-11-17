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
package com.propertyvista.crm.client.ui.report;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class ReportLister extends ListerBase<DashboardMetadata> {

    public ReportLister() {
        super(DashboardMetadata.class, CrmSiteMap.Report.Edit.class);
        getDataTablePanel().setFiltersVisible(false);
    }

    public ReportLister(boolean readOnly) {
        super(DashboardMetadata.class, CrmSiteMap.Report.Edit.class, readOnly);
        getDataTablePanel().setFiltersVisible(false);
    }

    @Override
    protected List<ColumnDescriptor<DashboardMetadata>> getDefaultColumnDescriptors(DashboardMetadata proto) {
        List<ColumnDescriptor<DashboardMetadata>> columnDescriptors = new ArrayList<ColumnDescriptor<DashboardMetadata>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.description()));
        return columnDescriptors;
    }
}
