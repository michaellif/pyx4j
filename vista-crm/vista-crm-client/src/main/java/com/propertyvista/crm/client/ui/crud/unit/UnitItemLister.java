/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-28
 * @author TPRGLET
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.crm.client.ui.crud.unit;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemLister extends ListerBase<AptUnitItem> {

    public UnitItemLister() {
        super(AptUnitItem.class, CrmSiteMap.Properties.UnitItem.class, false, true);
        getDataTablePanel().setFilteringEnabled(false);

        List<ColumnDescriptor> columnDescriptors = new ArrayList<ColumnDescriptor>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().type(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().description(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().flooringType(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().cabinetsType(), true));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().counterTopType(), true));
        setColumnDescriptors(columnDescriptors);
    }

}
