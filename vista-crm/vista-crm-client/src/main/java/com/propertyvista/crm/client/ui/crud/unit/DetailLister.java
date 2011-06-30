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

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class DetailLister extends ListerBase<AptUnitItem> {

    public DetailLister() {
        super(AptUnitItem.class, CrmSiteMap.Properties.UnitItem.class);
        setFiltersVisible(false);
    }

    public DetailLister(boolean readOnly) {
        super(AptUnitItem.class, CrmSiteMap.Properties.UnitItem.class, readOnly);
        setFiltersVisible(false);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<AptUnitItem>> columnDescriptors, AptUnitItem proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.description()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.conditionNotes()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.wallColour()));
    }
}
