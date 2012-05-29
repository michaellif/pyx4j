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

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.PaymentRecordDTO;

public class PaymentLister extends ListerBase<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentLister.class);

    public PaymentLister() {
        super(PaymentRecordDTO.class, false, true);
        getDataTablePanel().getDataTable().setHasCheckboxColumn(false);

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().id()).build(),
            new MemberColumnDescriptor.Builder(proto().leaseParticipant().customer().customerId()).build(),
            new MemberColumnDescriptor.Builder(proto().leaseParticipant().customer().person().name()).build(),
            new MemberColumnDescriptor.Builder(proto().leaseParticipant().role()).build(),
            new MemberColumnDescriptor.Builder(proto().amount()).build(),
            new MemberColumnDescriptor.Builder(proto().paymentMethod().type()).build(),
            new MemberColumnDescriptor.Builder(proto().createdDate()).build(),
            new MemberColumnDescriptor.Builder(proto().receivedDate()).build(),
            new MemberColumnDescriptor.Builder(proto().lastStatusChangeDate()).build(),
            new MemberColumnDescriptor.Builder(proto().targetDate()).build(),
            new MemberColumnDescriptor.Builder(proto().paymentStatus()).build()
        );//@formatter:on
    }
}
