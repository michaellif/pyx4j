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
package com.propertyvista.crm.client.ui.crud.billing.bill;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;

public class BillLister extends ListerBase<BillDataDTO> {

    public BillLister() {
        super(BillDataDTO.class, false);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().bill().billType()).build(),

            new MemberColumnDescriptor.Builder(proto().bill().executionDate()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().billingPeriodStartDate()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().billingPeriodEndDate()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().dueDate()).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().currentAmount()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().taxes()).build(),
            new MemberColumnDescriptor.Builder(proto().bill().totalDueAmount()).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().billStatus()).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().balanceForwardAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().paymentReceivedAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().depositRefundAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().immediateAccountAdjustments(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().nsfCharges(),false).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().pendingAccountAdjustments(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().depositAmount(),false).build(),
            
            new MemberColumnDescriptor.Builder(proto().bill().pastDueAmount(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().serviceCharge(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().recurringFeatureCharges(),false).build(),
            new MemberColumnDescriptor.Builder(proto().bill().oneTimeFeatureCharges(),false).build()
            
        );//@formatter:on
    }
}
