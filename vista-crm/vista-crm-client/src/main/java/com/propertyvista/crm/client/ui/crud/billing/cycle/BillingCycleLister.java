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
package com.propertyvista.crm.client.ui.crud.billing.cycle;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;

public class BillingCycleLister extends AbstractLister<BillingCycleDTO> {

    public BillingCycleLister() {
        super(BillingCycleDTO.class, false);
        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().building(), false).build(),
            new MemberColumnDescriptor.Builder(proto().billingType()).build(),
            new MemberColumnDescriptor.Builder(proto().billingCycleStartDate()).build(),
            new MemberColumnDescriptor.Builder(proto().billingCycleEndDate()).build(),
            new MemberColumnDescriptor.Builder(proto().targetBillExecutionDate()).build(),
            new MemberColumnDescriptor.Builder(proto().notRun()).build(),      
            new MemberColumnDescriptor.Builder(proto().stats().notConfirmed()).build(),        
            new MemberColumnDescriptor.Builder(proto().stats().failed()).build(), 
            new MemberColumnDescriptor.Builder(proto().stats().rejected()).build(),  
            new MemberColumnDescriptor.Builder(proto().stats().confirmed()).build(), 
            new MemberColumnDescriptor.Builder(proto().total()).build(),
            new MemberColumnDescriptor.Builder(proto().actualAutopayExecutionDate()).build(),
            new MemberColumnDescriptor.Builder(proto().pads()).sortable(false).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().targetAutopayExecutionDate()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().billingCycleStartDate(), true));
    }
}
