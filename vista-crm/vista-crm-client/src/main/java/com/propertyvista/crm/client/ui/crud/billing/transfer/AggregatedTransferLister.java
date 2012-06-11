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
package com.propertyvista.crm.client.ui.crud.billing.transfer;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.domain.financial.AggregatedTransfer;

public class AggregatedTransferLister extends ListerBase<AggregatedTransfer> {

    public AggregatedTransferLister() {
        super(AggregatedTransfer.class, false);

        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().paymentDate()).build(), 
                new MemberColumnDescriptor.Builder(proto().status()).build(),
                
                new MemberColumnDescriptor.Builder(proto().merchantAccount()).build(),
                
                new MemberColumnDescriptor.Builder(proto().grossPaymentCount()).build(),
                new MemberColumnDescriptor.Builder(proto().grossPaymentAmount()).build(),
                
                new MemberColumnDescriptor.Builder(proto().rejectItemsAmount()).build(),
                new MemberColumnDescriptor.Builder(proto().rejectItemsFee()).build(),
                new MemberColumnDescriptor.Builder(proto().rejectItemsCount()).build(),
                
                new MemberColumnDescriptor.Builder(proto().returnItemsAmount()).build(),
                new MemberColumnDescriptor.Builder(proto().returnItemsFee()).build(),
                new MemberColumnDescriptor.Builder(proto().returnItemsCount()).build()
        );//@formatter:on
    }
}
