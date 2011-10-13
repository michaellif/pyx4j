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
package com.propertyvista.crm.client.ui.crud.tenant.lead;

import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentLister extends ListerBase<Appointment> {

    public AppointmentLister() {
        super(Appointment.class, CrmSiteMap.Tenants.Appointment.class);
    }

    @Override
    protected void fillDefaultColumnDescriptors(List<ColumnDescriptor<Appointment>> columnDescriptors, Appointment proto) {
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.date()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.time()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.agent()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.phone()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.email()));
        columnDescriptors.add(ColumnDescriptorFactory.createColumnDescriptor(proto, proto.status()));
    }
}
