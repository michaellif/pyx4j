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
package com.propertyvista.crm.client.ui.crud.marketing.lead;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptorFactory;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.CrmSiteMap.Marketing;
import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentLister extends ListerBase<Appointment> {

    public AppointmentLister() {
        super(Appointment.class, Marketing.Appointment.class);

        @SuppressWarnings("unchecked")
        List<ColumnDescriptor<Appointment>> columnDescriptors = Arrays.asList((ColumnDescriptor<Appointment>[]) new ColumnDescriptor[] {
                ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().date(), true),
                ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().time(), true),
                ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().agent(), true),
                ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().phone(), true),
                ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().email(), true),
                ColumnDescriptorFactory.createColumnDescriptor(proto(), proto().status(), true) });

        setColumnDescriptors(columnDescriptors);
    }

}
