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
package com.propertyvista.crm.client.ui.crud.marketing.inquiry;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.tenant.Inquiry;

public class InquiryLister extends ListerBase<Inquiry> {

    public InquiryLister() {
        super(Inquiry.class, Marketing.Inquiry.class);
    }

    @Override
    protected List<ColumnDescriptor<Inquiry>> getDefaultColumnDescriptors(Inquiry proto) {
        List<ColumnDescriptor<Inquiry>> columnDescriptors = new ArrayList<ColumnDescriptor<Inquiry>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.email()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.building()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplan()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.leaseTerm()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.movingDate()));
        return columnDescriptors;
    }

    @Override
    protected List<ColumnDescriptor<Inquiry>> getAvailableColumnDescriptors(Inquiry proto) {
        List<ColumnDescriptor<Inquiry>> columnDescriptors = new ArrayList<ColumnDescriptor<Inquiry>>();
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.email()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.building()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.floorplan()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.leaseTerm()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.movingDate()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.appointmentDate1()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.appointmentDate2()));
        return columnDescriptors;
    }
}
