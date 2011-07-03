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
package com.propertyvista.crm.client.ui.crud.marketing;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.Concession;

public class ConcessionLister extends ListerBase<Concession> {
    public ConcessionLister() {
        super(Concession.class, CrmSiteMap.Properties.Concession.class);
        setFiltersVisible(false);
    }

    public ConcessionLister(boolean readOnly) {
        super(Concession.class, CrmSiteMap.Properties.Concession.class, readOnly);
        setFiltersVisible(false);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Concession>> columnDescriptors, Concession proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.type()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.value()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.condition()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.status()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.start()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.end()));
    }
}
