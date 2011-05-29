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
package com.propertyvista.crm.client.ui.listers;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.Inquiry;

public class InquiryLister extends ListerBase<Inquiry> {

    public InquiryLister() {
        super(Inquiry.class, new CrmSiteMap.Viewers.Inquiry());
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Inquiry>> columnDescriptors, Inquiry proto) {
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.name()));
//        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.price()));
    }
}
