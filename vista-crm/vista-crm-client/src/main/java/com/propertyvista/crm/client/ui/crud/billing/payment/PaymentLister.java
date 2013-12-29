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
package com.propertyvista.crm.client.ui.crud.billing.payment;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.prime.lister.AbstractLister;

import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentLister extends AbstractLister<PaymentRecordDTO> {

    public PaymentLister() {
        super(PaymentRecordDTO.class, true);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().id()).build(),
            new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().customerId()).build(),
            new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().person().name()).searchable(false).build(),
            new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().person().name().firstName()).searchableOnly().build(),
            new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().leaseParticipant().customer().person().name().lastName()).searchableOnly().build(),
            new MemberColumnDescriptor.Builder(proto().leaseTermParticipant().role()).build(),
            new MemberColumnDescriptor.Builder(proto().amount()).build(),
            new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).build(),
            new MemberColumnDescriptor.Builder(proto().createdDate()).build(),
            new MemberColumnDescriptor.Builder(proto().receivedDate()).build(),
            new MemberColumnDescriptor.Builder(proto().lastStatusChangeDate()).build(),
            new MemberColumnDescriptor.Builder(proto().targetDate()).build(),
            new MemberColumnDescriptor.Builder(proto().paymentStatus()).build()
        );//@formatter:on
    }

    @Override
    public List<Sort> getDefaultSorting() {
        return Arrays.asList(new Sort(proto().id(), true));
    }
}
