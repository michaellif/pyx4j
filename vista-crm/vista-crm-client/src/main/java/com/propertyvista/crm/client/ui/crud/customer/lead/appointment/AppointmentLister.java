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
package com.propertyvista.crm.client.ui.crud.customer.lead.appointment;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.domain.tenant.lead.Appointment;

public class AppointmentLister extends AbstractLister<Appointment> {

    public AppointmentLister() {
        super(Appointment.class, true);
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().date()).build(),
                new MemberColumnDescriptor.Builder(proto().time()).build(),
                new MemberColumnDescriptor.Builder(proto().agent()).build(),
                new MemberColumnDescriptor.Builder(proto().phone()).build(),
                new MemberColumnDescriptor.Builder(proto().email()).build(),
                new MemberColumnDescriptor.Builder(proto().status()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().date(), false), new Sort(proto().time(), false));
    }
}
