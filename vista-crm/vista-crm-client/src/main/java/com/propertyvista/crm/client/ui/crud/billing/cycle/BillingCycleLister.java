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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.dto.billing.BillingCycleDTO;

public class BillingCycleLister extends ListerBase<BillingCycleDTO> {

    public BillingCycleLister() {
        super(BillingCycleDTO.class, false);
        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().billingType()).build(),
            new MemberColumnDescriptor.Builder(proto().billingCycleStartDate()).build(),
            new MemberColumnDescriptor.Builder(proto().billingCycleEndDate()).build(),
            new MemberColumnDescriptor.Builder(proto().executionTargetDate()).build(),
            new MemberColumnDescriptor.Builder(proto().notRun()).build(),      
            new MemberColumnDescriptor.Builder(proto().notConfirmed()).build(),        
            new MemberColumnDescriptor.Builder(proto().failed()).build(), 
            new MemberColumnDescriptor.Builder(proto().rejected()).build(),  
            new MemberColumnDescriptor.Builder(proto().confirmed()).build(), 
            new MemberColumnDescriptor.Builder(proto().total()).build()           
        );//@formatter:on
    }
}
